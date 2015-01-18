package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockCompost;
import squeek.veganoption.blocks.BlockComposter;
import squeek.veganoption.blocks.renderers.RenderComposter;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.items.ItemFertilizer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Composting implements IContentModule
{
	public static Block composter;
	public static Item rottenPlants;
	public static Block compost;
	public static Item fertilizer;

	@Override
	public void create()
	{
		composter = new BlockComposter()
				.setHardness(2.5F)
				.setStepSound(Block.soundTypeWood)
				.setBlockName(ModInfo.MODID + ".composter")
				.setCreativeTab(VeganOption.creativeTab)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":composter");
		GameRegistry.registerBlock(composter, "composter");
		GameRegistry.registerTileEntity(TileEntityComposter.class, ModInfo.MODID + ".composter");
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			createComposterRenderer();
		}

		rottenPlants = new ItemFood(4, 0.1F, true)
				.setPotionEffect(Potion.hunger.id, 30, 0, 0.8F)
				.setUnlocalizedName(ModInfo.MODID + ".rottenPlants")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":rotten_plants");
		GameRegistry.registerItem(rottenPlants, "rottenPlants");

		fertilizer = new ItemFertilizer()
				.setUnlocalizedName(ModInfo.MODID + ".fertilizer")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":fertilizer");
		GameRegistry.registerItem(fertilizer, "fertilizer");

		compost = new BlockCompost()
				.setHardness(0.5F)
				.setStepSound(Block.soundTypeGravel)
				.setBlockName(ModInfo.MODID + ".compost")
				.setCreativeTab(VeganOption.creativeTab)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":compost");
		GameRegistry.registerBlock(compost, "compost");

	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.rottenOreDict, rottenPlants);
		OreDictionary.registerOre(ContentHelper.fertilizerOreDict, fertilizer);
		OreDictionary.registerOre(ContentHelper.brownDyeOreDict, fertilizer);
	}

	@Override
	public void recipes()
	{
		OreDictionary.registerOre(ContentHelper.rottenOreDict, new ItemStack(Items.rotten_flesh));
		Modifiers.recipes.convertInput(new ItemStack(Items.rotten_flesh), ContentHelper.rottenOreDict);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(composter), "/c/", "/ /", '/', ContentHelper.stickOreDict, 'c', new ItemStack(Blocks.chest)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fertilizer, 8), new ItemStack(compost), ContentHelper.saltpeterOreDict));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(compost), new ItemStack(composter));
		RelationshipRegistry.addRelationship(new ItemStack(rottenPlants), new ItemStack(composter));

		CompostRegistry.addBrown(ContentHelper.stickOreDict);
		CompostRegistry.addBrown(Items.paper);
		CompostRegistry.addBrown(ContentHelper.bastFibreOreDict);
		CompostRegistry.addBrown(ContentHelper.woodAshOreDict);
		CompostRegistry.addBrown(Blocks.deadbush);
		CompostRegistry.addBrown(ContentHelper.sawDustOreDict);
		CompostRegistry.addBrown(ContentHelper.sawDustAltOreDict);

		CompostRegistry.addGreen(ContentHelper.saplingOreDict);
		CompostRegistry.addGreen(rottenPlants);
		CompostRegistry.addGreen(Blocks.tallgrass);
		CompostRegistry.addGreen(Blocks.double_plant);
		CompostRegistry.addGreen(ContentHelper.leavesOreDict);
		CompostRegistry.addGreen(Blocks.pumpkin);
		CompostRegistry.addGreen(Blocks.melon_block);
		CompostRegistry.addGreen(Blocks.vine);
		CompostRegistry.addGreen(Blocks.yellow_flower);
		CompostRegistry.addGreen(Blocks.red_flower);
		CompostRegistry.addGreen(Blocks.brown_mushroom);
		CompostRegistry.addGreen(Blocks.red_mushroom);

		CompostRegistry.blacklist(new FoodSpecifier()
		{
			@Override
			public boolean matches(ItemStack itemStack)
			{
				// meat is bad for composting
				if (itemStack.getItem() instanceof ItemFood && ((ItemFood) itemStack.getItem()).isWolfsFavoriteMeat())
					return true;
				else if (itemStack.getItem() instanceof ItemFishFood)
					return true;

				int[] oreIDs = OreDictionary.getOreIDs(itemStack);
				for (int oreID : oreIDs)
				{
					String oreName = OreDictionary.getOreName(oreID);
					if (oreName.startsWith("listAllmeat") || oreName.contains("Meat"))
						return true;
				}

				return false;
			}
		});

		CompostRegistry.registerAllFoods();
	}

	@SideOnly(Side.CLIENT)
	public void createComposterRenderer()
	{
		RenderComposter composterRenderer = new RenderComposter();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityComposter.class, composterRenderer);
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(composter), composterRenderer);
	}
}
