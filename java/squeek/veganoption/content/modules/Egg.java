package squeek.veganoption.content.modules;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.entities.EntityPlasticEgg;
import squeek.veganoption.entities.EntityThrowableGenericDispenserBehavior;
import squeek.veganoption.items.ItemFoodContainered;
import squeek.veganoption.items.ItemThrowableGeneric;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Egg implements IContentModule
{
	public static Item potatoStarch;
	public static Item appleSauce;
	public static Item plasticEgg;

	public static final ItemStack potatoCrusher = new ItemStack(Blocks.piston);

	@Override
	public void create()
	{
		appleSauce = new ItemFoodContainered(3, 1f, false)
				.setUnlocalizedName(ModInfo.MODID + ".appleSauce")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":apple_sauce")
				.setContainerItem(Items.bowl);
		GameRegistry.registerItem(appleSauce, "appleSauce");

		potatoStarch = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".potatoStarch")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":potato_starch");
		GameRegistry.registerItem(potatoStarch, "potatoStarch");

		plasticEgg = new ItemThrowableGeneric(EntityPlasticEgg.class)
				.setUnlocalizedName(ModInfo.MODID + ".plasticEgg")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":plastic_egg");
		GameRegistry.registerItem(plasticEgg, "plasticEgg");

		EntityRegistry.registerModEntity(EntityPlasticEgg.class, "plasticEgg", ContentHelper.ENTITYID_PLASTIC_EGG, ModInfo.MODID, 80, 1, true);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			createPlasticEggRenderer();
		}
	}

	@SideOnly(Side.CLIENT)
	public void createPlasticEggRenderer()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityPlasticEgg.class, new RenderSnowball(plasticEgg));
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.eggOreDict, new ItemStack(Items.egg));
		OreDictionary.registerOre(ContentHelper.eggOreDict, new ItemStack(plasticEgg));

		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(Items.egg));
		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(appleSauce));
		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(potatoStarch));

		OreDictionary.registerOre(ContentHelper.starchOreDict, potatoStarch);
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInputForFoodOutput(new ItemStack(Items.egg), ContentHelper.eggBakingOreDict);
		Modifiers.recipes.convertInputForNonFoodOutput(new ItemStack(Items.egg), ContentHelper.eggOreDict);

		GameRegistry.addShapelessRecipe(new ItemStack(appleSauce), new ItemStack(Items.apple), new ItemStack(Items.bowl));

		GameRegistry.addShapelessRecipe(new ItemStack(potatoStarch), potatoCrusher, new ItemStack(Items.potato));
		Modifiers.crafting.addInputsToKeepForOutput(new ItemStack(potatoStarch), potatoCrusher);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(plasticEgg), " o ", "o o", " o ", 'o', ContentHelper.plasticOreDict));

		BlockDispenser.dispenseBehaviorRegistry.putObject(plasticEgg, new EntityThrowableGenericDispenserBehavior((ItemThrowableGeneric) plasticEgg));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(potatoStarch), new ItemStack(Items.potato));
		RelationshipRegistry.addRelationship(new ItemStack(potatoStarch), new ItemStack(Blocks.piston));
	}
}
