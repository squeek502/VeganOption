package squeek.veganoption.blocks.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.content.modules.Basin;
import squeek.veganoption.helpers.FluidContainerHelper;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.WorldHelper;

import java.util.List;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class TileEntityBasin extends TileEntity implements ITickable
{
	public FluidTank fluidTank = new BasinTank(Fluid.BUCKET_VOLUME);
	protected boolean isPowered = false;
	protected boolean fluidConsumeStopped = true;
	protected int ticksUntilNextFluidConsume = FLUID_CONSUME_TICK_PERIOD;
	protected int ticksUntilNextContainerFill = CONTAINER_FILL_TICK_PERIOD;

	public static int FLUID_CONSUME_TICK_PERIOD = MiscHelper.TICKS_PER_SEC;
	public static int CONTAINER_FILL_TICK_PERIOD = MiscHelper.TICKS_PER_SEC;

	/*
	 * Updating
	 */
	@Override
	public void update()
	{
		if (worldObj.isRemote)
			return;

		if (shouldConsumeFluid())
		{
			boolean didConsume = tryConsumeFluidAbove();
			if (didConsume)
				scheduleFluidConsume();
			else
				endFluidConsume();
		}
		else
			ticksUntilNextFluidConsume = Math.max(0, ticksUntilNextFluidConsume - 1);

		if (shouldFillContainers())
		{
			tryFillContainersInside();
			scheduleFillContainers();
		}
		else
			ticksUntilNextContainerFill = Math.max(0, ticksUntilNextContainerFill - 1);
	}

	/*
	 * Fluid container behavior
	 */
	public boolean couldFillContainers()
	{
		return isOpen() && fluidTank.getFluidAmount() > 0;
	}

	public boolean shouldFillContainers()
	{
		return couldFillContainers() && ticksUntilNextContainerFill <= 0;
	}

	public boolean tryFillContainersInside()
	{
		if (worldObj == null || worldObj.isRemote || !couldFillContainers())
			return false;

		List<EntityItem> entityItemsWithin = WorldHelper.getItemEntitiesWithin(worldObj, ((BlockBasin) Basin.basin).getInnerBoundingBox(worldObj, pos.getX(), pos.getY(), pos.getZ()));

		for (EntityItem entityItemWithin : entityItemsWithin)
		{
			if (!FluidContainerHelper.isEmptyContainer(entityItemWithin.getEntityItem()))
				continue;

			EntityItem entityItemToFill = entityItemWithin;
			ItemStack containerToFill = entityItemWithin.getEntityItem();
			FluidContainerHelper.drainHandlerIntoContainer(fluidTank, fluidTank.getFluid(), containerToFill);

			if (containerToFill.stackSize > 1 && !FluidContainerHelper.isEmptyContainer(containerToFill))
			{
				containerToFill.splitStack(1);
				entityItemToFill = new EntityItem(entityItemToFill.worldObj, entityItemToFill.posX, entityItemToFill.posY, entityItemToFill.posZ, containerToFill);
				entityItemToFill.setPickupDelay(10);
				entityItemToFill.worldObj.spawnEntityInWorld(entityItemToFill);
			}
			return true;
		}

		return false;
	}

	public void scheduleFillContainers(int ticksUntilContainerFill)
	{
		if (ticksUntilNextContainerFill == 0)
			ticksUntilNextContainerFill = ticksUntilContainerFill;
		else
			ticksUntilNextContainerFill = Math.min(ticksUntilNextContainerFill, ticksUntilContainerFill);
	}

	public void scheduleFillContainers()
	{
		scheduleFillContainers(CONTAINER_FILL_TICK_PERIOD);
	}

	/*
	 * Fluid consuming behavior
	 */
	public boolean couldConsumeFluid()
	{
		return isOpen() && fluidTank.getFluidAmount() != fluidTank.getCapacity();
	}

	public boolean shouldConsumeFluid()
	{
		return couldConsumeFluid() && !fluidConsumeStopped && ticksUntilNextFluidConsume <= 0;
	}

	public boolean tryConsumeFluidAbove()
	{
		if (worldObj == null || worldObj.isRemote || !couldConsumeFluid())
			return false;

		BlockPos blockPosAbove = pos.up();
		IBlockState stateAbove = worldObj.getBlockState(blockPosAbove);
		Fluid fluidAbove = FluidHelper.getFluidTypeOfBlock(stateAbove);

		if (fluidAbove == null)
			return false;

		FluidStack fluidToAdd = FluidHelper.consumeFluid(worldObj, blockPosAbove, fluidAbove, fluidTank.getCapacity() - fluidTank.getFluidAmount());

		if (fluidToAdd == null || !fluidTank.canFillFluidType(fluidToAdd))
			return false;

		fluidTank.fill(fluidToAdd, true);
		return true;
	}

	public void scheduleFluidConsume(int ticksUntilFluidConsume)
	{
		if (ticksUntilFluidConsume == 0)
			tryConsumeFluidAbove();
		else if (ticksUntilNextFluidConsume == 0)
			ticksUntilNextFluidConsume = ticksUntilFluidConsume;
		else
			ticksUntilNextFluidConsume = Math.min(ticksUntilNextFluidConsume, ticksUntilFluidConsume);

		fluidConsumeStopped = false;
	}

	public void scheduleFluidConsume()
	{
		scheduleFluidConsume(FLUID_CONSUME_TICK_PERIOD);
	}

	public void endFluidConsume()
	{
		fluidConsumeStopped = true;
	}

	/*
	 * Open/closed state
	 */
	public boolean isOpen()
	{
		return isPowered();
	}

	public boolean isClosed()
	{
		return !isOpen();
	}

	public void onOpen()
	{
		scheduleFluidConsume();
	}

	public void onClose()
	{
		endFluidConsume();
	}

	/*
	 * Right Click Handling
	 */
	public boolean onBlockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		ItemStack heldItem = player.getHeldItem(hand);
		if (FluidContainerHelper.isFluidContainer(heldItem))
		{
			// TODO: This would be better moved directly into FluidContainerHelper
			net.minecraftforge.fluids.capability.IFluidHandler containerCap = FluidUtil.getFluidHandler(heldItem);
			if (containerCap == null)
				return false;
			for (IFluidTankProperties tankProp : containerCap.getTankProperties())
			{
				FluidStack containerFluid = tankProp.getContents();
				if (containerFluid != null && fluidTank.fill(containerFluid, false) == containerFluid.amount)
				{
					FluidContainerHelper.drainContainerIntoHandler(heldItem, fluidTank);
					return true;
				} else if (fluidTank.getFluidAmount() > 0)
				{
					FluidContainerHelper.drainHandlerIntoContainer(fluidTank, fluidTank.getFluid(), heldItem);
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Redstone Power Handling
	 */
	public void setPowered(boolean isPowered)
	{
		if (isPowered != isPowered())
		{
			this.isPowered = isPowered;

			if (isPowered)
				onPowered();
			else
				onUnpowered();

			if (worldObj != null)
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 0);
		}
	}

	public boolean isPowered()
	{
		return isPowered;
	}

	public void onPowered()
	{
		if (worldObj != null)
			worldObj.notifyNeighborsOfStateChange(pos, Basin.basin);

		onOpen();
	}

	public void onUnpowered()
	{
		if (worldObj != null)
			worldObj.notifyNeighborsOfStateChange(pos, Basin.basin);

		onClose();
	}

	/*
	 * Fluid Handling
	 */
	public void onFluidLevelChanged(IFluidTank tank, FluidStack fluidDelta)
	{
		if (worldObj != null)
		{
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 0);
			worldObj.notifyNeighborsOfStateChange(pos, Basin.basin);
			scheduleFluidConsume();
		}
	}

	/*
	 * Synced data
	 */
	public void readSyncedNBT(NBTTagCompound compound)
	{
		if (compound.hasKey("Fluid"))
			fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("Fluid")));
		else
			fluidTank.setFluid(null);

		setPowered(compound.getBoolean("Powered"));
	}

	public void writeSyncedNBT(NBTTagCompound compound)
	{
		if (fluidTank.getFluid() != null)
		{
			NBTTagCompound fluidTag = new NBTTagCompound();
			fluidTank.getFluid().writeToNBT(fluidTag);
			compound.setTag("Fluid", fluidTag);
		}

		if (isPowered())
		{
			compound.setBoolean("Powered", isPowered());
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound tag = super.getUpdateTag();
		writeSyncedNBT(tag);
		return tag;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		readSyncedNBT(tag);
	}

	/*
			 * Save data
			 */
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		readSyncedNBT(compound);

		if (compound.hasKey("NextConsume"))
		{
			scheduleFluidConsume(compound.getInteger("NextConsume"));
		}
		else
		{
			endFluidConsume();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);

		writeSyncedNBT(compound);

		if (!fluidConsumeStopped)
		{
			compound.setInteger("NextConsume", ticksUntilNextFluidConsume);
		}

		return compound;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == FLUID_HANDLER_CAPABILITY ? (T) fluidTank : super.getCapability(capability, facing);
	}

	private class BasinTank extends FluidTank
	{
		public BasinTank(int capacity)
		{
			super(capacity);
		}

		@Override
		public int fill(FluidStack resource, boolean doFill)
		{
			if (resource == null || !canFillFluidType(resource))
				return 0;

			int amountFilled = super.fill(resource, doFill);

			if (doFill && amountFilled > 0)
				onFluidLevelChanged(this, new FluidStack(resource.getFluid(), amountFilled));

			return amountFilled;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain)
		{
			if (resource == null || !canDrainFluidType(resource))
				return null;

			return drain(resource.amount, doDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain)
		{
			FluidStack drainedStack = super.drain(maxDrain, doDrain);

			if (doDrain && drainedStack != null && drainedStack.amount > 0)
				onFluidLevelChanged(this, drainedStack.copy());

			return drainedStack;
		}

		@Override
		public boolean canFillFluidType(FluidStack fluid)
		{
			return this.fluid == null || this.fluid.getFluid() == null || this.fluid.getFluid() == fluid.getFluid();
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluid)
		{
			return this.fluid != null && this.fluid.getFluid() != null && this.fluid.getFluid() == fluid.getFluid();
		}
	}
}
