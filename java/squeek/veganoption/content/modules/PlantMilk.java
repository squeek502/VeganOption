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
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.recipes.ShapelessMatchingOreRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.integration.IntegrationBase;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.items.ItemBucketGeneric;

public class PlantMilk implements IContentModule
{
	public static Fluid fluidPlantMilk;
	public static Block plantMilk;
	public static Item bucketPlantMilk;

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
		fluidPlantMilk.setUnlocalizedName(fluidPlantMilk.getUnlocalizedName());
		GameRegistry.register(plantMilk);
		GameRegistry.register(new ItemBlock(plantMilk).setRegistryName(plantMilk.getRegistryName()));

		bucketPlantMilk = new ItemBucketGeneric(plantMilk)
			.setUnlocalizedName(ModInfo.MODID + ".bucketPlantMilk")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "bucketPlantMilk")
			.setContainerItem(Items.BUCKET);
		GameRegistry.register(bucketPlantMilk);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidPlantMilk, Fluid.BUCKET_VOLUME), new ItemStack(bucketPlantMilk), new ItemStack(Items.BUCKET));
	}

	@Override
	public void oredict()
	{
		if (!IntegrationHandler.modExists(IntegrationBase.MODID_MINEFACTORY_RELOADED))
			OreDictionary.registerOre(ContentHelper.milkOreDict, new ItemStack(Items.MILK_BUCKET));
		OreDictionary.registerOre(ContentHelper.milkOreDict, new ItemStack(bucketPlantMilk));

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

		GameRegistry.addRecipe(new ShapelessMatchingOreRecipe(new ItemStack(bucketPlantMilk),
															  new ItemStack(Items.WATER_BUCKET),
															  ContentHelper.plantMilkSourceOreDict,
															  ContentHelper.plantMilkSourceOreDict,
															  new ItemStack(Items.SUGAR)));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(bucketPlantMilk), // output
													  new ItemStack(Items.WATER_BUCKET));

		PistonCraftingRegistry.register(new PistonCraftingRecipe(fluidPlantMilk, FluidRegistry.WATER, Items.SUGAR, new InputItemStack(ContentHelper.plantMilkSourceOreDict, 2)));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(bucketPlantMilk), new ItemStack(plantMilk));
		RelationshipRegistry.addRelationship(new ItemStack(plantMilk), new ItemStack(bucketPlantMilk));
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
		ContentHelper.registerTypicalItemModel(bucketPlantMilk);
		ContentHelper.registerFluidMapperAndMeshDef(plantMilk, "plant_milk");
	}
}
