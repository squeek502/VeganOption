package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IHollowBlock
{
	boolean isBlockFullCube(Level world, BlockPos pos);
}
