package squeek.veganoption.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class WorldHelper
{
	public static final AABB FULL_BLOCK_AABB = Shapes.block().bounds();

	public static List<ItemEntity> getItemEntitiesWithin(Level level, BlockPos pos)
	{
		return getItemEntitiesWithin(level, new AABB(pos));
	}

	@SuppressWarnings("unchecked")
	public static List<ItemEntity> getItemEntitiesWithin(Level level, AABB aabb)
	{
		return level.getEntitiesOfClass(ItemEntity.class, aabb);
	}
}
