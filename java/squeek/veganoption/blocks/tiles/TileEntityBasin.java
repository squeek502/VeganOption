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
import net.minecraftforge.fluids.*;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.content.modules.Basin;
import squeek.veganoption.helpers.FluidContainerHelper;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.WorldHelper;

import java.util.List;

public class TileEntityBasin extends TileEntity implements IFluidHandler, ITickable
{
	protected FluidTank fluid = new FluidTank(Fluid.BUCKET_VOLUME);
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
		return isOpen() && fluid.getFluidAmount() > 0;
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
			if (!FluidContainerRegistry.isEmptyContainer(entityItemWithin.getEntityItem()))
				continue;

			EntityItem entityItemToFill = entityItemWithin;
			ItemStack containerToFill = entityItemWithin.getEntityItem();
			ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluid.getFluid(), containerToFill);

			if (filledContainer == null)
				continue;

			if (containerToFill.stackSize > 1)
			{
				containerToFill.splitStack(1);
				entityItemToFill = new EntityItem(entityItemToFill.worldObj, entityItemToFill.posX, entityItemToFill.posY, entityItemToFill.posZ, filledContainer);
				entityItemToFill.setPickupDelay(10);
				entityItemToFill.worldObj.spawnEntityInWorld(entityItemToFill);
			}

			entityItemToFill.setEntityItemStack(filledContainer);

			FluidStack filledWith = FluidContainerRegistry.getFluidForFilledItem(filledContainer);
			drain(EnumFacing.NORTH, filledWith.amount, true);
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
		return isOpen() && fluid.getFluidAmount() != fluid.getCapacity();
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

		if (fluidAbove == null || !canFill(EnumFacing.UP, fluidAbove))
			return false;

		FluidStack fluidToAdd = FluidHelper.consumeFluid(worldObj, blockPosAbove, fluidAbove, fluid.getCapacity() - fluid.getFluidAmount());

		if (fluidToAdd == null)
			return false;

		fill(EnumFacing.UP, fluidToAdd, true);
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
	public boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
		if (heldItem != null && FluidContainerRegistry.isContainer(heldItem))
		{
			FluidStack containerFluid = FluidContainerRegistry.getFluidForFilledItem(heldItem);

			if (containerFluid != null && fill(EnumFacing.NORTH, containerFluid, false) == containerFluid.amount)
			{
				ItemStack emptyContainer = FluidContainerHelper.tryEmptyContainer(player, heldItem);

				if (emptyContainer == null)
					return false;

				fill(EnumFacing.NORTH, containerFluid, true);
			}
			else if (fluid.getFluidAmount() > 0)
			{
				ItemStack filledContainer = FluidContainerHelper.tryFillContainer(player, heldItem, fluid.getFluid());

				if (filledContainer == null)
					return false;

				drain(EnumFacing.NORTH, FluidContainerRegistry.getFluidForFilledItem(filledContainer).amount, true);
			}

			return true;
		}
		return false;
	}

	/*
	 * Redstone Power Handling
	 */
	public void onPowerChange(boolean isPowered)
	{
		if (isPowered != isPowered())
		{
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
		return worldObj.getBlockState(pos).getValue(BlockBasin.IS_OPEN);
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

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill)
	{
		if (resource == null || !canFill(from, resource.getFluid()))
			return 0;

		int amountFilled = fluid.fill(resource, doFill);

		if (doFill && amountFilled > 0)
			onFluidLevelChanged(fluid, new FluidStack(resource.getFluid(), amountFilled));

		return amountFilled;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
	{
		if (resource == null || !canDrain(from, resource.getFluid()))
			return null;

		return drain(from, resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
	{
		FluidStack drainedStack = fluid.drain(maxDrain, doDrain);

		if (doDrain && drainedStack != null && drainedStack.amount > 0)
			onFluidLevelChanged(fluid, drainedStack.copy());

		return drainedStack;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid)
	{
		return this.fluid.getFluid() == null || this.fluid.getFluid().getFluid() == fluid;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid)
	{
		return this.fluid.getFluid() != null && this.fluid.getFluid().getFluid() == fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from)
	{
		return new FluidTankInfo[]{fluid.getInfo()};
	}

	/*
	 * Synced data
	 */
	public void readSyncedNBT(NBTTagCompound compound)
	{
		if (compound.hasKey("Fluid"))
			fluid.setFluid(FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("Fluid")));
		else
			fluid.setFluid(null);
	}

	public void writeSyncedNBT(NBTTagCompound compound)
	{
		if (fluid.getFluid() != null)
		{
			NBTTagCompound fluidTag = new NBTTagCompound();
			fluid.getFluid().writeToNBT(fluidTag);
			compound.setTag("Fluid", fluidTag);
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
}
