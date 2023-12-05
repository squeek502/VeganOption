package squeek.veganoption.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import squeek.veganoption.content.modules.Ender;

public class TileEntityEnderRift extends TheEndPortalBlockEntity
{
	public TileEntityEnderRift(BlockPos pos, BlockState state)
	{
		super(Ender.enderRiftType.get(), pos, state);
	}
}
