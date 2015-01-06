package squeek.veganoption.content.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;

public class Fossils implements IContentModule
{
	@Override
	public void create()
	{
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		// bones as a rare drop from stone
		Modifiers.drops.addDropsToBlock(new BlockSpecifier(Blocks.stone), new DropSpecifier(new ItemStack(Items.bone), 0.01f, 1, 2));
	}

	@Override
	public void finish()
	{
	}
}
