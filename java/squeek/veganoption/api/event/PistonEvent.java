package squeek.veganoption.api.event;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import static cpw.mods.fml.common.eventhandler.Event.HasResult;

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
	 * This event is fired in {@link BlockPistonBase#tryExtend(World, int, int, int, int)}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class TryExtend extends PistonEvent
	{
		public final World world;

		public final int baseX;
		public final int baseY;
		public final int baseZ;
		public final int facing;

		public final int headX;
		public final int headY;
		public final int headZ;

		public TryExtend(World world, int baseX, int baseY, int baseZ, int facing)
		{
			this.world = world;
			this.baseX = baseX;
			this.baseY = baseY;
			this.baseZ = baseZ;
			this.facing = facing;
			this.headX = baseX + Facing.offsetsXForSide[facing];
			this.headY = baseY + Facing.offsetsYForSide[facing];
			this.headZ = baseZ + Facing.offsetsZForSide[facing];
		}
	}

	/**
	 * Fired every time a piston retracts.
	 * 
	 * This event is fired in {@link BlockPistonBase#tryExtend(World, int, int, int, int)}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class Retract extends PistonEvent
	{
		public final World world;

		public final int baseX;
		public final int baseY;
		public final int baseZ;
		public final int facing;

		public final int headX;
		public final int headY;
		public final int headZ;

		public Retract(World world, int baseX, int baseY, int baseZ, int facing)
		{
			this.world = world;
			this.baseX = baseX;
			this.baseY = baseY;
			this.baseZ = baseZ;
			this.facing = facing;
			this.headX = baseX + Facing.offsetsXForSide[facing];
			this.headY = baseY + Facing.offsetsYForSide[facing];
			this.headZ = baseZ + Facing.offsetsZForSide[facing];
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

		public final int baseX;
		public final int baseY;
		public final int baseZ;

		public final int headX;
		public final int headY;
		public final int headZ;

		public final EntityItem crushedItem;

		public CrushItem(EntityItem crushedItem)
		{
			this.crushedItem = crushedItem;
			this.world = crushedItem.worldObj;
			this.headX = MathHelper.floor_double(crushedItem.posX);
			this.headY = MathHelper.floor_double(crushedItem.posY);
			this.headZ = MathHelper.floor_double(crushedItem.posZ);
			int headDirection = BlockPistonExtension.getDirectionMeta(world.getBlockMetadata(headX, headY, headZ));
			this.baseX = headX - Facing.offsetsXForSide[headDirection];
			this.baseY = headY - Facing.offsetsYForSide[headDirection];
			this.baseZ = headZ - Facing.offsetsZForSide[headDirection];
		}
	}
}
