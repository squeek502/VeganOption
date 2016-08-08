package squeek.veganoption.content.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		Modifiers.drops.addDropsToBlock(new BlockSpecifier(Blocks.STONE), new DropSpecifier(new ItemStack(Items.BONE), 0.01f, 1, 2));
	}

	@Override
	public void finish()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePost()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
	}
}
