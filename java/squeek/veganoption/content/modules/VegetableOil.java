package squeek.veganoption.content.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.integration.pams.HarvestCraft;
import cpw.mods.fml.common.registry.GameRegistry;

public class VegetableOil implements IContentModule
{
	public static Item seedSunflower;
	public static Item oilSunflower;

	public static ItemStack oilPresser;

	@Override
	public void create()
	{
		if (IntegrationHandler.modExists(IntegrationHandler.MODID_HARVESTCRAFT))
			oilPresser = new ItemStack(HarvestCraft.getItem("juicerItem"));
		else
			oilPresser = new ItemStack(Blocks.heavy_weighted_pressure_plate);

		seedSunflower = new ItemFood(1, 0.05f, false)
				.setUnlocalizedName(ModInfo.MODID + ".seedSunflower")
				.setCreativeTab(CreativeTabs.tabFood)
				.setTextureName(ModInfo.MODID_LOWER + ":sunflower_seeds");
		GameRegistry.registerItem(seedSunflower, "seedSunflower");

		if (IntegrationHandler.modExists(IntegrationHandler.MODID_HARVESTCRAFT))
		{
			seedSunflower = HarvestCraft.getItem("sunflowerseedsItem");
		}

		oilSunflower = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".oilSunflower")
				.setCreativeTab(CreativeTabs.tabFood)
				.setTextureName(ModInfo.MODID_LOWER + ":sunflower_oil")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(oilSunflower, "oilSunflower");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.vegetableOilOreDict, new ItemStack(oilSunflower));
	}

	@Override
	public void recipes()
	{
		BlockSpecifier sunflowerTopSpecifier = new BlockSpecifier(Blocks.double_plant, 0)
		{
			@Override
			public boolean matches(IBlockAccess world, int x, int y, int z, Block block, int meta)
			{
				boolean isRightBlock = this.block == block;
				boolean isRightMeta = this.meta == BlockDoublePlant.func_149890_d(meta);
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
						if (drop.getItem() == Item.getItemFromBlock(Blocks.double_plant) && drop.getItemDamage() == 0)
							dropsToRemove.add(drop);
					}
					drops.removeAll(dropsToRemove);

					super.modifyDrops(drops, harvester, fortuneLevel, isSilkTouching);
				}
			}
		};
		Modifiers.drops.addDropsToBlock(sunflowerTopSpecifier, sunflowerDropSpecifier);

		addOilRecipe(new ItemStack(oilSunflower), new ItemStack(seedSunflower));
	}

	@Override
	public void finish()
	{
	}

	public static void addOilRecipe(ItemStack output, ItemStack... inputs)
	{
		List<ItemStack> recipeInputs = new ArrayList<ItemStack>(Arrays.asList(inputs));
		recipeInputs.add(0, oilPresser);
		if (output.getItem().hasContainerItem(output))
		{
			recipeInputs.add(output.getItem().getContainerItem(output));
		}
		GameRegistry.addShapelessRecipe(output, (Object[]) recipeInputs.toArray(new ItemStack[recipeInputs.size()]));
		if (!oilPresser.getItem().hasContainerItem(oilPresser))
		{
			Modifiers.crafting.addInputsToKeepForOutput(output, oilPresser);
		}
	}
}
