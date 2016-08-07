package squeek.veganoption.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHollowBlock
{
	public boolean isBlockFullCube(World world, BlockPos pos);
}
