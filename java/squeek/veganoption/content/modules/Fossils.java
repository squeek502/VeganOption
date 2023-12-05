package squeek.veganoption.content.modules;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

// todo: there is now an archaeology feature in vanilla. we may want to look into using that instead of a simple stone loot modifier.
public class Fossils implements IContentModule
{
	@Override
	public void create()
	{
		// Only feature for this module is the stone-drops-bone loot modifier.
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"stone_fossils",
			new SimpleBlockDropLootModifier(
				Blocks.STONE,
				Items.BONE,
				ConstantValue.exactly(0.01f),
				UniformGenerator.between(1, 2))
			);
	}
}
