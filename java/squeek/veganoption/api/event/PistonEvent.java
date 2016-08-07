package squeek.veganoption.api.event;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Base class for all PistonEvent events.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class PistonEvent extends Event
{
	/**
	 * Fired when a piston tries to extend.
	 * Note: The piston is not guaranteed to extend after this event is fired.
	 *
	 * This event is fired in {@link BlockPistonBase#doMove(World, BlockPos, EnumFacing, boolean)}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class TryExtend extends PistonEvent
	{
		public final World world;

		public final BlockPos basePos;
		public final EnumFacing facing;

		public final BlockPos headPos;

		public TryExtend(World world, BlockPos basePos, EnumFacing facing)
		{
			this.world = world;
			this.basePos = basePos;
			this.facing = facing;
			this.headPos = basePos.offset(facing);
		}
	}

	/**
	 * Fired every time a piston retracts.
	 *
	 * This event is fired in {@link BlockPistonBase#doMove(World, BlockPos, EnumFacing, boolean)}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class Retract extends PistonEvent
	{
		public final World world;

		public final BlockPos basePos;
		public final EnumFacing facing;

		public final BlockPos headPos;

		public Retract(World world, BlockPos basePos, EnumFacing facing)
		{
			this.world = world;
			this.basePos = basePos;
			this.facing = facing;
			this.headPos = basePos.offset(facing);
		}
	}

	/**
	 * Fired whenever an item is being crushed (an EntityItem is inside a fully extended piston head).
	 *
	 * This event is fired in {@link EntityItem#onUpdate()}.<br>
	 * <br>
	 * This event is {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	@Cancelable
	public static class CrushItem extends PistonEvent
	{
		public final World world;

		public final BlockPos basePos;

		public final BlockPos headPos;

		public final EntityItem crushedItem;

		public CrushItem(EntityItem crushedItem)
		{
			this.crushedItem = crushedItem;
			this.world = crushedItem.worldObj;
			headPos = new BlockPos(crushedItem.posX, crushedItem.posY, crushedItem.posZ);
			EnumFacing headDirection = world.getBlockState(headPos).getValue(BlockPistonExtension.FACING);
			basePos = headPos.subtract(headDirection.getDirectionVec());
		}
	}
}
