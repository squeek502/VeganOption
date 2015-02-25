package squeek.veganoption.helpers;

import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class WorldHelper
{
	@SuppressWarnings("unchecked")
	public static List<EntityItem> getItemEntitiesWithin(World world, int x, int y, int z)
	{
		return world.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1), IEntitySelector.selectAnything);
	}
}
