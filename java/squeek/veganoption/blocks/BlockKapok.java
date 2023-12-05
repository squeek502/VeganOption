package squeek.veganoption.blocks;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BlockKapok extends Block
{
	public BlockKapok(DyeColor color)
	{
		super(BlockBehaviour.Properties.of()
			.mapColor(color.getMapColor())
			.instrument(NoteBlockInstrument.GUITAR)
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.ignitedByLava()
			.strength(0.8F));
	}
}
