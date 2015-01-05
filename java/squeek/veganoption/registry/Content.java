package squeek.veganoption.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.*;
import squeek.veganoption.blocks.renderers.RenderComposter;
import squeek.veganoption.blocks.renderers.RenderEnderRift;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.entities.EntityBubbleDispenserBehavior;
import squeek.veganoption.helpers.ConstantHelper;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.integration.pams.HarvestCraft;
import squeek.veganoption.items.ItemBedGeneric;
import squeek.veganoption.items.ItemBucketGeneric;
import squeek.veganoption.items.ItemFertilizer;
import squeek.veganoption.items.ItemFoodContainered;
import squeek.veganoption.items.ItemFrozenBubble;
import squeek.veganoption.items.ItemSoap;
import squeek.veganoption.items.ItemSoapSolution;
import squeek.veganoption.modifications.CraftingModifier;
import squeek.veganoption.modifications.DropsModifier;
import squeek.veganoption.modifications.DropsModifier.BlockSpecifier;
import squeek.veganoption.modifications.DropsModifier.DropSpecifier;
import squeek.veganoption.modifications.RecipeModifier;
import squeek.veganoption.registry.CompostRegistry.FoodSpecifier;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class Content
{
	// helper itemstacks for vanilla stuff
	public static final ItemStack charcoal = new ItemStack(Items.coal, 1, 1);
	// {"skeleton", "wither", "zombie", "steve", "creeper"}
	public static final ItemStack mobHeadSkeleton = new ItemStack(Items.skull, 1, 0);
	public static final ItemStack mobHeadWitherSkeleton = new ItemStack(Items.skull, 1, 1);
	public static final ItemStack mobHeadZombie = new ItemStack(Items.skull, 1, 2);
	public static final ItemStack mobHeadSteve = new ItemStack(Items.skull, 1, 3);
	public static final ItemStack mobHeadCreeper = new ItemStack(Items.skull, 1, 4);

	// jute
	public static BlockRettable juteBundled;
	public static Item juteStalk;
	public static Item juteFibre;

	// burlap
	public static Item burlap;
	public static ItemArmor burlapHelmet;
	public static ItemArmor burlapChestplate;
	public static ItemArmor burlapLeggings;
	public static ItemArmor burlapBoots;

	// kapok
	public static Item kapokTuft;
	public static BlockColored kapokBlock;

	// faux feather
	public static Item fauxFeather;

	// straw bed
	public static BlockBedGeneric bedStrawBlock;
	public static ItemBedGeneric bedStrawItem;

	// vegan milk
	public static Fluid fluidPumpkinSeedMilk;
	public static Block pumpkinSeedMilk;
	public static Item bucketPumpkinSeedMilk;

	// egg replacers
	public static Item potatoStarch;
	public static Item appleSauce;

	// resin
	public static Item resin;

	// vegetable oil
	public static Item seedSunflower;
	public static Item oilSunflower;

	// vegetable oil ink and components
	public static Item inkVegetableOil;
	public static Item waxVegetable;
	public static Item rosin;

	// bioplastic
	public static Item bioplastic;
	public static Item plasticRod;

	// composting
	public static Block composter;
	public static Item rottenPlants;
	public static Block compost;
	public static Item fertilizer;

	// soap
	public static Fluid fluidLyeWater;
	public static Block lyeWater;
	public static Item bucketLyeWater;
	public static Item soap;
	public static Item soapSolution;

	// frozen bubble
	public static Item frozenBubble;

	// ender
	public static Block encrustedObsidian;
	public static Block enderRift;
	public static Fluid fluidRawEnder;
	public static Block rawEnder;
	public static Item bucketRawEnder;
	public static int RAW_ENDER_PER_PEARL = FluidContainerRegistry.BUCKET_VOLUME;

	// poison
	public static Item falseMorel;
	public static Item falseMorelFermented;

	// heads
	public static Item papierMache;
	public static Item mobHeadBlank;

	// gunpowder
	public static Item sulfur;
	public static Item saltpeter;

	// oredict
	public static final String leatherOreDict = "materialLeather";
	public static final String woolOreDict = "materialBedding";
	public static final String featherOreDict = "materialFeather";
	public static final String bastFibreOreDict = "materialFiber";
	public static final String milkOreDict = "listAllmilk"; // HarvestCraft's oredict entry
	public static final String eggOreDict = "listAllegg"; // HarvestCraft's oredict entry
	public static final String slimeballOreDict = "slimeball"; // Forge's oredict entry
	public static final String vegetableOilOreDict = "foodOliveoil"; // HarvestCraft's oredict entry
	public static final String waxOreDict = "materialWax";
	public static final String waxOreDictForestry = "itemBeeswax";
	public static final String inkSacOreDict = "dyeBlack"; // Forge's oredict entry
	public static final String brownDyeOreDict = "dyeBrown"; // Forge's oredict entry
	public static final String plasticOreDict = "sheetPlastic"; // MFR's oredict entry
	public static final String plasticRodOreDict = "stickPlastic";
	public static final String rottenOreDict = "materialRotten";
	public static final String fertilizerOreDict = "fertilizerOrganic"; // IC2's oredict entry
	public static final String pufferFishOreDict = "reagentWaterBreathing";
	public static final String soapOreDict = "soap";
	public static final String poisonousOreDict = "reagentPoisonous";
	public static final String fermentedOreDict = "reagentFermented";
	public static final String sulfurOreDict = "dustSulfur"; // This is what other mods use
	public static final String saltpeterOreDict = "dustSaltpeter"; // This is what other mods use
	public static final String stickOreDict = "stickWood"; // Forge's oredict entry
	public static final String leavesOreDict = "treeLeaves"; // Forge's oredict entry
	public static final String saplingOreDict = "treeSapling"; // Forge's oredict entry
	public static final String sawDustOreDict = "pulpWood"; // Mekanism's oredict entry
	public static final String sawDustAltOreDict = "dustWood"; // TE's oredict entry
	public static final String woodAshOreDict = "ashWood";
	public static final String blackPigmentOreDict = "pigmentBlack";

	// modifiers
	public static final RecipeModifier recipeModifier = new RecipeModifier();
	public static final DropsModifier dropsModifier = new DropsModifier();
	public static final CraftingModifier craftingModifier = new CraftingModifier();

	public static void create()
	{
		jute();
		burlap();
		kapok();
		palliasse();
		string();
		milk();
		fossils();
		eggReplacers();
		resin();
		vegetableOil();
		ink();
		bioplastic();
		fauxFeather();
		compost();
		soap();
		frozenBubble();
		ender();
		poison();
		heads();
		gunpowder();
	}

	public static void finish()
	{
		recipeModifier.replaceRecipes();
		registerCompostables();
		registerRelationships();
	}

	private static void registerCompostables()
	{
		CompostRegistry.addBrown(stickOreDict);
		CompostRegistry.addBrown(Items.paper);
		CompostRegistry.addBrown(papierMache);
		CompostRegistry.addBrown(bastFibreOreDict);
		CompostRegistry.addBrown(charcoal);
		CompostRegistry.addBrown(Blocks.deadbush);
		CompostRegistry.addBrown(sawDustOreDict);
		CompostRegistry.addBrown(sawDustAltOreDict);

		CompostRegistry.addGreen(saplingOreDict);
		CompostRegistry.addGreen(rottenPlants);
		CompostRegistry.addGreen(juteStalk);
		CompostRegistry.addGreen(Blocks.tallgrass);
		CompostRegistry.addGreen(Blocks.double_plant);
		CompostRegistry.addGreen(leavesOreDict);
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

	private static void registerRelationships()
	{
		// child, parent
		RelationshipRegistry.addRelationship(new ItemStack(bucketRawEnder), new ItemStack(rawEnder));
		RelationshipRegistry.addRelationship(new ItemStack(rawEnder), new ItemStack(bucketRawEnder));
		RelationshipRegistry.addRelationship(new ItemStack(rawEnder), new ItemStack(enderRift));
		RelationshipRegistry.addRelationship(new ItemStack(enderRift), new ItemStack(encrustedObsidian));
		RelationshipRegistry.addRelationship(new ItemStack(juteFibre), new ItemStack(juteBundled));
		RelationshipRegistry.addRelationship(new ItemStack(lyeWater), new ItemStack(bucketLyeWater));
		RelationshipRegistry.addRelationship(new ItemStack(bucketLyeWater), new ItemStack(lyeWater));
		RelationshipRegistry.addRelationship(new ItemStack(frozenBubble, 1, 1), new ItemStack(frozenBubble));
		RelationshipRegistry.addRelationship(new ItemStack(Items.ender_pearl), new ItemStack(frozenBubble, 1, 1));
		RelationshipRegistry.addRelationship(new ItemStack(frozenBubble, 1, 1), new ItemStack(rawEnder));
		RelationshipRegistry.addRelationship(new ItemStack(potatoStarch), new ItemStack(Items.potato));
		RelationshipRegistry.addRelationship(new ItemStack(potatoStarch), new ItemStack(Blocks.piston));
		RelationshipRegistry.addRelationship(new ItemStack(frozenBubble), new ItemStack(soapSolution));
		RelationshipRegistry.addRelationship(new ItemStack(bucketPumpkinSeedMilk), new ItemStack(pumpkinSeedMilk));
		RelationshipRegistry.addRelationship(new ItemStack(pumpkinSeedMilk), new ItemStack(bucketPumpkinSeedMilk));
	}

	private static void jute()
	{
		juteFibre = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteFibre")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_fibre");
		GameRegistry.registerItem(juteFibre, "juteFibre");
		OreDictionary.registerOre(bastFibreOreDict, new ItemStack(juteFibre));

		juteStalk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteStalk")
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_stalk");
		GameRegistry.registerItem(juteStalk, "juteStalk");

		juteBundled = (BlockRettable) new BlockRettable(juteFibre, 8, 15)
				.setHardness(0.5F)
				.setStepSound(Block.soundTypeGrass)
				.setBlockName(ModInfo.MODID + ".juteBundled")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":jute_block");
		juteBundled.setHarvestLevel("axe", 0);
		GameRegistry.registerBlock(juteBundled, "juteBundled");
		GameRegistry.addShapedRecipe(new ItemStack(juteBundled), "///", "///", "///", '/', new ItemStack(juteStalk));

		Content.dropsModifier.addDropsToBlock(
												new DropsModifier.NEIBlockSpecifier(juteBundled, OreDictionary.WILDCARD_VALUE, new ItemStack(juteBundled, 1, juteBundled.numRettingStages)),
												new DropsModifier.NEIDropSpecifier(new ItemStack(juteBundled.rettedItem), 1f, juteBundled.minRettedItemDrops, juteBundled.maxRettedItemDrops)
				);

		BlockSpecifier doubleFernSpecifier = new BlockSpecifier(Blocks.double_plant, 3)
		{
			@Override
			public boolean metaMatches(int meta)
			{
				return this.meta == BlockDoublePlant.func_149890_d(meta);
			}
		};
		dropsModifier.addDropsToBlock(doubleFernSpecifier, new DropSpecifier(new ItemStack(juteStalk), 1, 3));
	}

	private static void burlap()
	{
		OreDictionary.registerOre(leatherOreDict, new ItemStack(Items.leather));
		recipeModifier.convertInput(new ItemStack(Items.leather), leatherOreDict);

		burlap = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".burlap")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":burlap");
		GameRegistry.registerItem(burlap, "burlap");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(burlap), "~~", "~~", '~', bastFibreOreDict));
		OreDictionary.registerOre(leatherOreDict, new ItemStack(burlap));

		burlapHelmet = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 0)
				.setUnlocalizedName(ModInfo.MODID + ".helmetBurlap")
				.setTextureName("leather_helmet");
		GameRegistry.registerItem(burlapHelmet, "helmetBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapHelmet), "XXX", "X X", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_helmet));

		burlapChestplate = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 1)
				.setUnlocalizedName(ModInfo.MODID + ".chestplateBurlap")
				.setTextureName("leather_chestplate");
		GameRegistry.registerItem(burlapChestplate, "chestplateBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapChestplate), "X X", "XXX", "XXX", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_chestplate));

		burlapLeggings = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 2)
				.setUnlocalizedName(ModInfo.MODID + ".leggingsBurlap")
				.setTextureName("leather_leggings");
		GameRegistry.registerItem(burlapLeggings, "leggingsBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapLeggings), "XXX", "X X", "X X", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_leggings));

		burlapBoots = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 3)
				.setUnlocalizedName(ModInfo.MODID + ".bootsBurlap")
				.setTextureName("leather_boots");
		GameRegistry.registerItem(burlapBoots, "bootsBurlap");
		GameRegistry.addRecipe(new ItemStack(burlapBoots), "X X", "X X", 'X', new ItemStack(burlap));
		recipeModifier.excludeOutput(new ItemStack(Items.leather_boots));
	}

	private static void string()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.string), "~~", '~', bastFibreOreDict));
		GameRegistry.addShapedRecipe(new ItemStack(Items.string), "~~~", '~', kapokTuft);
	}

	private static void kapok()
	{
		OreDictionary.registerOre(woolOreDict, new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));
		recipeModifier.convertInput(new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), woolOreDict);
		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			OreDictionary.registerOre(woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(Blocks.wool, 1, 15 - i));
			recipeModifier.convertInput(new ItemStack(Blocks.wool, 1, 15 - i), woolOreDict + ConstantHelper.dyeColors[i]);
			recipeModifier.excludeOutput(new ItemStack(Blocks.wool, 1, 15 - i));
		}

		kapokTuft = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".kapokTuft")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":kapok_tuft");
		GameRegistry.registerItem(kapokTuft, "kapokTuft");

		kapokBlock = (BlockColored) new BlockColored(Material.cloth)
				.setHardness(0.8F)
				.setStepSound(Block.soundTypeCloth)
				.setBlockName(ModInfo.MODID + ".kapok")
				.setBlockTextureName("wool_colored");
		GameRegistry.registerBlock(kapokBlock, ItemCloth.class, "kapok");
		OreDictionary.registerOre(woolOreDict, new ItemStack(kapokBlock, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addShapedRecipe(new ItemStack(kapokBlock), "~~", "~~", '~', new ItemStack(kapokTuft));

		for (int i = 0; i < ConstantHelper.dyeColors.length; i++)
		{
			OreDictionary.registerOre(woolOreDict + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock, 1, 15 - i));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(kapokBlock, 1, 15 - i), "dye" + ConstantHelper.dyeColors[i], new ItemStack(kapokBlock)));
		}

		BlockSpecifier jungleLeavesSpecifier = new BlockSpecifier(Blocks.leaves, 3)
		{
			@Override
			public boolean metaMatches(int meta)
			{
				return this.meta == (meta & 3);
			}
		};
		dropsModifier.addDropsToBlock(jungleLeavesSpecifier, new DropSpecifier(new ItemStack(kapokTuft), 0.07f, 1, 2));
	}

	private static void fauxFeather()
	{
		OreDictionary.registerOre(featherOreDict, new ItemStack(Items.feather));
		recipeModifier.convertInput(new ItemStack(Items.feather), featherOreDict);

		fauxFeather = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".fauxFeather")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName("feather");
		GameRegistry.registerItem(fauxFeather, "fauxFeather");
		OreDictionary.registerOre(featherOreDict, new ItemStack(fauxFeather));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fauxFeather), new ItemStack(kapokTuft), plasticRodOreDict));
	}

	private static void palliasse()
	{
		bedStrawBlock = (BlockBedGeneric) new BlockBedGeneric()
				.setHardness(0.2F)
				.setBlockName(ModInfo.MODID + ".bedStraw")
				.setBlockTextureName(ModInfo.MODID_LOWER + ":straw_bed");
		bedStrawItem = (ItemBedGeneric) new ItemBedGeneric(bedStrawBlock)
				.setMaxStackSize(1)
				.setUnlocalizedName(ModInfo.MODID + ".bedStraw")
				.setTextureName(ModInfo.MODID_LOWER + ":straw_bed");
		bedStrawBlock.setBedItem(bedStrawItem);
		GameRegistry.registerBlock(bedStrawBlock, null, "bedStraw");
		GameRegistry.registerItem(bedStrawItem, "bedStraw");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bedStrawItem), "~~~", "===", '~', new ItemStack(Blocks.hay_block), '=', "plankWood"));
	}

	private static void milk()
	{
		OreDictionary.registerOre(milkOreDict, new ItemStack(Items.milk_bucket));
		recipeModifier.convertInput(new ItemStack(Items.milk_bucket), milkOreDict);

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
		OreDictionary.registerOre(milkOreDict, new ItemStack(bucketPumpkinSeedMilk));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidPumpkinSeedMilk, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketPumpkinSeedMilk), new ItemStack(Items.bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(bucketPumpkinSeedMilk), // output
										new ItemStack(Items.water_bucket),
										new ItemStack(Items.pumpkin_seeds),
										new ItemStack(Items.pumpkin_seeds),
										new ItemStack(Items.sugar));
		craftingModifier.addInputsToRemoveForOutput(new ItemStack(bucketPumpkinSeedMilk), // output
													new ItemStack(Items.water_bucket));
	}

	private static void fossils()
	{
		// bones as a rare drop from stone
		dropsModifier.addDropsToBlock(new BlockSpecifier(Blocks.stone), new DropSpecifier(new ItemStack(Items.bone), 0.01f, 1, 2));
	}

	private static void eggReplacers()
	{
		OreDictionary.registerOre(eggOreDict, new ItemStack(Items.egg));
		recipeModifier.convertInput(new ItemStack(Items.egg), eggOreDict);

		appleSauce = new ItemFoodContainered(3, 1f, false)
				.setUnlocalizedName(ModInfo.MODID + ".appleSauce")
				.setCreativeTab(CreativeTabs.tabFood)
				.setTextureName(ModInfo.MODID_LOWER + ":apple_sauce")
				.setContainerItem(Items.bowl);
		GameRegistry.registerItem(appleSauce, "appleSauce");
		if (IntegrationHandler.modExists(IntegrationHandler.MODID_HARVESTCRAFT))
		{
			appleSauce = HarvestCraft.getItem("applesauceItem");
		}
		else
		{
			GameRegistry.addShapelessRecipe(new ItemStack(appleSauce), new ItemStack(Items.apple), new ItemStack(Items.bowl));
		}
		OreDictionary.registerOre(eggOreDict, new ItemStack(appleSauce));

		potatoStarch = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".potatoStarch")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":potato_starch");
		GameRegistry.registerItem(potatoStarch, "potatoStarch");
		OreDictionary.registerOre(eggOreDict, new ItemStack(potatoStarch));
		ItemStack potatoCrusher = new ItemStack(Blocks.piston);
		GameRegistry.addShapelessRecipe(new ItemStack(potatoStarch), potatoCrusher, new ItemStack(Items.potato));
		craftingModifier.addInputsToKeepForOutput(new ItemStack(potatoStarch), potatoCrusher);
	}

	private static void resin()
	{
		OreDictionary.registerOre(slimeballOreDict, new ItemStack(Items.slime_ball));
		recipeModifier.convertInput(new ItemStack(Items.slime_ball), slimeballOreDict);

		resin = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".resin")
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":resin");
		GameRegistry.registerItem(resin, "resin");
		OreDictionary.registerOre(slimeballOreDict, new ItemStack(resin));

		BlockSpecifier spruceLogSpecifier = new BlockSpecifier(Blocks.log, 1)
		{
			@Override
			public boolean metaMatches(int meta)
			{
				return this.meta == BlockLog.func_150165_c(meta);
			}
		};
		dropsModifier.addDropsToBlock(spruceLogSpecifier, new DropSpecifier(new ItemStack(resin), 0.1f));
	}

	private static void vegetableOil()
	{
		seedSunflower = new ItemFood(1, 0.05f, false)
				.setUnlocalizedName(ModInfo.MODID + ".seedSunflower")
				.setCreativeTab(CreativeTabs.tabFood)
				.setTextureName(ModInfo.MODID_LOWER + ":sunflower_seeds");
		GameRegistry.registerItem(seedSunflower, "seedSunflower");

		if (IntegrationHandler.modExists(IntegrationHandler.MODID_HARVESTCRAFT))
		{
			seedSunflower = HarvestCraft.getItem("sunflowerseedsItem");
		}

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
		dropsModifier.addDropsToBlock(sunflowerTopSpecifier, sunflowerDropSpecifier);

		oilSunflower = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".oilSunflower")
				.setCreativeTab(CreativeTabs.tabFood)
				.setTextureName(ModInfo.MODID_LOWER + ":sunflower_oil")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(oilSunflower, "oilSunflower");
		OreDictionary.registerOre(vegetableOilOreDict, new ItemStack(oilSunflower));
		registerOil(new ItemStack(oilSunflower), new ItemStack(seedSunflower));
	}

	public static void registerOil(ItemStack output, ItemStack... inputs)
	{
		ItemStack oilPresser = !IntegrationHandler.modExists(IntegrationHandler.MODID_HARVESTCRAFT) ? new ItemStack(Blocks.heavy_weighted_pressure_plate) : new ItemStack(HarvestCraft.getItem("juicerItem"));
		List<ItemStack> recipeInputs = new ArrayList<ItemStack>(Arrays.asList(inputs));
		recipeInputs.add(0, oilPresser);
		if (output.getItem().hasContainerItem(output))
		{
			recipeInputs.add(output.getItem().getContainerItem(output));
		}
		GameRegistry.addShapelessRecipe(output, (Object[]) recipeInputs.toArray(new ItemStack[recipeInputs.size()]));
		if (!IntegrationHandler.modExists(IntegrationHandler.MODID_HARVESTCRAFT))
		{
			craftingModifier.addInputsToKeepForOutput(output, oilPresser);
		}

		OreDictionary.registerOre(vegetableOilOreDict, output.copy());
	}

	private static void ink()
	{
		OreDictionary.registerOre(blackPigmentOreDict, charcoal.copy());

		waxVegetable = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".waxVegetable")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_wax");
		GameRegistry.registerItem(waxVegetable, "waxVegetable");
		OreDictionary.registerOre(waxOreDict, new ItemStack(waxVegetable));
		OreDictionary.registerOre(waxOreDictForestry, new ItemStack(waxVegetable));
		for (ItemStack vegetableOil : OreDictionary.getOres(vegetableOilOreDict))
		{
			GameRegistry.addSmelting(vegetableOil.copy(), new ItemStack(waxVegetable), 0.2f);
		}

		rosin = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".rosin")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":rosin");
		GameRegistry.registerItem(rosin, "rosin");
		GameRegistry.addSmelting(resin, new ItemStack(rosin), 0.2f);

		inkVegetableOil = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOil")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_oil_ink")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(inkVegetableOil, "inkVegetableOil");
		OreDictionary.registerOre(inkSacOreDict, inkVegetableOil);
		GameRegistry.addRecipe(new ShapelessOreRecipe(inkVegetableOil, vegetableOilOreDict, waxOreDict, rosin, blackPigmentOreDict));
		craftingModifier.addInputsToRemoveForOutput(new ItemStack(inkVegetableOil), new ItemStack(oilSunflower));
	}

	private static void bioplastic()
	{
		bioplastic = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".bioplastic")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":bioplastic");
		GameRegistry.registerItem(bioplastic, "bioplastic");
		OreDictionary.registerOre(plasticOreDict, bioplastic);
		GameRegistry.addSmelting(potatoStarch, new ItemStack(bioplastic, 2), 0.35f);

		plasticRod = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".plasticRod")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":plastic_rod");
		GameRegistry.registerItem(plasticRod, "plasticRod");
		OreDictionary.registerOre(plasticRodOreDict, plasticRod);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(plasticRod, 4), "p", "p", 'p', plasticOreDict));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.blaze_rod), plasticRodOreDict, new ItemStack(rosin), waxOreDict, new ItemStack(Items.flint_and_steel, 1, OreDictionary.WILDCARD_VALUE)));
		craftingModifier.addInputsToKeepForOutput(new ItemStack(Items.blaze_rod), new ItemStack(Items.flint_and_steel, 1, OreDictionary.WILDCARD_VALUE));
	}

	private static void compost()
	{
		OreDictionary.registerOre(rottenOreDict, new ItemStack(Items.rotten_flesh));
		recipeModifier.convertInput(new ItemStack(Items.rotten_flesh), rottenOreDict);

		composter = new BlockComposter()
				.setHardness(2.5F)
				.setStepSound(Block.soundTypeWood)
				.setBlockName(ModInfo.MODID + ".composter")
				.setCreativeTab(CreativeTabs.tabInventory)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":composter");
		GameRegistry.registerBlock(composter, "composter");
		GameRegistry.registerTileEntity(TileEntityComposter.class, ModInfo.MODID + ".composter");
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			RenderComposter composterRenderer = new RenderComposter();
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityComposter.class, composterRenderer);
			MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(composter), composterRenderer);
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(composter), "/c/", "/ /", '/', stickOreDict, 'c', new ItemStack(Blocks.chest)));

		rottenPlants = new ItemFood(4, 0.1F, true)
				.setPotionEffect(Potion.hunger.id, 30, 0, 0.8F)
				.setUnlocalizedName(ModInfo.MODID + ".rottenPlants")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":rotten_plants");
		GameRegistry.registerItem(rottenPlants, "rottenPlants");
		OreDictionary.registerOre(rottenOreDict, rottenPlants);

		fertilizer = new ItemFertilizer()
				.setUnlocalizedName(ModInfo.MODID + ".fertilizer")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":fertilizer");
		GameRegistry.registerItem(fertilizer, "fertilizer");
		OreDictionary.registerOre(fertilizerOreDict, fertilizer);
		OreDictionary.registerOre(brownDyeOreDict, fertilizer);

		compost = new BlockCompost()
				.setHardness(0.5F)
				.setStepSound(Block.soundTypeGravel)
				.setBlockName(ModInfo.MODID + ".compost")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":compost");
		GameRegistry.registerBlock(compost, "compost");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fertilizer, 8), new ItemStack(compost), saltpeterOreDict));
	}

	private static void soap()
	{
		OreDictionary.registerOre(woodAshOreDict, charcoal.copy());

		fluidLyeWater = new Fluid(ModInfo.MODID + ".lyeWater");
		FluidRegistry.registerFluid(fluidLyeWater);
		lyeWater = new BlockLyeWater(fluidLyeWater)
				.setBlockName(ModInfo.MODID + ".lyeWater");
		fluidLyeWater.setBlock(lyeWater);
		fluidLyeWater.setUnlocalizedName(lyeWater.getUnlocalizedName());
		GameRegistry.registerBlock(lyeWater, "lyeWater");

		bucketLyeWater = new ItemBucketGeneric(lyeWater)
				.setUnlocalizedName(ModInfo.MODID + ".bucketLyeWater")
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":lye_water_bucket")
				.setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucketLyeWater, "bucketLyeWater");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidLyeWater, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketLyeWater), new ItemStack(Items.bucket));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bucketLyeWater), new ItemStack(Items.water_bucket), woodAshOreDict, woodAshOreDict, woodAshOreDict));
		craftingModifier.addInputsToRemoveForOutput(new ItemStack(bucketLyeWater), new ItemStack(Items.water_bucket));

		soap = new ItemSoap()
				.setUnlocalizedName(ModInfo.MODID + ".soap")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":soap");
		GameRegistry.registerItem(soap, "soap");
		OreDictionary.registerOre(soapOreDict, new ItemStack(soap, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(soap),
				new ItemStack(bucketLyeWater),
				vegetableOilOreDict,
				new ItemStack(rosin)));

		soapSolution = new ItemSoapSolution()
				.setUnlocalizedName(ModInfo.MODID + ".soapSolution")
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":soap_solution")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(soapSolution, "soapSolution");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(soapSolution),
				soapOreDict,
				new ItemStack(Items.water_bucket),
				new ItemStack(Items.sugar),
				new ItemStack(Items.glass_bottle)));
		craftingModifier.addInputsToKeepForOutput(new ItemStack(soapSolution), new ItemStack(soap, 1, OreDictionary.WILDCARD_VALUE));
	}

	private static void frozenBubble()
	{
		ItemStack pufferFish = new ItemStack(Items.fish, 1, ItemFishFood.FishType.PUFFERFISH.ordinal());
		OreDictionary.registerOre(pufferFishOreDict, pufferFish);
		recipeModifier.convertInput(pufferFish, pufferFishOreDict);

		frozenBubble = new ItemFrozenBubble()
				.setUnlocalizedName(ModInfo.MODID + ".frozenBubble")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":frozen_bubble");
		GameRegistry.registerItem(frozenBubble, "frozenBubble");
		OreDictionary.registerOre(pufferFishOreDict, frozenBubble);
		GameRegistry.addShapedRecipe(new ItemStack(frozenBubble), "iii", "isi", "iii", 'i', Blocks.ice, 's', soapSolution);
		GameRegistry.addShapelessRecipe(new ItemStack(frozenBubble), Blocks.packed_ice, soapSolution);

		EntityRegistry.registerModEntity(EntityBubble.class, "bubble", EntityRegistry.findGlobalUniqueEntityId(), ModInfo.MODID, 80, 10, true);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			RenderingRegistry.registerEntityRenderingHandler(EntityBubble.class, new RenderSnowball(frozenBubble));
		}
		BlockDispenser.dispenseBehaviorRegistry.putObject(soapSolution, new EntityBubbleDispenserBehavior());
	}

	private static void ender()
	{
		encrustedObsidian = new BlockEncrustedObsidian()
				.setHardness(50.0F)
				.setResistance(2000.0F)
				.setStepSound(Block.soundTypePiston)
				.setBlockName(ModInfo.MODID + ".encrustedObsidian")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":encrusted_obsidian");
		GameRegistry.registerBlock(encrustedObsidian, "encrustedObsidian");
		encrustedObsidian.setHarvestLevel("pickaxe", 3);
		GameRegistry.addShapelessRecipe(new ItemStack(encrustedObsidian, 2), Items.diamond, Blocks.obsidian, Blocks.obsidian, Items.emerald);

		enderRift = new BlockEnderRift()
				.setHardness(-1.0F)
				.setResistance(6000000.0F)
				.setBlockName(ModInfo.MODID + ".enderRift");
		GameRegistry.registerBlock(enderRift, "enderRift");
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnderRift.class, new RenderEnderRift());
		}
		GameRegistry.registerTileEntity(TileEntityEnderRift.class, ModInfo.MODID + ".enderRift");

		fluidRawEnder = new Fluid(ModInfo.MODID + ".rawEnder")
				.setLuminosity(3)
				.setViscosity(3000)
				.setDensity(4000);
		FluidRegistry.registerFluid(fluidRawEnder);
		rawEnder = new BlockRawEnder(fluidRawEnder)
				.setBlockName(ModInfo.MODID + ".rawEnder");
		fluidRawEnder.setBlock(rawEnder);
		fluidRawEnder.setUnlocalizedName(rawEnder.getUnlocalizedName());
		GameRegistry.registerBlock(rawEnder, "rawEnder");

		bucketRawEnder = new ItemBucketGeneric(rawEnder)
				.setUnlocalizedName(ModInfo.MODID + ".bucketRawEnder")
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":raw_ender_bucket")
				.setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucketRawEnder, "bucketRawEnder");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidRawEnder, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketRawEnder), new ItemStack(Items.bucket));

		GameRegistry.addShapelessRecipe(new ItemStack(Items.ender_pearl), new ItemStack(frozenBubble), new ItemStack(bucketRawEnder));
	}

	private static void poison()
	{
		OreDictionary.registerOre(poisonousOreDict, Items.spider_eye);
		recipeModifier.convertInput(new ItemStack(Items.spider_eye), poisonousOreDict);
		recipeModifier.excludeOutput(new ItemStack(Items.fermented_spider_eye));

		OreDictionary.registerOre(fermentedOreDict, Items.fermented_spider_eye);
		recipeModifier.convertInput(new ItemStack(Items.fermented_spider_eye), fermentedOreDict);

		falseMorel = new ItemFood(2, 0.8F, false)
				.setPotionEffect(Potion.poison.id, 5, 0, 1.0F)
				.setPotionEffect(PotionHelper.spiderEyeEffect)
				.setUnlocalizedName(ModInfo.MODID + ".falseMorel")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":false_morel");
		GameRegistry.registerItem(falseMorel, "falseMorel");
		OreDictionary.registerOre(poisonousOreDict, falseMorel);

		DropSpecifier dontDropWhenSilkTouching = new DropSpecifier(new ItemStack(falseMorel), 0.15f)
		{
			@Override
			public boolean shouldDrop(EntityPlayer harvester, int fortuneLevel, boolean isSilkTouching)
			{
				return !isSilkTouching && super.shouldDrop(harvester, fortuneLevel, isSilkTouching);
			}
		};
		dropsModifier.addDropsToBlock(new BlockSpecifier(Blocks.mycelium), dontDropWhenSilkTouching);

		falseMorelFermented = new Item()
				.setPotionEffect(PotionHelper.fermentedSpiderEyeEffect)
				.setUnlocalizedName(ModInfo.MODID + ".falseMorelFermented")
				.setCreativeTab(CreativeTabs.tabBrewing)
				.setTextureName(ModInfo.MODID_LOWER + ":false_morel_fermented");
		GameRegistry.registerItem(falseMorelFermented, "falseMorelFermented");
		OreDictionary.registerOre(fermentedOreDict, falseMorelFermented);
		GameRegistry.addShapelessRecipe(new ItemStack(falseMorelFermented), new ItemStack(falseMorel), new ItemStack(Blocks.brown_mushroom), new ItemStack(Items.sugar));
	}

	private static void heads()
	{
		papierMache = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".papierMache")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":papier_mache");
		GameRegistry.registerItem(papierMache, "papierMache");
		GameRegistry.addShapelessRecipe(new ItemStack(papierMache, 8), new ItemStack(Items.water_bucket), new ItemStack(potatoStarch), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper));

		mobHeadBlank = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".mobHeadBlank")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":blank_mob_head");
		GameRegistry.registerItem(mobHeadBlank, "mobHeadBlank");
		GameRegistry.addShapedRecipe(new ItemStack(mobHeadBlank), "///", "/m/", "///", '/', new ItemStack(papierMache), 'm', new ItemStack(Blocks.melon_block));

		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadSkeleton.copy(), "ddd", "dhd", "ddd", 'd', "dyeLightGray", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadWitherSkeleton.copy(), "ddd", "dhd", "ddd", 'd', "dyeBlack", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadSteve.copy(), "ddd", "dhd", "ddd", 'd', "dyeBrown", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadZombie.copy(), "ddd", "dhd", "ddd", 'd', "dyeGreen", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadCreeper.copy(), "ddd", "dhd", "ddd", 'd', "dyeLime", 'h', mobHeadBlank));
	}

	private static void gunpowder()
	{
		sulfur = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".sulfur")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":sulfur");
		GameRegistry.registerItem(sulfur, "sulfur");
		OreDictionary.registerOre(sulfurOreDict, sulfur);

		dropsModifier.addDropsToBlock(new BlockSpecifier(Blocks.netherrack), new DropSpecifier(new ItemStack(sulfur), 0.02f));

		saltpeter = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".saltpeter")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":saltpeter");
		GameRegistry.registerItem(saltpeter, "saltpeter");
		OreDictionary.registerOre(saltpeterOreDict, saltpeter);

		dropsModifier.addDropsToBlock(new BlockSpecifier(Blocks.sandstone), new DropSpecifier(new ItemStack(saltpeter), 0.02f, 1, 3));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder), charcoal.copy(), sulfurOreDict, saltpeterOreDict));
	}
}
