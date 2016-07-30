package squeek.veganoption.helpers;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHelper
{
	public static List<EntityItem> getItemEntitiesWithin(World world, BlockPos pos)
	{
		return getItemEntitiesWithin(world, new AxisAlignedBB(pos));
	}

	@SuppressWarnings("unchecked")
	public static List<EntityItem> getItemEntitiesWithin(World world, AxisAlignedBB aabb)
	{
		return world.selectEntitiesWithinAABB(EntityItem.class, aabb, IEntitySelector.selectAnything);
	}
}
