package squeek.veganoption.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.modules.Resin;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.MiscHelper;

import java.util.Map;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = ModInfo.MODID_LOWER)
public class DamagedSpruceLogBlock extends Block
{
	private static final MapCodec<DamagedSpruceLogBlock> CODEC = simpleCodec(p -> new DamagedSpruceLogBlock());
	private static final int TICKS_TO_HARDEN_MIN = MiscHelper.TICKS_PER_DAY;
	private static final int TICKS_TO_HARDEN_MAX = TICKS_TO_HARDEN_MIN + (3 * MiscHelper.TICKS_PER_DAY);
	public static final BooleanProperty NORTH_DAMAGED = BooleanProperty.create("north_damaged");
	public static final BooleanProperty SOUTH_DAMAGED = BooleanProperty.create("south_damaged");
	public static final BooleanProperty EAST_DAMAGED = BooleanProperty.create("east_damaged");
	public static final BooleanProperty WEST_DAMAGED = BooleanProperty.create("west_damaged");
	public static final BooleanProperty NORTH_HARDENED = BooleanProperty.create("north_hardened");
	public static final BooleanProperty SOUTH_HARDENED = BooleanProperty.create("south_hardened");
	public static final BooleanProperty EAST_HARDENED = BooleanProperty.create("east_hardened");
	public static final BooleanProperty WEST_HARDENED = BooleanProperty.create("west_hardened");
	public static final Map<Direction, BooleanProperty> DAMAGED_PROPERTY_BY_DIRECTION = ImmutableMap.of(Direction.NORTH, NORTH_DAMAGED, Direction.SOUTH, SOUTH_DAMAGED, Direction.EAST, EAST_DAMAGED, Direction.WEST, WEST_DAMAGED);
	public static final Map<Direction, BooleanProperty> HARDENED_PROPERTY_BY_DIRECTION = ImmutableMap.of(Direction.NORTH, NORTH_HARDENED, Direction.SOUTH, SOUTH_HARDENED, Direction.EAST, EAST_HARDENED, Direction.WEST, WEST_HARDENED);
	private static final Predicate<BlockState> LOG_CHECK = (state) -> state.is(Blocks.SPRUCE_LOG) || state.is(Resin.damagedSpruceLog.get());

	public DamagedSpruceLogBlock()
	{
		super(BlockBehaviour.Properties.of()
				  .mapColor(MapColor.PODZOL)
				  .instrument(NoteBlockInstrument.BASS)
				  .strength(2.0F)
				  .sound(SoundType.WOOD)
				  .ignitedByLava());
		registerDefaultState(
			getStateDefinition()
				.any()
				.setValue(NORTH_DAMAGED, false)
				.setValue(SOUTH_DAMAGED, false)
				.setValue(EAST_DAMAGED, false)
				.setValue(WEST_DAMAGED, false)
				.setValue(NORTH_HARDENED, false)
				.setValue(SOUTH_HARDENED, false)
				.setValue(EAST_HARDENED, false)
				.setValue(WEST_HARDENED, false));
	}

	@SubscribeEvent
	public static void damageSpruceLog(PlayerInteractEvent.RightClickBlock event)
	{
		Level level = event.getLevel();
		if (!level.isClientSide())
		{
			BlockHitResult hitResult = event.getHitVec();
			BlockPos pos = hitResult.getBlockPos();
			Player player = event.getEntity();
			InteractionHand hand = event.getHand();
			BlockState currentState = level.getBlockState(pos);
			if (currentState.is(Blocks.SPRUCE_LOG) && currentState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y)
				cutIntoSide(level, pos, Resin.damagedSpruceLog.get().defaultBlockState(), player, hand, hitResult, true);
		}
	}

	private static void cutIntoSide(Level level, BlockPos pos, BlockState baseState, Player player, InteractionHand hand, BlockHitResult hitResult, boolean swing)
	{
		ItemStack tool = player.getItemInHand(hand);
		if (tool.getItem() instanceof SwordItem)
		{
			if (swing)
				player.swing(hand, true);
			Direction side = hitResult.getDirection();
			if (BlockHelper.isValidTree(level, pos, LOG_CHECK, Blocks.SPRUCE_LEAVES) && side.getAxis() != Direction.Axis.Y)
			{
				boolean usedTool = false;
				BooleanProperty isSideDamaged = DAMAGED_PROPERTY_BY_DIRECTION.get(side);
				if (!baseState.getValue(isSideDamaged))
				{
					level.setBlockAndUpdate(pos, baseState.setValue(isSideDamaged, true));
					scheduleHardeningTick(level, pos);
					usedTool = true;
				}
				else
				{
					BooleanProperty isSideHardened = HARDENED_PROPERTY_BY_DIRECTION.get(side);
					if (baseState.getValue(isSideHardened))
					{
						dropResinInFront(level, pos, baseState, side);
						usedTool = true;
					}
				}
				if (usedTool)
				{
					tool.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
					level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1f, 1f);
				}
			}
		}
	}

	private static void scheduleHardeningTick(Level level, BlockPos pos)
	{
		if (BlockHelper.isValidTree(level, pos, LOG_CHECK, Blocks.SPRUCE_LEAVES))
			level.scheduleTick(pos, Resin.damagedSpruceLog.get(), getTickDelayForHardening(level, pos, level.getRandom()));
	}

	private static void scheduleDroppingTick(Level level, BlockPos pos)
	{
		if (BlockHelper.isValidTree(level, pos, LOG_CHECK, Blocks.SPRUCE_LEAVES))
			level.scheduleTick(pos, Resin.damagedSpruceLog.get(), getTickDelayForDropping(level.getRandom()));
	}

	/**
	 * The tick schedule is a random tick between the minimum and maximum, multiplied by how many injuries there are in the tree.
	 * Each existing injury causes the tree to heal more slowly.
	 * <br />
	 * Even number ticks are for hardening, and odd number ticks are for dropping.
	 */
	private static int getTickDelayForHardening(Level level, BlockPos pos, RandomSource random)
	{
		int injuriesInTree = BlockHelper.getMatchingBlocksInColumn(level, pos, (state) -> state.is(Resin.damagedSpruceLog.get()), (state) -> !state.is(Blocks.SPRUCE_LOG) && !state.is(Resin.damagedSpruceLog.get()), (state) -> {
			int injuries = 0;
			for (Direction side : DAMAGED_PROPERTY_BY_DIRECTION.keySet())
			{
				if (state.getValue(DAMAGED_PROPERTY_BY_DIRECTION.get(side)))
					injuries++;
			}
			return injuries;
		});
		int tickDelay = Math.max(1, injuriesInTree) * random.nextIntBetweenInclusive(TICKS_TO_HARDEN_MIN, TICKS_TO_HARDEN_MAX);
		return tickDelay % 2 == 0 ? tickDelay : tickDelay - 1;
	}

	/**
	 * The tick schedule for dropping the resin is between the maximum and twice the maximum, i.e., it will take twice as long to drop as it
	 * took to harden.
	 * <br />
	 * Even number ticks are for hardening, and odd number ticks are for dropping.
	 */
	private static int getTickDelayForDropping(RandomSource random)
	{
		int tick = random.nextIntBetweenInclusive(TICKS_TO_HARDEN_MAX, TICKS_TO_HARDEN_MAX * 2);
		return tick % 2 != 0 ? tick : tick - 1;
	}

	private static void dropResinInFront(Level level, BlockPos pos, BlockState state, Direction dir)
	{
		Position inFrontOfLog = pos.relative(dir).getCenter();
		ItemEntity resin = new ItemEntity(level, inFrontOfLog.x(), inFrontOfLog.y(), inFrontOfLog.z(), new ItemStack(Resin.resin.get()));
		resin.setDefaultPickUpDelay();
		level.addFreshEntity(resin);
		level.setBlockAndUpdate(pos, state.setValue(HARDENED_PROPERTY_BY_DIRECTION.get(dir), false));
		scheduleHardeningTick(level, pos);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		ItemStack sword = player.getItemInHand(hand);
		if (sword.getItem() instanceof SwordItem)
		{
			if (!level.isClientSide())
				cutIntoSide(level, pos, state, player, hand, hitResult, false);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return InteractionResult.PASS;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		// even ticks are for hardening
		if (level.getGameTime() % 2 == 0)
		{
			if (BlockHelper.isValidTree(level, pos, LOG_CHECK, Blocks.SPRUCE_LEAVES))
			{
				for (Direction dir : Direction.allShuffled(random))
				{
					if (dir.getAxis().isVertical())
						continue;
					if (state.getValue(DAMAGED_PROPERTY_BY_DIRECTION.get(dir)) && !state.getValue(HARDENED_PROPERTY_BY_DIRECTION.get(dir)))
					{
						level.setBlockAndUpdate(pos, state.setValue(HARDENED_PROPERTY_BY_DIRECTION.get(dir), true));
						scheduleDroppingTick(level, pos);
						break;
					}
				}
			}
		}
		// odd ticks are for dropping
		else
		{
			for (Direction dir : Direction.allShuffled(random))
			{
				if (dir.getAxis().isVertical())
					continue;
				if (state.getValue(DAMAGED_PROPERTY_BY_DIRECTION.get(dir)) && state.getValue(HARDENED_PROPERTY_BY_DIRECTION.get(dir)))
				{
					dropResinInFront(level, pos, state, dir);
					break;
				}
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH_DAMAGED, SOUTH_DAMAGED, EAST_DAMAGED, WEST_DAMAGED);
		builder.add(NORTH_HARDENED, SOUTH_HARDENED, EAST_HARDENED, WEST_HARDENED);
	}

	@Override
	protected MapCodec<? extends Block> codec()
	{
		return CODEC;
	}
}
