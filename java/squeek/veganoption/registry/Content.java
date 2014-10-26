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
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.*;
import squeek.veganoption.blocks.renderers.RenderEnderRift;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.entities.EntityBubbleDispenserBehavior;
import squeek.veganoption.helpers.ConstantHelper;
import squeek.veganoption.integration.HarvestCraft;
import squeek.veganoption.items.ItemBedGeneric;
import squeek.veganoption.items.ItemBucketGeneric;
import squeek.veganoption.items.ItemFertilizer;
import squeek.veganoption.items.ItemFrozenBubble;
import squeek.veganoption.items.ItemSoapSolution;
import squeek.veganoption.modifications.CraftingModifier;
import squeek.veganoption.modifications.DropsModifier;
import squeek.veganoption.modifications.DropsModifier.BlockSpecifier;
import squeek.veganoption.modifications.DropsModifier.DropSpecifier;
import squeek.veganoption.modifications.RecipeModifier;
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
	public static ItemBucketMilk bucketPumpkinSeedMilk;

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
	public static final String fluidLyeWaterName = "lyeWater";
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
	public static final String fluidRawEnderName = "rawEnder";
	public static Block rawEnder;
	public static Item bucketRawEnder;

	// oredict
	public static final String leatherOreDict = "materialLeather";
	public static final String woolOreDict = "materialBedding";
	public static final String featherOreDict = "materialFeather";
	public static final String milkOreDict = "listAllmilk"; // HarvestCraft's oredict entry
	public static final String eggOreDict = "listAllegg"; // HarvestCraft's oredict entry
	public static final String slimeballOreDict = "slimeball"; // Forge's oredict entry
	public static final String vegetableOilOreDict = "foodOliveoil"; // HarvestCraft's oredict entry
	public static final String waxOreDict = "materialWax";
	public static final String inkSacOreDict = "dyeBlack"; // Forge's oredict entry
	public static final String plasticOreDict = "materialPlastic"; // TODO: use MFR's oredict entry (if it exists)
	public static final String plasticRodOreDict = "stickPlastic";
	public static final String rottenOreDict = "materialRotten";
	public static final String fertilizerOreDict = "fertilizer";
	public static final String pufferFishOreDict = "reagentWaterBreathing";
	public static final String soapOreDict = "soap";

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
	}

	public static void finish()
	{
		recipeModifier.replaceRecipes();
	}

	private static void jute()
	{
		juteFibre = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteFibre")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_fibre");
		GameRegistry.registerItem(juteFibre, "juteFibre");
		OreDictionary.registerOre("fibreBast", new ItemStack(juteFibre));

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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(burlap), "~~", "~~", '~', "fibreBast"));
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.string), "~~", '~', "fibreBast"));
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
		dropsModifier.addDropsToBlock(jungleLeavesSpecifier, new DropSpecifier(new ItemStack(kapokTuft), 0.1f, 0, 2));
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
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fauxFeather), new ItemStack(kapokTuft), "stickPlastic"));
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

		bucketPumpkinSeedMilk = (ItemBucketMilk) new ItemBucketMilk()
				.setUnlocalizedName(ModInfo.MODID + ".bucketPumpkinSeedMilk")
				.setContainerItem(Items.bucket)
				.setTextureName("bucket_milk");
		GameRegistry.registerItem(bucketPumpkinSeedMilk, "bucketPumpkinSeedMilk");
		OreDictionary.registerOre(milkOreDict, new ItemStack(bucketPumpkinSeedMilk));
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

		if (HarvestCraft.exists)
		{
			appleSauce = HarvestCraft.getItem("applesauceItem");
		}
		else
		{
			appleSauce = new ItemFood(3, 1f, false)
					.setUnlocalizedName(ModInfo.MODID + ".appleSauce")
					.setCreativeTab(CreativeTabs.tabFood)
					.setTextureName(ModInfo.MODID_LOWER + ":apple_sauce")
					.setContainerItem(Items.bowl);
			GameRegistry.registerItem(appleSauce, "appleSauce");
			GameRegistry.addShapelessRecipe(new ItemStack(appleSauce), new ItemStack(Items.apple), new ItemStack(Items.bowl));
		}
		OreDictionary.registerOre(eggOreDict, new ItemStack(appleSauce));

		potatoStarch = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".potatoStarch")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":potato_starch")
				.setContainerItem(Items.bowl);
		GameRegistry.registerItem(potatoStarch, "potatoStarch");
		OreDictionary.registerOre(eggOreDict, new ItemStack(potatoStarch));
		ItemStack potatoCrusher = !HarvestCraft.exists ? new ItemStack(Blocks.heavy_weighted_pressure_plate) : new ItemStack(HarvestCraft.getItem("mortarandpestleItem"));
		GameRegistry.addShapelessRecipe(new ItemStack(potatoStarch), potatoCrusher, new ItemStack(Items.water_bucket), new ItemStack(Items.potato), new ItemStack(Items.bowl));
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
		if (HarvestCraft.exists)
		{
			seedSunflower = HarvestCraft.getItem("sunflowerseedsItem");
		}
		else
		{
			seedSunflower = new ItemFood(1, 0.05f, false)
					.setUnlocalizedName(ModInfo.MODID + ".seedSunflower")
					.setCreativeTab(CreativeTabs.tabFood)
					.setTextureName(ModInfo.MODID_LOWER + ":sunflower_seeds");
			GameRegistry.registerItem(seedSunflower, "seedSunflower");
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
		ItemStack oilPresser = !HarvestCraft.exists ? new ItemStack(Blocks.heavy_weighted_pressure_plate) : new ItemStack(HarvestCraft.getItem("juicerItem"));
		List<ItemStack> recipeInputs = new ArrayList<ItemStack>(Arrays.asList(inputs));
		recipeInputs.add(0, oilPresser);
		if (output.getItem().hasContainerItem(output))
		{
			recipeInputs.add(output.getItem().getContainerItem(output));
		}
		GameRegistry.addShapelessRecipe(output, (Object[]) recipeInputs.toArray(new ItemStack[recipeInputs.size()]));
		if (!HarvestCraft.exists)
		{
			craftingModifier.addInputsToKeepForOutput(output, oilPresser);
		}

		OreDictionary.registerOre(vegetableOilOreDict, output.copy());
	}

	private static void ink()
	{
		waxVegetable = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".waxVegetable")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_wax");
		GameRegistry.registerItem(waxVegetable, "waxVegetable");
		OreDictionary.registerOre(waxOreDict, new ItemStack(waxVegetable));
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

		ItemStack charcoal = new ItemStack(Items.coal, 1, 1);

		inkVegetableOil = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".inkVegetableOil")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":vegetable_oil_ink")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(inkVegetableOil, "inkVegetableOil");
		OreDictionary.registerOre(inkSacOreDict, inkVegetableOil);
		GameRegistry.addRecipe(new ShapelessOreRecipe(inkVegetableOil, vegetableOilOreDict, waxOreDict, rosin, charcoal));
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(plasticRod), "p", "p", 'p', plasticOreDict));

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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(composter), "/c/", "/ /", '/', "stickWood", 'c', new ItemStack(Blocks.chest)));

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

		compost = new BlockCompost()
				.setHardness(0.5F)
				.setStepSound(Block.soundTypeGravel)
				.setBlockName(ModInfo.MODID + ".compost")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":compost");
		GameRegistry.registerBlock(compost, "compost");
		GameRegistry.addShapelessRecipe(new ItemStack(fertilizer, 12), new ItemStack(compost), new ItemStack(Items.bone));
	}

	private static void soap()
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
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":lye_water_bucket")
				.setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucketLyeWater, "bucketLyeWater");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidLyeWater, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketLyeWater), new ItemStack(Items.bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(bucketLyeWater), charcoal, new ItemStack(Items.water_bucket));
		craftingModifier.addInputsToRemoveForOutput(new ItemStack(bucketLyeWater), new ItemStack(Items.water_bucket));

		soap = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".soap")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":soap");
		GameRegistry.registerItem(soap, "soap");
		OreDictionary.registerOre(soapOreDict, soap);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(soap), // output
		new ItemStack(bucketLyeWater),
				vegetableOilOreDict,
				new ItemStack(rosin)));

		soapSolution = new ItemSoapSolution()
				.setUnlocalizedName(ModInfo.MODID + ".soapSolution")
				.setCreativeTab(CreativeTabs.tabMisc)
				.setTextureName(ModInfo.MODID_LOWER + ":soap_solution")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(soapSolution, "soapSolution");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(soapSolution), // output
		soapOreDict,
				new ItemStack(Items.water_bucket),
				new ItemStack(Items.sugar),
				new ItemStack(Items.glass_bottle)));
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

		fluidRawEnder = new Fluid(ModInfo.MODID + ".rawEnder");
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
}
