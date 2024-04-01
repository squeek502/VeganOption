package squeek.veganoption.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import squeek.veganoption.content.modules.Ender;

public class EnderRiftBlockEntity extends TheEndPortalBlockEntity
{
	public EnderRiftBlockEntity(BlockPos pos, BlockState state)
	{
		super(Ender.enderRiftType.get(), pos, state);
	}
}
