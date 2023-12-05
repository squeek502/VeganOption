package squeek.veganoption.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Base class for all PistonEvent events.<br>
 * <br>
 * All children of this event are fired on the {@link NeoForge#EVENT_BUS}.
 */
public abstract class PistonEvent extends Event
{

	/**
	 * Fired when a piston tries to extend.
	 * Note: The piston is not guaranteed to extend after this event is fired.
	 *
	 * This event is fired in {@link PistonBaseBlock#moveBlocks(Level, BlockPos, Direction, boolean)}.<br>
	 * <br>
	 * This event is not cancelable ({@link ICancellableEvent}).<br>
	 */
	public static class TryExtend extends PistonEvent
	{
		public final Level level;

		public final BlockPos basePos;
		public final Direction facing;

		public final BlockPos headPos;

		public TryExtend(Level level, BlockPos basePos, Direction direction)
		{
			this.level = level;
			this.basePos = basePos;
			this.facing = direction;
			this.headPos = basePos.relative(direction);
		}
	}

	/**
	 * Fired every time a piston updates while extending.
	 *
	 * This event is fired in {@link TileEntityPiston#update()}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class Extending extends PistonEvent
	{
		public final Level level;
		public final BlockPos headPos;
		public final float progress;

		public Extending(Level level, BlockPos headPos, float progress)
		{
			this.level = level;
			this.headPos = headPos;
			this.progress = progress;
		}
	}
}
