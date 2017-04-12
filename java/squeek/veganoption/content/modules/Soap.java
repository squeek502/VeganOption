package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockLyeWater;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.items.ItemSoap;

public class Soap implements IContentModule
{
	public static Fluid fluidLyeWater;
	public static Block lyeWater;
	public static ItemStack bucketLyeWater;
	public static Item soap;

	@Override
	public void create()
	{
		fluidLyeWater = new Fluid("lye_water", new ResourceLocation(ModInfo.MODID_LOWER, "blocks/lye_water_still"), new ResourceLocation(ModInfo.MODID_LOWER, "blocks/lye_water_flow"));
		FluidRegistry.registerFluid(fluidLyeWater);
		lyeWater = new BlockLyeWater(fluidLyeWater)
			.setUnlocalizedName(ModInfo.MODID + ".lyeWater")
			.setRegistryName(ModInfo.MODID_LOWER, "lyeWater");
		fluidLyeWater.setBlock(lyeWater);
		fluidLyeWater.setUnlocalizedName(lyeWater.getUnlocalizedName());
		GameRegistry.register(lyeWater);
		GameRegistry.register(new ItemBlock(lyeWater).setRegistryName(lyeWater.getRegistryName()));

		FluidRegistry.addBucketForFluid(fluidLyeWater);

		soap = new ItemSoap()
			.setUnlocalizedName(ModInfo.MODID + ".soap")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER + ":soap");
		GameRegistry.register(soap);

		UniversalBucket bucket = ForgeModContainer.getInstance().universalBucket;
		bucketLyeWater = new ItemStack(bucket);
		bucket.fill(bucketLyeWater, new FluidStack(fluidLyeWater, Fluid.BUCKET_VOLUME), true);
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
		GameRegistry.addRecipe(new ShapelessOreRecipe(bucketLyeWater.copy(), new ItemStack(Items.WATER_BUCKET), ContentHelper.woodAshOreDict, ContentHelper.woodAshOreDict, ContentHelper.woodAshOreDict));
		Modifiers.crafting.addInputsToRemoveForOutput(bucketLyeWater.copy(), new ItemStack(Items.WATER_BUCKET));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(soap),
													  bucketLyeWater.copy(),
													  ContentHelper.vegetableOilOreDict,
													  ContentHelper.rosinOreDict));

		PistonCraftingRegistry.register(new PistonCraftingRecipe(fluidLyeWater, FluidRegistry.WATER, new InputItemStack(ContentHelper.woodAshOreDict, 3)));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(lyeWater), bucketLyeWater.copy());
		RelationshipRegistry.addRelationship(bucketLyeWater.copy(), new ItemStack(lyeWater));
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
		ContentHelper.registerTypicalItemModel(soap);
		ContentHelper.registerFluidMapperAndMeshDef(lyeWater, "lye_water");
	}
}
