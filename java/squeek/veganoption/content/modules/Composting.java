package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.items.ItemFertilizer;

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
			.setUnlocalizedName(ModInfo.MODID + ".composter")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "composter");
		GameRegistry.register(composter);
		GameRegistry.register(new ItemBlock(composter).setRegistryName(composter.getRegistryName()));
		GameRegistry.registerTileEntity(TileEntityComposter.class, ModInfo.MODID + ".composter");

		rottenPlants = new ItemFood(4, 0.1F, true)
			.setPotionEffect(new PotionEffect(MobEffects.HUNGER, 30, 0), 0.8F)
			.setUnlocalizedName(ModInfo.MODID + ".rottenPlants")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "rottenPlants");
		GameRegistry.register(rottenPlants);

		fertilizer = new ItemFertilizer()
			.setUnlocalizedName(ModInfo.MODID + ".fertilizer")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "fertilizer");
		GameRegistry.register(fertilizer);

		compost = new BlockCompost()
			.setHardness(0.5F)
			.setUnlocalizedName(ModInfo.MODID + ".compost")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "compost");
		GameRegistry.register(compost);
		GameRegistry.register(new ItemBlock(compost).setRegistryName(compost.getRegistryName()));
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.rottenOreDict, new ItemStack(Items.ROTTEN_FLESH));
		OreDictionary.registerOre(ContentHelper.rottenOreDict, rottenPlants);
		OreDictionary.registerOre(ContentHelper.fertilizerOreDict, fertilizer);
		OreDictionary.registerOre(ContentHelper.brownDyeOreDict, fertilizer);
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Items.ROTTEN_FLESH), ContentHelper.rottenOreDict);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(composter), "/c/", "/ /", '/', ContentHelper.stickOreDict, 'c', new ItemStack(Blocks.CHEST)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fertilizer, 8), new ItemStack(compost), ContentHelper.saltpeterOreDict));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(compost), new ItemStack(composter));
		RelationshipRegistry.addRelationship(new ItemStack(rottenPlants), new ItemStack(composter));

		CompostRegistry.addBrown(ContentHelper.stickOreDict);
		CompostRegistry.addBrown(Items.PAPER);
		CompostRegistry.addBrown(ContentHelper.bastFibreOreDict);
		CompostRegistry.addBrown(ContentHelper.woodAshOreDict);
		CompostRegistry.addBrown(Blocks.DEADBUSH);
		CompostRegistry.addBrown(ContentHelper.sawDustOreDict);
		CompostRegistry.addBrown(ContentHelper.sawDustAltOreDict);

		CompostRegistry.addGreen(ContentHelper.saplingOreDict);
		CompostRegistry.addGreen(rottenPlants);
		CompostRegistry.addGreen(Blocks.TALLGRASS);
		CompostRegistry.addGreen(Blocks.DOUBLE_PLANT);
		CompostRegistry.addGreen(ContentHelper.leavesOreDict);
		CompostRegistry.addGreen(Blocks.PUMPKIN);
		CompostRegistry.addGreen(Blocks.MELON_BLOCK);
		CompostRegistry.addGreen(Blocks.VINE);
		CompostRegistry.addGreen(Blocks.YELLOW_FLOWER);
		CompostRegistry.addGreen(Blocks.RED_FLOWER);
		CompostRegistry.addGreen(Blocks.BROWN_MUSHROOM);
		CompostRegistry.addGreen(Blocks.RED_MUSHROOM);

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
	@Override
	public void clientSidePost()
	{
		RenderComposter composterRenderer = new RenderComposter();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityComposter.class, composterRenderer);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(rottenPlants);
		ContentHelper.registerTypicalItemModel(fertilizer);
		ContentHelper.registerTypicalItemModel(Item.getItemFromBlock(compost));
		ContentHelper.registerTypicalItemModel(Item.getItemFromBlock(composter));
	}
}
