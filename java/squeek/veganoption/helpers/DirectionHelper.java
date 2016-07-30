package squeek.veganoption.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class DirectionHelper
{
	public static EnumFacing getDirectionFromYaw(EntityLivingBase entity)
	{
		int l = MathHelper.floor_double((entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		switch (l)
		{
			case 0:
				return EnumFacing.SOUTH;
			case 1:
				return EnumFacing.WEST;
			case 2:
				return EnumFacing.NORTH;
			case 3:
				return EnumFacing.EAST;
			default:
				return EnumFacing.NORTH;
		}
	}
}
