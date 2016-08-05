package squeek.veganoption.content.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockFluidGeneric;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;

public class VegetableOil implements IContentModule
{
	public static Item seedSunflower;
	public static Item oilVegetable;
	public static Fluid fluidVegetableOil;
	public static Block fluidBlockVegetableOil;

	public static ItemStack oilPresser;

	@Override
	public void create()
	{
		oilPresser = new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

		seedSunflower = new ItemFood(1, 0.05f, false)
				.setUnlocalizedName(ModInfo.MODID + ".seedSunflower")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "seedsSunflower");
		GameRegistry.register(seedSunflower);

		fluidVegetableOil = new Fluid(ModInfo.MODID + ".fluidOilVegetable", new ResourceLocation(ModInfo.MODID_LOWER, "blocks/vegetable_oil_still"), new ResourceLocation(ModInfo.MODID_LOWER, "blocks/vegetable_oil_flow"));
		FluidRegistry.registerFluid(fluidVegetableOil);
		fluidBlockVegetableOil = new BlockFluidGeneric(fluidVegetableOil, Material.WATER)
				.setUnlocalizedName(ModInfo.MODID + ".fluidOilVegetable");
		fluidVegetableOil.setBlock(fluidBlockVegetableOil);
		fluidVegetableOil.setUnlocalizedName(fluidBlockVegetableOil.getUnlocalizedName());
		GameRegistry.register(fluidBlockVegetableOil);

		oilVegetable = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".oilVegetable")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "oilVegetable")
				.setContainerItem(Items.GLASS_BOTTLE);
		GameRegistry.registerItem(oilVegetable, "oilVegetable");

		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidVegetableOil, Fluid.BUCKET_VOLUME), new ItemStack(oilVegetable), new ItemStack(oilVegetable.getContainerItem()));
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.oilPresserOreDict, oilPresser.copy());
		OreDictionary.registerOre(ContentHelper.sunflowerSeedOreDict, new ItemStack(seedSunflower));
		OreDictionary.registerOre(ContentHelper.vegetableOilOreDict, new ItemStack(oilVegetable));
	}

	@Override
	public void recipes()
	{
		ContentHelper.remapOre(ContentHelper.sunflowerSeedOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.grapeSeedOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.soybeanOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.cottonSeedOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.coconutOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.oliveOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.cornOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.nutOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.teaSeedOreDict, ContentHelper.vegetableOilSourceOreDict);
		ContentHelper.remapOre(ContentHelper.avocadoOreDict, ContentHelper.vegetableOilSourceOreDict);

		BlockSpecifier sunflowerTopSpecifier = new BlockSpecifier(Blocks.DOUBLE_PLANT, 0)
		{
			@Override
			public boolean matches(IBlockAccess world, BlockPos pos, Block block, int meta)
			{
				boolean isRightBlock = this.block == block;
				boolean isRightMeta = this.meta == BlockDoublePlant.EnumPlantType.SUNFLOWER.getMeta();
				return isRightBlock && isRightMeta;
			}
		};
		DropSpecifier sunflowerDropSpecifier = new DropSpecifier(new ItemStack(seedSunflower))
		{
			@Override
			public void modifyDrops(List<ItemStack> drops, EntityPlayer harvester, int fortuneLevel, boolean isSilkTouching)
			{
				// harvester is null when breaking the top block because
				// the bottom breaks on its own once there is no longer a top
				if (harvester == null)
				{
					List<ItemStack> dropsToRemove = new ArrayList<ItemStack>();
					for (ItemStack drop : drops)
					{
						if (drop.getItem() == Item.getItemFromBlock(Blocks.DOUBLE_PLANT) && drop.getItemDamage() == 0)
							dropsToRemove.add(drop);
					}
					drops.removeAll(dropsToRemove);

					super.modifyDrops(drops, null, fortuneLevel, isSilkTouching);
				}
			}
		};
		Modifiers.drops.addDropsToBlock(sunflowerTopSpecifier, sunflowerDropSpecifier);

		addOilRecipe(new ItemStack(oilVegetable), ContentHelper.vegetableOilSourceOreDict);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(fluidVegetableOil, ContentHelper.vegetableOilSourceOreDict));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(fluidBlockVegetableOil), new ItemStack(oilVegetable));
		RelationshipRegistry.addRelationship(new ItemStack(oilVegetable), new ItemStack(fluidBlockVegetableOil));
	}

	public static void addOilRecipe(ItemStack output, Object... inputs)
	{
		List<Object> recipeInputs = new ArrayList<Object>(Arrays.asList(inputs));
		recipeInputs.add(0, ContentHelper.oilPresserOreDict);
		if (output.getItem().hasContainerItem(output))
		{
			recipeInputs.add(output.getItem().getContainerItem(output));
		}
		GameRegistry.addRecipe(new ShapelessOreRecipe(output, recipeInputs.toArray(new Object[recipeInputs.size()])));
		if (!oilPresser.getItem().hasContainerItem(oilPresser))
		{
			Modifiers.crafting.addInputsToKeepForOutput(output, oilPresser);
		}
	}
}
