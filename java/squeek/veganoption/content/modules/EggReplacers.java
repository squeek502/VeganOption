package squeek.veganoption.content.modules;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.integration.pams.HarvestCraft;
import squeek.veganoption.items.ItemFoodContainered;
import cpw.mods.fml.common.registry.GameRegistry;

public class EggReplacers implements IContentModule
{
	public static Item potatoStarch;
	public static Item appleSauce;

	public static final ItemStack potatoCrusher = new ItemStack(Blocks.piston);

	@Override
	public void create()
	{
		appleSauce = new ItemFoodContainered(3, 1f, false)
				.setUnlocalizedName(ModInfo.MODID + ".appleSauce")
				.setCreativeTab(CreativeTabs.tabFood)
				.setTextureName(ModInfo.MODID_LOWER + ":apple_sauce")
				.setContainerItem(Items.bowl);
		GameRegistry.registerItem(appleSauce, "appleSauce");

		potatoStarch = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".potatoStarch")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":potato_starch");
		GameRegistry.registerItem(potatoStarch, "potatoStarch");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.starchOreDict, potatoStarch);
		OreDictionary.registerOre(ContentHelper.eggOreDict, new ItemStack(appleSauce));
		OreDictionary.registerOre(ContentHelper.eggOreDict, new ItemStack(potatoStarch));
	}

	@Override
	public void recipes()
	{
		if (IntegrationHandler.modExists(IntegrationHandler.MODID_HARVESTCRAFT))
		{
			appleSauce = HarvestCraft.getItem("applesauceItem");
		}
		else
		{
			GameRegistry.addShapelessRecipe(new ItemStack(appleSauce), new ItemStack(Items.apple), new ItemStack(Items.bowl));
		}

		OreDictionary.registerOre(ContentHelper.eggOreDict, new ItemStack(Items.egg));
		Modifiers.recipes.convertInput(new ItemStack(Items.egg), ContentHelper.eggOreDict);

		GameRegistry.addShapelessRecipe(new ItemStack(potatoStarch), potatoCrusher, new ItemStack(Items.potato));
		Modifiers.crafting.addInputsToKeepForOutput(new ItemStack(potatoStarch), potatoCrusher);
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(potatoStarch), new ItemStack(Items.potato));
		RelationshipRegistry.addRelationship(new ItemStack(potatoStarch), new ItemStack(Blocks.piston));
	}
}
