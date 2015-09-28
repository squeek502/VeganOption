package squeek.veganoption.blocks.tiles;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.content.modules.Basin;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.FluidContainerHelper;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.WorldHelper;

// TODO: Handle partially filled frozen bubbles
// TODO: Convert open/closed to use metadata
public class TileEntityBasin extends TileEntity implements IFluidHandler
{
	protected FluidTank fluid = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	protected boolean isPowered = false;
	protected boolean fluidConsumeStopped = true;
	protected int ticksUntilNextFluidConsume = FLUID_CONSUME_TICK_PERIOD;
	protected int ticksUntilNextContainerFill = CONTAINER_FILL_TICK_PERIOD;
	protected boolean needsInit = true;

	public static int FLUID_CONSUME_TICK_PERIOD = MiscHelper.TICKS_PER_SEC;
	public static int CONTAINER_FILL_TICK_PERIOD = MiscHelper.TICKS_PER_SEC;

	/*
	 * Updating
	 */
	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (worldObj.isRemote)
			return;

		if (needsInit)
		{
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			needsInit = false;
		}

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

		List<EntityItem> entityItemsWithin = WorldHelper.getItemEntitiesWithin(worldObj, ((BlockBasin) Basin.basin).getInnerBoundingBox(worldObj, xCoord, yCoord, zCoord));

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
				entityItemToFill.delayBeforeCanPickup = 10;
				entityItemToFill.worldObj.spawnEntityInWorld(entityItemToFill);
			}

			entityItemToFill.setEntityItemStack(filledContainer);

			FluidStack filledWith = FluidContainerRegistry.getFluidForFilledItem(filledContainer);
			drain(ForgeDirection.UNKNOWN, filledWith.amount, true);
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

		BlockHelper.BlockPos blockPosAbove = new BlockHelper.BlockPos(worldObj, xCoord, yCoord + 1, zCoord);
		Block blockAbove = blockPosAbove.getBlock();
		Fluid fluidAbove = FluidHelper.getFluidTypeOfBlock(blockAbove);

		if (fluidAbove == null || !canFill(ForgeDirection.UP, fluidAbove))
			return false;

		FluidStack fluidToAdd = FluidHelper.consumeFluid(blockPosAbove, fluidAbove, fluid.getCapacity() - fluid.getFluidAmount());

		if (fluidToAdd == null)
			return false;

		fill(ForgeDirection.UP, fluidToAdd, true);
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
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		ItemStack heldItem = player.getHeldItem();
		if (heldItem != null && FluidContainerRegistry.isContainer(heldItem))
		{
			FluidStack containerFluid = FluidContainerRegistry.getFluidForFilledItem(heldItem);

			if (containerFluid != null && fill(ForgeDirection.UNKNOWN, containerFluid, false) == containerFluid.amount)
			{
				ItemStack emptyContainer = FluidContainerHelper.tryEmptyContainer(player, heldItem);

				if (emptyContainer == null)
					return false;

				fill(ForgeDirection.UNKNOWN, containerFluid, true);
			}
			else if (fluid.getFluidAmount() > 0)
			{
				ItemStack filledContainer = FluidContainerHelper.tryFillContainer(player, heldItem, fluid.getFluid());

				if (filledContainer == null)
					return false;

				drain(ForgeDirection.UNKNOWN, FluidContainerRegistry.getFluidForFilledItem(filledContainer).amount, true);
			}

			return true;
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
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public boolean isPowered()
	{
		return isPowered;
	}

	public void onPowered()
	{
		if (worldObj != null)
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, Basin.basin);

		onOpen();
	}

	public void onUnpowered()
	{
		if (worldObj != null)
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, Basin.basin);

		onClose();
	}

	/*
	 * Fluid Handling
	 */
	public void onFluidLevelChanged(IFluidTank tank, FluidStack fluidDelta)
	{
		if (worldObj != null)
		{
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, Basin.basin);
			scheduleFluidConsume();
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource == null || !canFill(from, resource.getFluid()))
			return 0;

		int amountFilled = fluid.fill(resource, doFill);

		if (doFill && amountFilled > 0)
			onFluidLevelChanged(fluid, new FluidStack(resource.getFluid(), amountFilled));

		return amountFilled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource == null || !canDrain(from, resource.getFluid()))
			return null;

		return drain(from, resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		FluidStack drainedStack = fluid.drain(maxDrain, doDrain);

		if (doDrain && drainedStack != null && drainedStack.amount > 0)
			onFluidLevelChanged(fluid, drainedStack.copy());

		return drainedStack;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return this.fluid.getFluid() == null || this.fluid.getFluid().getFluid() == fluid;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return this.fluid.getFluid() != null && this.fluid.getFluid().getFluid() == fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
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

		setPowered(compound.getBoolean("Powered"));
	}

	public void writeSyncedNBT(NBTTagCompound compound)
	{
		if (fluid.getFluid() != null)
		{
			NBTTagCompound fluidTag = new NBTTagCompound();
			fluid.getFluid().writeToNBT(fluidTag);
			compound.setTag("Fluid", fluidTag);
		}

		if (isPowered())
		{
			compound.setBoolean("Powered", isPowered());
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		readSyncedNBT(pkt.func_148857_g());
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound compound = new NBTTagCompound();
		writeSyncedNBT(compound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
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
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);

		writeSyncedNBT(compound);

		if (!fluidConsumeStopped)
		{
			compound.setInteger("NextConsume", ticksUntilNextFluidConsume);
		}
	}
}
