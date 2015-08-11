package squeek.veganoption.helpers;

import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class WorldHelper
{
	public static List<EntityItem> getItemEntitiesWithin(World world, int x, int y, int z)
	{
		return getItemEntitiesWithin(world, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
	}

	@SuppressWarnings("unchecked")
	public static List<EntityItem> getItemEntitiesWithin(World world, AxisAlignedBB aabb)
	{
		return world.selectEntitiesWithinAABB(EntityItem.class, aabb, IEntitySelector.selectAnything);
	}
}
