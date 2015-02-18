package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockFluidGeneric;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class Ink implements IContentModule
{
	public static Item blackVegetableOilInk;
	public static Item whiteVegetableOilInk;
	public static Item waxVegetable;
	public static Fluid blackInkFluid;
	public static Fluid whiteInkFluid;
	public static Block blackInk;
	public static Block whiteInk;

	@Override
	public void create()
	{
		waxVegetable = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".waxVegetable")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_wax");
		GameRegistry.registerItem(waxVegetable, "waxVegetable");

		blackVegetableOilInk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOilBlack")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_oil_ink_black")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(blackVegetableOilInk, "inkVegetableOilBlack");

		blackInkFluid = new Fluid(ModInfo.MODID + ".inkBlack");
		FluidRegistry.registerFluid(blackInkFluid);
		blackInk = new BlockFluidGeneric(blackInkFluid, Material.water, "black_ink")
				.setBlockName(ModInfo.MODID + ".inkBlack");
		blackInkFluid.setBlock(blackInk);
		blackInkFluid.setUnlocalizedName(blackInk.getUnlocalizedName());
		GameRegistry.registerBlock(blackInk, "inkBlack");

		FluidContainerRegistry.registerFluidContainer(new FluidStack(blackInkFluid, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(blackVegetableOilInk), new ItemStack(blackVegetableOilInk.getContainerItem()));

		whiteVegetableOilInk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOilWhite")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_oil_ink_white")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(whiteVegetableOilInk, "inkVegetableOilWhite");

		whiteInkFluid = new Fluid(ModInfo.MODID + ".inkWhite");
		FluidRegistry.registerFluid(whiteInkFluid);
		whiteInk = new BlockFluidGeneric(whiteInkFluid, Material.water, "white_ink")
				.setBlockName(ModInfo.MODID + ".inkWhite");
		whiteInkFluid.setBlock(whiteInk);
		whiteInkFluid.setUnlocalizedName(whiteInk.getUnlocalizedName());
		GameRegistry.registerBlock(whiteInk, "inkWhite");

		FluidContainerRegistry.registerFluidContainer(new FluidStack(whiteInkFluid, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(whiteVegetableOilInk), new ItemStack(whiteVegetableOilInk.getContainerItem()));
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.blackInkOreDict, ContentHelper.inkSac.copy());

		OreDictionary.registerOre(ContentHelper.blackPigmentOreDict, ContentHelper.charcoal.copy());
		OreDictionary.registerOre(ContentHelper.whitePigmentOreDict, Items.quartz);
		OreDictionary.registerOre(ContentHelper.blackDyeOreDict, blackVegetableOilInk);
		OreDictionary.registerOre(ContentHelper.blackInkOreDict, blackVegetableOilInk);
		OreDictionary.registerOre(ContentHelper.whiteDyeOreDict, whiteVegetableOilInk);
		OreDictionary.registerOre(ContentHelper.whiteInkOreDict, whiteVegetableOilInk);
		OreDictionary.registerOre(ContentHelper.waxOreDict, new ItemStack(waxVegetable));
		OreDictionary.registerOre(ContentHelper.waxOreDictForestry, new ItemStack(waxVegetable));
		OreDictionary.registerOre(ContentHelper.waxOreDictHarvestCraft, new ItemStack(waxVegetable));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(ContentHelper.inkSac.copy(), ContentHelper.blackInkOreDict);

		ContentHelper.addOreSmelting(ContentHelper.vegetableOilOreDict, new ItemStack(waxVegetable), 0.2f);

		GameRegistry.addRecipe(new ShapelessOreRecipe(blackVegetableOilInk, ContentHelper.vegetableOilOreDict, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.blackPigmentOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(blackVegetableOilInk), ContentHelper.vegetableOilOreDict);

		GameRegistry.addRecipe(new ShapelessOreRecipe(whiteVegetableOilInk, ContentHelper.vegetableOilOreDict, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.whitePigmentOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(whiteVegetableOilInk), ContentHelper.vegetableOilOreDict);
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(whiteVegetableOilInk), new ItemStack(whiteInk));
		RelationshipRegistry.addRelationship(new ItemStack(whiteInk), new ItemStack(whiteVegetableOilInk));
		RelationshipRegistry.addRelationship(new ItemStack(blackVegetableOilInk), new ItemStack(blackInk));
		RelationshipRegistry.addRelationship(new ItemStack(blackInk), new ItemStack(blackVegetableOilInk));
	}

}
