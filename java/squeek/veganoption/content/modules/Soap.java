package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
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
import squeek.veganoption.blocks.BlockLyeWater;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.items.ItemBucketGeneric;
import squeek.veganoption.items.ItemSoap;
import cpw.mods.fml.common.registry.GameRegistry;

public class Soap implements IContentModule
{
	public static Fluid fluidLyeWater;
	public static Block lyeWater;
	public static Item bucketLyeWater;
	public static Item soap;

	@Override
	public void create()
	{
		fluidLyeWater = new Fluid(ModInfo.MODID + ".lyeWater");
		FluidRegistry.registerFluid(fluidLyeWater);
		lyeWater = new BlockLyeWater(fluidLyeWater)
				.setBlockName(ModInfo.MODID + ".lyeWater");
		fluidLyeWater.setBlock(lyeWater);
		fluidLyeWater.setUnlocalizedName(lyeWater.getUnlocalizedName());
		GameRegistry.registerBlock(lyeWater, "lyeWater");

		bucketLyeWater = new ItemBucketGeneric(lyeWater)
				.setUnlocalizedName(ModInfo.MODID + ".bucketLyeWater")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":lye_water_bucket")
				.setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucketLyeWater, "bucketLyeWater");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidLyeWater, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketLyeWater), new ItemStack(Items.bucket));

		soap = new ItemSoap()
				.setUnlocalizedName(ModInfo.MODID + ".soap")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":soap");
		GameRegistry.registerItem(soap, "soap");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.woodAshOreDict, ContentHelper.charcoal.copy());
		OreDictionary.registerOre(ContentHelper.soapOreDict, new ItemStack(soap, 1, OreDictionary.WILDCARD_VALUE));
	}

	@Override
	public void recipes()
	{
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bucketLyeWater), new ItemStack(Items.water_bucket), ContentHelper.woodAshOreDict, ContentHelper.woodAshOreDict, ContentHelper.woodAshOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(bucketLyeWater), new ItemStack(Items.water_bucket));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(soap),
				new ItemStack(bucketLyeWater),
				ContentHelper.vegetableOilOreDict,
				ContentHelper.rosinOreDict));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(lyeWater), new ItemStack(bucketLyeWater));
		RelationshipRegistry.addRelationship(new ItemStack(bucketLyeWater), new ItemStack(lyeWater));
	}
}
