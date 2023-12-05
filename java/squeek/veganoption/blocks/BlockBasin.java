package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.content.modules.Basin;

import static squeek.veganoption.helpers.WorldHelper.FULL_BLOCK_AABB;

public class BlockBasin extends Block implements EntityBlock
{
	public static final BooleanProperty IS_OPEN = BooleanProperty.create("is_open");
	public static final double SIDE_WIDTH = 0.125D;
	private static final VoxelShape OPEN_SHAPE = Shapes.join(
		Shapes.block(),
		Shapes.box(2, 2, 2, 14, 14, 14),
		BooleanOp.ONLY_FIRST
	);

	public BlockBasin(Properties properties)
	{
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(IS_OPEN, false));
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(IS_OPEN);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileEntityBasin(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return type == Basin.basinType.get() ? TileEntityBasin::onTick : null;
	}

	/*
	 * Events
	 */
	private void update(Level level, BlockPos pos)
	{
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TileEntityBasin)
		{
			((TileEntityBasin) be).setPowered(level.hasNeighborSignal(pos));
			((TileEntityBasin) be).scheduleFluidConsume();
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos myPos, Block block, BlockPos pos2, boolean unknown)
	{
		super.neighborChanged(state, level, myPos, block, pos2, unknown);
		update(level, myPos);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state2, boolean unknown)
	{
		super.onPlace(state, level, pos, state2, unknown);
		update(level, pos);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TileEntityBasin)
			return ((TileEntityBasin) be).onBlockActivated(player, hand, hit.getDirection(), hit.getLocation()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		return InteractionResult.PASS;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context)
	{
		return state.getValue(IS_OPEN) ? OPEN_SHAPE : Shapes.block();
	}

	public AABB getInnerBoundingBox(BlockPos pos)
	{
		return getInnerBoundingBox(pos.getX(), pos.getY(), pos.getZ());
	}

	public AABB getInnerBoundingBox(int x, int y, int z)
	{
		return new AABB(x + FULL_BLOCK_AABB.minX + SIDE_WIDTH, y + FULL_BLOCK_AABB.minY + SIDE_WIDTH, z + FULL_BLOCK_AABB.minZ + SIDE_WIDTH,
						x + FULL_BLOCK_AABB.maxX - SIDE_WIDTH, y + FULL_BLOCK_AABB.maxY - SIDE_WIDTH, z + FULL_BLOCK_AABB.maxZ - SIDE_WIDTH);
	}
}
