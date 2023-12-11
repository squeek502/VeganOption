package squeek.veganoption.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.*;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
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
	public static <T extends BlockEntity> void onServerTick(Level level, BlockPos blockPos, BlockState blockState, T t)
	{
		if (!(t instanceof TileEntityBasin te))
			return;

		if (te.shouldConsumeFluid())
		{
			boolean didConsume = te.tryConsumeFluidAbove();
			if (didConsume)
				te.scheduleFluidConsume();
			else
				te.endFluidConsume();
			te.markDirty();
		}
		else
			te.ticksUntilNextFluidConsume = Math.max(0, te.ticksUntilNextFluidConsume - 1);

		if (te.shouldFillContainers())
		{
			if (te.tryFillContainersInside())
				te.markDirty();
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
		if (level == null || !couldFillContainers())
			return false;

		List<ItemEntity> entityItemsWithin = WorldHelper.getItemEntitiesWithin(level, ((BlockBasin) Basin.basin.get()).getInnerBoundingBox(getBlockPos()));

		for (ItemEntity entityItemWithin : entityItemsWithin)
		{
			// We have to copy the stack in order to prevent invalid/failed items from disappearing
			ItemStack containerToFill = entityItemWithin.getItem().copy().split(1);

			SoundEvent soundevent = fluidTank.getFluid().getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL);
			FluidActionResult result = FluidUtil.tryFillContainer(containerToFill, fluidTank, fluidTank.getFluidAmount(), null, true);

			if (result.isSuccess())
			{
				if (soundevent != null)
					level.playSound(null, worldPosition, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);

				ItemEntity newEntity = new ItemEntity(level, entityItemWithin.getX(), entityItemWithin.getY(), entityItemWithin.getZ(), result.getResult());
				newEntity.setPickUpDelay(10);
				level.addFreshEntity(newEntity);
				entityItemWithin.discard();
				return true;
			}
			return false;
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
		return level != null && isOpen(level.getBlockState(worldPosition));
	}

	public boolean isOpen(BlockState state)
	{
		return state.getValue(BlockBasin.IS_OPEN);
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
		if (level != null && FluidUtil.interactWithFluidHandler(player, hand, fluidTank))
		{
			markDirty();
			return true;
		}
		return false;
	}

	/*
	 * Fluid Handling
	 */
	public void onFluidLevelChanged(IFluidTank tank, FluidStack fluidDelta)
	{
		if (level != null)
		{
			scheduleFluidConsume();
			BlockState state = getBlockState();
			markDirty();
			level.sendBlockUpdated(worldPosition, state, state, 0);
			level.updateNeighborsAt(worldPosition, Basin.basin.get());
		}
	}

	/*
	 * Synced data
	 */
	public void readSyncedNBT(CompoundTag compound)
	{
		fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(compound.getCompound("Fluid")));
	}

	public void writeSyncedNBT(CompoundTag compound)
	{
		CompoundTag fluidTag = new CompoundTag();
		fluidTank.getFluid().writeToNBT(fluidTag);
		compound.put("Fluid", fluidTag);
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
	public void load(CompoundTag compound)
	{
		super.load(compound);

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
	public void saveAdditional(CompoundTag compound)
	{
		super.saveAdditional(compound);

		writeSyncedNBT(compound);

		if (!fluidConsumeStopped)
		{
			compound.putInt("NextConsume", ticksUntilNextFluidConsume);
		}
	}

	private void markDirty()
	{
		setChanged();
		if (level instanceof ServerLevel serverLevel)
			serverLevel.getChunkSource().blockChanged(worldPosition);
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
				return FluidStack.EMPTY;

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
			return this.fluid.isEmpty() || this.fluid.getFluid() == fluid.getFluid();
		}
	}
}
