package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nonnull;

public class ItemSeedsGeneric extends ItemSeeds
{
	EnumPlantType plantType;

	public ItemSeedsGeneric(Block plantBlock, EnumPlantType plantType)
	{
		super(plantBlock, Blocks.AIR);
		this.plantType = plantType;
	}

	@Nonnull
	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
	{
		return plantType;
	}
}
