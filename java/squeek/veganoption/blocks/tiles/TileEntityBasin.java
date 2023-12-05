package squeek.veganoption.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.content.modules.Basin;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.WorldHelper;

import java.util.List;

public class TileEntityBasin extends BlockEntity
{
	public FluidTank fluidTank = new BasinTank(FluidType.BUCKET_VOLUME);
	protected boolean isPowered = false;
	protected boolean fluidConsumeStopped = true;
	protected int ticksUntilNextFluidConsume = FLUID_CONSUME_TICK_PERIOD;
	protected int ticksUntilNextContainerFill = CONTAINER_FILL_TICK_PERIOD;

	public static int FLUID_CONSUME_TICK_PERIOD = MiscHelper.TICKS_PER_SEC;
	public static int CONTAINER_FILL_TICK_PERIOD = MiscHelper.TICKS_PER_SEC;



	public TileEntityBasin(BlockPos pos, BlockState state)
	{
		super(Basin.basinType.get(), pos, state);
	}

	/*
	 * Updating
	 */
	public static <T extends BlockEntity> void onTick(Level level, BlockPos blockPos, BlockState blockState, T t)
	{
		if (EffectiveSide.get().isClient() || !(t instanceof TileEntityBasin te))
			return;

		if (te.shouldConsumeFluid())
		{
			boolean didConsume = te.tryConsumeFluidAbove();
			if (didConsume)
				te.scheduleFluidConsume();
			else
				te.endFluidConsume();
		}
		else
			te.ticksUntilNextFluidConsume = Math.max(0, te.ticksUntilNextFluidConsume - 1);

		if (te.shouldFillContainers())
		{
			te.tryFillContainersInside();
			te.scheduleFillContainers();
		}
		else
			te.ticksUntilNextContainerFill = Math.max(0, te.ticksUntilNextContainerFill - 1);
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
		if (level == null || EffectiveSide.get().isClient() || !couldFillContainers())
			return false;

		List<ItemEntity> entityItemsWithin = WorldHelper.getItemEntitiesWithin(level, ((BlockBasin) Basin.basin.get()).getInnerBoundingBox(getBlockPos()));

		for (ItemEntity entityItemWithin : entityItemsWithin)
		{
			if (FluidUtil.getFluidContained(entityItemWithin.getItem()).isPresent())
				continue;

			ItemEntity entityItemToFill = entityItemWithin;
			ItemStack containerToFill = entityItemWithin.getItem().split(1);
			IFluidHandlerItem fluidHandlerToFill = FluidUtil.getFluidHandler(containerToFill).orElse(null);
			FluidUtil.tryFluidTransfer(fluidHandlerToFill, fluidTank, fluidTank.getFluid(), true);

			if (FluidUtil.getFluidContained(containerToFill).isPresent())
			{
				entityItemToFill = new ItemEntity(entityItemToFill.level(), entityItemToFill.blockPosition().getX(), entityItemToFill.blockPosition().getY(), entityItemToFill.blockPosition().getZ(), containerToFill);
				entityItemToFill.setPickUpDelay(10);
				level.addFreshEntity(entityItemToFill);
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
		if (level == null || EffectiveSide.get().isClient() || !couldConsumeFluid())
			return false;

		BlockPos blockPosAbove = worldPosition.above();
		BlockState stateAbove = level.getBlockState(blockPosAbove);
		Fluid fluidAbove = FluidHelper.getFluidTypeOfBlock(stateAbove);

		if (fluidAbove == null)
			return false;

		FluidStack fluidToAdd = FluidHelper.consumeFluid(level, blockPosAbove, fluidAbove, fluidTank.getCapacity() - fluidTank.getFluidAmount());

		if (fluidToAdd == null || !fluidTank.isFluidValid(fluidToAdd))
			return false;

		fluidTank.fill(fluidToAdd, IFluidHandler.FluidAction.EXECUTE);
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
	public boolean onBlockActivated(Player player, InteractionHand hand, Direction side, Vec3 location)
	{
		return FluidUtil.interactWithFluidHandler(player, hand, fluidTank);
	}

	/**
	 * Attempts to add an item to the player's inventory in the following order:
	 * 1. Their current hand if there is no held item, or the held item has a stack size of 0
	 * 2. The first open slot in their inventory
	 * 3. Dropped in the world, if there is no free slot in the inventory.
	 */
	private void tryAddItemToInventory(Player player, InteractionHand hand, ItemStack newItem)
	{
		ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.isEmpty())
		{
			player.setItemInHand(hand, newItem);
			return;
		}
		if (!player.addItem(newItem))
			player.drop(newItem, true);
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

			if (level != null)
			{
				level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockBasin.IS_OPEN, isPowered), 0);
				level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 0);
			}
		}
	}

	public boolean isPowered()
	{
		return isPowered;
	}

	public void onPowered()
	{
		if (level != null)
			level.updateNeighborsAt(worldPosition, Basin.basin.get());

		onOpen();
	}

	public void onUnpowered()
	{
		if (level != null)
			level.updateNeighborsAt(worldPosition, Basin.basin.get());

		onClose();
	}

	/*
	 * Fluid Handling
	 */
	public void onFluidLevelChanged(IFluidTank tank, FluidStack fluidDelta)
	{
		if (level != null)
		{
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 0);;
			level.updateNeighborsAt(worldPosition, Basin.basin.get());
			scheduleFluidConsume();
		}
	}

	/*
	 * Synced data
	 */
	public void readSyncedNBT(CompoundTag compound)
	{
		if (compound.contains("Fluid"))
			fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(compound.getCompound("Fluid")));
		else
			fluidTank.setFluid(null);

		setPowered(compound.getBoolean("Powered"));
	}

	public void writeSyncedNBT(CompoundTag compound)
	{
		if (!fluidTank.getFluid().isEmpty())
		{
			CompoundTag fluidTag = new CompoundTag();
			fluidTank.getFluid().writeToNBT(fluidTag);
			compound.put("Fluid", fluidTag);
		}

		if (isPowered())
		{
			compound.putBoolean("Powered", isPowered());
		}
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
	{
		handleUpdateTag(pkt.getTag());
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		writeSyncedNBT(tag);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag)
	{
		readSyncedNBT(tag);
	}

	/*
			 * Save data
			 */
	@Override
	public void deserializeNBT(CompoundTag compound)
	{
		super.deserializeNBT(compound);

		readSyncedNBT(compound);

		if (compound.contains("NextConsume"))
		{
			scheduleFluidConsume(compound.getInt("NextConsume"));
		}
		else
		{
			endFluidConsume();
		}
	}

	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag compound = super.serializeNBT();

		writeSyncedNBT(compound);

		if (!fluidConsumeStopped)
		{
			compound.putInt("NextConsume", ticksUntilNextFluidConsume);
		}

		return compound;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing)
	{
		return capability == Capabilities.FLUID_HANDLER ? LazyOptional.of(() -> (T) fluidTank) : super.getCapability(capability, facing);
	}



	private class BasinTank extends FluidTank
	{
		public BasinTank(int capacity)
		{
			super(capacity);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			if (resource.isEmpty() || !isFluidValid(resource))
				return 0;

			int amountFilled = super.fill(resource, action);

			if (action.execute() && amountFilled > 0)
				onFluidLevelChanged(this, new FluidStack(resource.getFluid(), amountFilled));

			return amountFilled;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action)
		{
			if (resource.isEmpty() || !resource.isFluidEqual(fluid))
				return null;

			return drain(resource.getAmount(), action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action)
		{
			FluidStack drainedStack = super.drain(maxDrain, action);

			if (action.execute() && !drainedStack.isEmpty())
				onFluidLevelChanged(this, drainedStack.copy());

			return drainedStack;
		}

		@Override
		public boolean isFluidValid(FluidStack fluid)
		{
			return this.fluid.getFluid() == null || this.fluid.getFluid() == fluid.getFluid();
		}
	}
}
