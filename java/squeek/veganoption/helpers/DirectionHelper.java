package squeek.veganoption.helpers;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

public class DirectionHelper
{
	public static Direction getDirectionFromYaw(LivingEntity entity)
	{
		int l = (int) Math.floor((entity.getYRot() * 4.0F / 360.0F) + 0.5D) & 3;
		return switch (l)
		{
			case 0 -> Direction.SOUTH;
			case 1 -> Direction.WEST;
			case 2 -> Direction.NORTH;
			case 3 -> Direction.EAST;
			default -> Direction.NORTH;
		};
	}
}
