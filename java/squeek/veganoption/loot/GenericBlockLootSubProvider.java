package squeek.veganoption.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;

import java.util.Collections;

public abstract class GenericBlockLootSubProvider extends BlockLootSubProvider
{
	public GenericBlockLootSubProvider()
	{
		super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
	}

//	@Override
//	protected Iterable<Block> getKnownBlocks()
//	{
//		return VeganOption.REGISTER_BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
//	}
}
