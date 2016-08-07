package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;

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
				.setRegistryName(ModInfo.MODID_LOWER, "waxVegetable");
		GameRegistry.register(waxVegetable);

		blackVegetableOilInk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOilBlack")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "inkVegetableOilBlack")
				.setContainerItem(Items.GLASS_BOTTLE);
		GameRegistry.register(blackVegetableOilInk);

		blackInkFluid = new Fluid(ModInfo.MODID + ".inkBlack", new ResourceLocation(ModInfo.MODID_LOWER, "blocks/black_ink_still"), new ResourceLocation(ModInfo.MODID_LOWER, "blocks/black_ink_flow"));
		FluidRegistry.registerFluid(blackInkFluid);
		blackInk = new BlockFluidClassic(blackInkFluid, Material.WATER)
				.setRegistryName(ModInfo.MODID_LOWER, "inkBlack");
		blackInkFluid.setBlock(blackInk);
		blackInkFluid.setUnlocalizedName(blackInk.getUnlocalizedName());
		GameRegistry.register(blackInk);
		GameRegistry.register(new ItemBlock(blackInk).setRegistryName(blackInk.getRegistryName()));

		FluidContainerRegistry.registerFluidContainer(new FluidStack(blackInkFluid, Fluid.BUCKET_VOLUME), new ItemStack(blackVegetableOilInk), new ItemStack(blackVegetableOilInk.getContainerItem()));

		whiteVegetableOilInk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOilWhite")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "inkVegetableOilWhite")
				.setContainerItem(Items.GLASS_BOTTLE);
		GameRegistry.register(whiteVegetableOilInk);

		whiteInkFluid = new Fluid(ModInfo.MODID + ".inkWhite", new ResourceLocation(ModInfo.MODID_LOWER, "blocks/white_ink_still"), new ResourceLocation(ModInfo.MODID_LOWER, "blocks/white_ink_flow"));
		FluidRegistry.registerFluid(whiteInkFluid);
		whiteInk = new BlockFluidClassic(whiteInkFluid, Material.WATER)
				.setUnlocalizedName(ModInfo.MODID + ".inkWhite")
				.setRegistryName(ModInfo.MODID_LOWER, "inkWhite");
		whiteInkFluid.setBlock(whiteInk);
		whiteInkFluid.setUnlocalizedName(whiteInk.getUnlocalizedName());
		GameRegistry.register(whiteInk);
		GameRegistry.register(new ItemBlock(whiteInk).setRegistryName(whiteInk.getRegistryName()));

		FluidContainerRegistry.registerFluidContainer(new FluidStack(whiteInkFluid, Fluid.BUCKET_VOLUME), new ItemStack(whiteVegetableOilInk), new ItemStack(whiteVegetableOilInk.getContainerItem()));
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.blackInkOreDict, ContentHelper.inkSac.copy());

		OreDictionary.registerOre(ContentHelper.blackPigmentOreDict, ContentHelper.charcoal.copy());
		OreDictionary.registerOre(ContentHelper.whitePigmentOreDict, Items.QUARTZ);
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

		PistonCraftingRegistry.register(new PistonCraftingRecipe(blackInkFluid, VegetableOil.fluidVegetableOil, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.blackPigmentOreDict));

		GameRegistry.addRecipe(new ShapelessOreRecipe(whiteVegetableOilInk, ContentHelper.vegetableOilOreDict, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.whitePigmentOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(whiteVegetableOilInk), ContentHelper.vegetableOilOreDict);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(whiteInkFluid, VegetableOil.fluidVegetableOil, ContentHelper.waxOreDict, ContentHelper.rosinOreDict, ContentHelper.whitePigmentOreDict));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(whiteVegetableOilInk), new ItemStack(whiteInk));
		RelationshipRegistry.addRelationship(new ItemStack(whiteInk), new ItemStack(whiteVegetableOilInk));
		RelationshipRegistry.addRelationship(new ItemStack(blackVegetableOilInk), new ItemStack(blackInk));
		RelationshipRegistry.addRelationship(new ItemStack(blackInk), new ItemStack(blackVegetableOilInk));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSide()
	{
	}

}
