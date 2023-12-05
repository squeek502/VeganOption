package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.gui.ComposterMenu;
import squeek.veganoption.helpers.LangHelper;

public class BlockComposter extends HorizontalDirectionalBlock implements EntityBlock
{
	public static final VoxelShape SHAPE = Shapes.box(1, 0, 1, 15, 14, 15);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public BlockComposter()
	{
		super(BlockBehaviour.Properties.of()
			.strength(2.5f)
			.sound(SoundType.WOOD)
			.mapColor(MapColor.WOOD)
			.ignitedByLava()
			.pushReaction(PushReaction.DESTROY));
		registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof TileEntityComposter composter)
		{
			return composter.onActivated(player, state) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}
		return super.use(state, level, pos, player, hand, hit);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
	{
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof TileEntityComposter composter)
		{
			composter.onBlockBroken();
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile != null && tile instanceof TileEntityComposter)
		{
			return ((TileEntityComposter) tile).getComparatorSignalStrength();
		}
		return super.getAnalogOutputSignal(state, level, pos);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileEntityComposter(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return type == Composting.composterEntityType.get() ? TileEntityComposter::onTick : null;
	}

	@Nullable
	@Override
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
	{
		return new MenuProvider() {
			@Override
			public Component getDisplayName()
			{
				return Component.translatable(LangHelper.prependModId("block.composter.name"));
			}

			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int containerID, Inventory playerInv, Player player)
			{
				return new ComposterMenu(containerID, playerInv, pos);
			}
		};
	}
}
