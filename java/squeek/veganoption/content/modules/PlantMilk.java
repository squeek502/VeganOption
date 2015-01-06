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
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockPumpkinSeedMilk;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.items.ItemBucketGeneric;
import cpw.mods.fml.common.registry.GameRegistry;

public class PlantMilk implements IContentModule
{
	public static Fluid fluidPumpkinSeedMilk;
	public static Block pumpkinSeedMilk;
	public static Item bucketPumpkinSeedMilk;

	@Override
	public void create()
	{
		fluidPumpkinSeedMilk = new Fluid(ModInfo.MODID + ".pumpkinSeedMilk");
		FluidRegistry.registerFluid(fluidPumpkinSeedMilk);
		pumpkinSeedMilk = new BlockPumpkinSeedMilk(fluidPumpkinSeedMilk)
				.setBlockName(ModInfo.MODID + ".pumpkinSeedMilk");
		fluidPumpkinSeedMilk.setBlock(pumpkinSeedMilk);
		fluidPumpkinSeedMilk.setUnlocalizedName(pumpkinSeedMilk.getUnlocalizedName());
		GameRegistry.registerBlock(pumpkinSeedMilk, "pumpkinSeedMilk");

		bucketPumpkinSeedMilk = new ItemBucketGeneric(pumpkinSeedMilk)
				.setUnlocalizedName(ModInfo.MODID + ".bucketPumpkinSeedMilk")
				.setTextureName("bucket_milk")
				.setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucketPumpkinSeedMilk, "bucketPumpkinSeedMilk");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidPumpkinSeedMilk, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketPumpkinSeedMilk), new ItemStack(Items.bucket));
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.milkOreDict, new ItemStack(Items.milk_bucket));
		OreDictionary.registerOre(ContentHelper.milkOreDict, new ItemStack(bucketPumpkinSeedMilk));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Items.milk_bucket), ContentHelper.milkOreDict);

		GameRegistry.addShapelessRecipe(new ItemStack(bucketPumpkinSeedMilk), // output
										new ItemStack(Items.water_bucket),
										new ItemStack(Items.pumpkin_seeds),
										new ItemStack(Items.pumpkin_seeds),
										new ItemStack(Items.sugar));
		Modifiers.crafting.addInputsToRemoveForOutput(new ItemStack(bucketPumpkinSeedMilk), // output
														new ItemStack(Items.water_bucket));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(bucketPumpkinSeedMilk), new ItemStack(pumpkinSeedMilk));
		RelationshipRegistry.addRelationship(new ItemStack(pumpkinSeedMilk), new ItemStack(bucketPumpkinSeedMilk));
	}
}
