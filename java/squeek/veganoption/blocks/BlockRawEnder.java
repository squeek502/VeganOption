package squeek.veganoption.blocks;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import squeek.veganoption.content.modules.Ender;

public class BlockRawEnder extends LiquidBlock
{
	public BlockRawEnder()
	{
		super(() -> (FlowingFluid) Ender.rawEnderStill.get(), BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_BLACK)
			.replaceable()
			.noCollission()
			.randomTicks()
			.strength(100f)
			.lightLevel(state -> 3)
			.pushReaction(PushReaction.DESTROY)
			.noLootTable()
			.liquid()
			.sound(SoundType.EMPTY));
		registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
	}
}
