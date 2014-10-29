package squeek.veganoption.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class DirectionHelper
{
	public static ForgeDirection getDirectionFromYaw(EntityLivingBase entity)
	{
		int l = MathHelper.floor_double((entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		switch (l)
		{
			case 0:
				return ForgeDirection.SOUTH;
			case 1:
				return ForgeDirection.WEST;
			case 2:
				return ForgeDirection.NORTH;
			case 3:
				return ForgeDirection.EAST;
			default:
				return ForgeDirection.UNKNOWN;
		}
	}
}
