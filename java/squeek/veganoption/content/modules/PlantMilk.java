package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.recipes.ShapelessMatchingOreRecipe;
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.content.registry.PistonCraftingRegistry;

public class PlantMilk implements IContentModule
{
	public static Fluid fluidPlantMilk;
	public static Block plantMilk;
	public static ItemStack bucketPlantMilk;

	@Override
	public void create()
	{
		fluidPlantMilk = new Fluid("plant_milk", new ResourceLocation(ModInfo.MODID_LOWER, "blocks/plant_milk_still"), new
			ResourceLocation(ModInfo.MODID_LOWER, "blocks/plant_milk_flow"));
		FluidRegistry.registerFluid(fluidPlantMilk);
		plantMilk = new BlockFluidClassic(fluidPlantMilk, Material.WATER)
			.setUnlocalizedName(ModInfo.MODID + ".plantMilk")
			.setRegistryName(ModInfo.MODID_LOWER, "plantMilk");
		fluidPlantMilk.setBlock(plantMilk);
		fluidPlantMilk.setUnlocalizedName(plantMilk.getUnlocalizedName());
		GameRegistry.register(plantMilk);
		GameRegistry.register(new ItemBlock(plantMilk).setRegistryName(plantMilk.getRegistryName()));

		FluidRegistry.addBucketForFluid(fluidPlantMilk);

		bucketPlantMilk = new ItemStack(Items.MILK_BUCKET);
		bucketPlantMilk.setStackDisplayName("Plant Milk Bucket");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.plantMilkSourceOreDict, new ItemStack(Items.PUMPKIN_SEEDS));
	}

	@Override
	public void recipes()
	{
		ContentHelper.remapOre(ContentHelper.soybeanOreDict, ContentHelper.plantMilkSourceOreDict);
		ContentHelper.remapOre(ContentHelper.coconutOreDict, ContentHelper.plantMilkSourceOreDict);
		ContentHelper.remapOre(ContentHelper.almondOreDict, ContentHelper.plantMilkSourceOreDict);
		ContentHelper.remapOre(ContentHelper.riceOreDict, ContentHelper.plantMilkSourceOreDict);
		ContentHelper.remapOre(ContentHelper.oatOreDict, ContentHelper.plantMilkSourceOreDict);

		Modifiers.recipes.convertInput(new ItemStack(Items.MILK_BUCKET), ContentHelper.milkOreDict);

		GameRegistry.addRecipe(new ShapelessMatchingOreRecipe(bucketPlantMilk.copy(),
															  new ItemStack(Items.WATER_BUCKET),
															  ContentHelper.plantMilkSourceOreDict,
															  ContentHelper.plantMilkSourceOreDict,
															  new ItemStack(Items.SUGAR)));
		Modifiers.crafting.addInputsToRemoveForOutput(bucketPlantMilk.copy(), // output
													  new ItemStack(Items.WATER_BUCKET));

		PistonCraftingRegistry.register(new PistonCraftingRecipe(fluidPlantMilk, FluidRegistry.WATER, Items.SUGAR, new InputItemStack(ContentHelper.plantMilkSourceOreDict, 2)));

		DescriptionRegistry.registerCustomCraftingText(bucketPlantMilk.copy(), ModInfo.MODID + ":plant_milk_bucket.vowiki.crafting");
		DescriptionRegistry.registerCustomUsageText(bucketPlantMilk.copy(), ModInfo.MODID + ":plant_milk_bucket.vowiki.usage");
	}

	@Override
	public void finish()
	{
		//RelationshipRegistry.addRelationship(bucketPlantMilk.copy(), new ItemStack(plantMilk));
		//RelationshipRegistry.addRelationship(new ItemStack(plantMilk), bucketPlantMilk.copy());
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
		ContentHelper.registerFluidMapperAndMeshDef(plantMilk, "plant_milk");
	}
}
