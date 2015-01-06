package squeek.veganoption.content.modules;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import cpw.mods.fml.common.registry.GameRegistry;

public class Gunpowder implements IContentModule
{
	public static Item sulfur;
	public static Item saltpeter;

	@Override
	public void create()
	{
		sulfur = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".sulfur")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":sulfur");
		GameRegistry.registerItem(sulfur, "sulfur");

		saltpeter = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".saltpeter")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":saltpeter");
		GameRegistry.registerItem(saltpeter, "saltpeter");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.sulfurOreDict, sulfur);
		OreDictionary.registerOre(ContentHelper.saltpeterOreDict, saltpeter);
	}

	@Override
	public void recipes()
	{
		Modifiers.drops.addDropsToBlock(new BlockSpecifier(Blocks.netherrack), new DropSpecifier(new ItemStack(sulfur), 0.02f));

		Modifiers.drops.addDropsToBlock(new BlockSpecifier(Blocks.sandstone), new DropSpecifier(new ItemStack(saltpeter), 0.02f, 1, 3));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder), ContentHelper.charcoal.copy(), ContentHelper.sulfurOreDict, ContentHelper.saltpeterOreDict));
	}

	@Override
	public void finish()
	{
	}

}
