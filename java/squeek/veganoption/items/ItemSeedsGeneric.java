package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;

public class ItemSeedsGeneric extends ItemSeeds
{
	EnumPlantType plantType;

	public ItemSeedsGeneric(Block plantBlock, EnumPlantType plantType)
	{
		super(plantBlock, null);
		this.plantType = plantType;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
	{
		return plantType;
	}
}
