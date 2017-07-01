package squeek.veganoption.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

public class BlockRawEnder extends BlockFluidFinite
{
	public static Material materialRawEnder = Material.WATER;

	public BlockRawEnder(Fluid fluid)
	{
		super(fluid, materialRawEnder);
		this.setDefaultState(blockState.getBaseState().withProperty(LEVEL, 7));
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos)
	{
		//if (world.getBlock(x, y, z).getMaterial().isLiquid())
		//	return false;
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos)
	{
		//if (world.getBlock(x, y, z).getMaterial().isLiquid())
		//	return false;
		return super.displaceIfPossible(world, pos);
	}
}
