package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.content.modules.Syrup;
import squeek.veganoption.helpers.BlockHelper;

public class SpoutBlock extends HorizontalDirectionalBlock
{
	public static final BooleanProperty HAS_BUCKET = BooleanProperty.create("has_bucket");
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final int MIN_LEVEL = 0;
	public static final int MAX_LEVEL = 4;
	public static final IntegerProperty LEVEL = IntegerProperty.create("level", MIN_LEVEL, MAX_LEVEL);
	private static final VoxelShape SPOUT_SOUTH_AABB = Block.box(6.0, 11.0, 12.0, 10.0, 15.0, 16.0);
	private static final VoxelShape SPOUT_NORTH_AABB = Block.box(6.0, 11.0, 0.0, 10.0, 15.0, 4.0);
	private static final VoxelShape SPOUT_WEST_AABB = Block.box(0.0, 11.0, 6.0, 4.0, 15.0, 10.0);
	private static final VoxelShape SPOUT_EAST_AABB = Block.box(12.0, 11.0, 6.0, 16.0, 15.0, 10.0);
	private static final VoxelShape BUCKET_SOUTH_AABB = Block.box(3.0, 2.0, 4.0, 13.0, 11.0, 14.0);
	private static final VoxelShape BUCKET_NORTH_AABB = Block.box(3.0, 2.0, 2.0, 13.0, 11.0, 12.0);
	private static final VoxelShape BUCKET_WEST_AABB = Block.box(2.0, 2.0, 3.0, 12.0, 11.0, 13.0);
	private static final VoxelShape BUCKET_EAST_AABB = Block.box(4.0, 2.0, 3.0, 14.0, 11.0, 13.0);
	private static final VoxelShape WITH_BUCKET_NORTH = Shapes.or(SPOUT_NORTH_AABB, BUCKET_NORTH_AABB);
	private static final VoxelShape WITH_BUCKET_SOUTH = Shapes.or(SPOUT_SOUTH_AABB, BUCKET_SOUTH_AABB);
	private static final VoxelShape WITH_BUCKET_EAST = Shapes.or(SPOUT_EAST_AABB, BUCKET_EAST_AABB);
	private static final VoxelShape WITH_BUCKET_WEST = Shapes.or(SPOUT_WEST_AABB, BUCKET_WEST_AABB);

	public SpoutBlock()
	{
		super(
			Block.Properties.of()
				.strength(0.5f)
				.sound(SoundType.METAL)
				.mapColor(MapColor.METAL)
				.noOcclusion()
				.randomTicks()
				.pushReaction(PushReaction.DESTROY));
		registerDefaultState(getStateDefinition().any().setValue(HAS_BUCKET, false).setValue(FACING, Direction.NORTH).setValue(LEVEL, 0));
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
	{
		BlockState placedOnState = reader.getBlockState(pos.relative(state.getValue(FACING)));
		return placedOnState.is(Blocks.BIRCH_LOG) && placedOnState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		ItemStack stackInHand = player.getItemInHand(hand);
		int sapLevel = state.getValue(LEVEL);
		if (state.getValue(HAS_BUCKET) && (sapLevel == MAX_LEVEL || sapLevel == MIN_LEVEL))
		{
			if (!level.isClientSide())
			{
				Item bucketItem = sapLevel == MAX_LEVEL ? Syrup.sapBucket.get() : Items.BUCKET;
				Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.getRandom(), 0.7F);
				ItemEntity sapBucket = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), new ItemStack(bucketItem));
				sapBucket.setDefaultPickUpDelay();
				level.addFreshEntity(sapBucket);
				level.setBlockAndUpdate(pos, state.setValue(HAS_BUCKET, false).setValue(LEVEL, MIN_LEVEL));
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}

		if (stackInHand.is(Syrup.sapBucket.get()))
		{
			if (!level.isClientSide())
			{
				level.setBlockAndUpdate(pos, state.setValue(HAS_BUCKET, true).setValue(LEVEL, MAX_LEVEL));
				stackInHand.shrink(1);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		else if (stackInHand.is(Items.BUCKET))
		{
			if (!level.isClientSide())
			{
				level.setBlockAndUpdate(pos, state.setValue(HAS_BUCKET, true).setValue(LEVEL, MIN_LEVEL));
				stackInHand.shrink(1);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return InteractionResult.PASS;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		if (!canSurvive(state, level, pos))
		{
			level.destroyBlock(pos, true);
			return;
		}

		if (state.getValue(LEVEL) == MAX_LEVEL || !state.getValue(HAS_BUCKET))
			return;
		if (random.nextFloat() <= 0.15f)
		{
			if (BlockHelper.isValidTree(level, pos.relative(state.getValue(FACING)), Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES))
			{
				level.setBlockAndUpdate(pos, state.cycle(LEVEL));
			}
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		for (Direction direction : context.getNearestLookingDirections())
		{
			if (direction.getAxis() == Direction.Axis.Y)
				continue;
			BlockState state = defaultBlockState().setValue(FACING, direction);
			if (state.canSurvive(context.getLevel(), context.getClickedPos()))
				return state;
		}
		return null;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
	{
		if (state.getValue(HAS_BUCKET))
		{
			return switch (state.getValue(FACING))
			{
				case NORTH -> WITH_BUCKET_NORTH;
				case SOUTH -> WITH_BUCKET_SOUTH;
				case EAST -> WITH_BUCKET_EAST;
				case WEST -> WITH_BUCKET_WEST;
				default -> super.getShape(state, getter, pos, context);
			};
		}
		else
		{
			return switch (state.getValue(FACING))
			{
				case NORTH -> SPOUT_NORTH_AABB;
				case SOUTH -> SPOUT_SOUTH_AABB;
				case EAST -> SPOUT_EAST_AABB;
				case WEST -> SPOUT_WEST_AABB;
				default -> super.getShape(state, getter, pos, context);
			};
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(HAS_BUCKET);
		builder.add(FACING);
		builder.add(LEVEL);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		return state.getValue(HAS_BUCKET) ? state.getValue(LEVEL) : 0;
	}
}
