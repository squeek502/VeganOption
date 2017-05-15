package squeek.veganoption.content;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;

public class ContentHelper
{
	// entity ids; mod-specific, don't need to be universally unique
	public static final int ENTITYID_BUBBLE = 0;
	public static final int ENTITYID_PLASTIC_EGG = 1;

	// helper itemstacks for vanilla stuff
	public static final ItemStack charcoal = new ItemStack(Items.COAL, 1, 1);
	public static final ItemStack inkSac = new ItemStack(Items.DYE, 1, 0);
	public static final ItemStack boneMeal = new ItemStack(Items.DYE, 1, 15);

	// oredict
	public static final String leatherOreDict = "leather"; // Forge's oredict entry
	public static final String woolOreDict = "materialBedding";
	public static final String featherOreDict = "feather"; // Forge's oredict entry
	public static final String bastFibreOreDict = "materialFiber";
	public static final String milkOreDict = "listAllmilk"; // HarvestCraft's oredict entry
	public static final String eggForgeOreDict = "egg"; // Forge's oredict entry
	public static final String eggObjectOreDict = "objectEgg"; // not for food equivalents
	public static final String eggBakingOreDict = "bakingEgg";
	public static final String eggFoodOreDict = "listAllegg"; // HarvestCraft's oredict entry
	public static final String slimeballOreDict = "slimeball"; // Forge's oredict entry
	public static final String resinOreDict = "resin";
	public static final String resinMaterialOreDict = "materialResin"; // ElectricalAge's oredict entry
	public static final String rosinOreDict = "rosin";
	public static final String rosinMaterialOreDict = "materialRosin";
	public static final String vegetableOilOreDict = "foodOliveoil"; // HarvestCraft's oredict entry
	public static final String waxOreDict = "materialWax";
	public static final String waxOreDictForestry = "itemBeeswax"; // Forestry's oredict entry
	public static final String waxOreDictHarvestCraft = "materialPressedwax"; // HarvestCraft's oredict entry
	public static final String blackDyeOreDict = "dyeBlack"; // Forge's oredict entry
	public static final String blackInkOreDict = "inkBlack";
	public static final String whiteDyeOreDict = "dyeWhite"; // Forge's oredict entry
	public static final String whiteInkOreDict = "inkWhite";
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
	public static final String whitePigmentOreDict = "pigmentWhite";
	public static final String woodPlankOreDict = "plankWood"; // Forge's oredict entry
	public static final String starchOreDict = "starch";
	public static final String kapokOreDict = "materialFluffy";
	public static final String sunflowerSeedOreDict = "cropSunflower"; // HarvestCraft's oredict entry
	public static final String oilPresserOreDict = "presserOil";
	public static final String vegetableOilSourceOreDict = "sourceVegetableOil";
	public static final String tearOreDict = "reagentTear";
	public static final String goldNuggetOreDict = "nuggetGold"; // Forge's oredict entry
	public static final String plantMilkSourceOreDict = "sourcePlantMilk";
	public static final String bbqSauceOreDict = "foodBBQSauce";
	public static final String wheatFlourOreDict = "flourWheat";
	public static final String wheatDoughOreDict = "doughWheat";
	public static final String rawSeitanOreDict = "seitanRaw";
	public static final String leatherBootsOreDict = "bootsLeather";
	public static final String leatherLeggingsOreDict = "leggingsLeather";
	public static final String leatherChestplateOreDict = "chestplateLeather";
	public static final String leatherHelmetOreDict = "helmetLeather";

	// raw and cooked meats from HarvestCraft
	public static final String rawMeatOreDict = "listAllmeatraw";
	public static final String cookedMeatOreDict = "listAllmeatcooked";
	public static final String rawBeefOreDict = "listAllbeefraw";
	public static final String cookedBeefOreDict = "listAllbeefcooked";
	public static final String rawChickenOreDict = "listAllchickenraw";
	public static final String cookedChickenOreDict = "listAllchickencooked";
	public static final String rawFishOreDict = "listAllfishraw";
	public static final String cookedFishOreDict = "listAllfishcooked";
	public static final String rawMuttonOreDict = "listAllmuttonraw";
	public static final String cookedMuttonOreDict = "listAllmuttoncooked";
	public static final String rawPorkOreDict = "listAllporkraw";
	public static final String cookedPorkOreDict = "listAllporkcooked";
	public static final String rawRabbitOreDict = "listAllrabbitraw";
	public static final String cookedRabbitOreDict = "listAllrabbitcooked";
	public static final String rawTurkeyOreDict = "listAllturkeyraw";
	public static final String cookedTurkeyOreDict = "listAllturkeycooked";
	public static final String rawVenisonOreDict = "listAllvenisonraw";
	public static final String cookedVenisonOreDict = "listAllvenisoncooked";

	public static final String[] harvestCraftRawMeatOreDicts = new String[]{
		rawMeatOreDict, rawBeefOreDict, rawChickenOreDict, rawFishOreDict, rawMuttonOreDict,
		rawPorkOreDict, rawRabbitOreDict, rawTurkeyOreDict, rawVenisonOreDict
	};
	public static final String[] harvestCraftCookedMeatOreDicts = new String[]{
		cookedMeatOreDict, cookedBeefOreDict, cookedChickenOreDict, cookedFishOreDict, cookedMuttonOreDict,
		cookedPorkOreDict, cookedRabbitOreDict, cookedTurkeyOreDict, cookedVenisonOreDict
	};

	// various vegetable oil sources from other mods
	public static final String grapeSeedOreDict = "seedGrape";
	public static final String soybeanOreDict = "cropSoybean";
	public static final String cottonSeedOreDict = "seedCotton";
	public static final String coconutOreDict = "cropCoconut";
	public static final String oliveOreDict = "cropOlive";
	public static final String cornOreDict = "cropCorn";
	public static final String nutOreDict = "listAllnut";
	public static final String teaSeedOreDict = "seedTea";
	public static final String avocadoOreDict = "cropAvocado";

	// various plant milk sources from other mods
	public static final String almondOreDict = "cropAlmond";
	public static final String oatOreDict = "cropOats";
	public static final String riceOreDict = "cropRice";

	public static void addOreSmelting(String inputOreName, ItemStack output, float xp)
	{
		for (ItemStack ore : OreDictionary.getOres(inputOreName))
		{
			GameRegistry.addSmelting(ore.copy(), output, xp);
		}
	}

	public static void addOreSmelting(String inputOreName, ItemStack output)
	{
		addOreSmelting(inputOreName, output, 0.2F);
	}

	public static void remapOre(String from, String to)
	{
		for (ItemStack ore : OreDictionary.getOres(from))
		{
			OreDictionary.registerOre(to, ore.copy());
		}
	}

	/**
	 * Registers a typical (no meta, no special inventory variant or anything) item model.
	 *
	 * @param item The item
	 */
	@SideOnly(Side.CLIENT)
	public static void registerTypicalItemModel(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	/**
	 * Registers a model with a specific name for the given ItemStack
	 *
	 * @param stack The itemstack
	 * @param name The specific name
	 */
	@SideOnly(Side.CLIENT)
	public static void registerTypicalItemStackModel(ItemStack stack, String name)
	{
		ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getItemDamage(), new ModelResourceLocation(name, "inventory"));
	}

	/**
	 * Registers an ItemStack for each valid state of the block
	 */
	@SideOnly(Side.CLIENT)
	public static void registerTypicalBlockItemModels(Block block, String name)
	{
		for (IBlockState state : block.getBlockState().getValidStates())
		{
			int meta = block.getMetaFromState(state);
			registerTypicalItemStackModel(new ItemStack(block, 1, meta), name);
		}
	}

	/**
	 * Registers a ColorHandler for both the block and its item version
	 */
	@SideOnly(Side.CLIENT)
	public static <T extends IBlockColor & IItemColor> void registerSharedColorHandler(T colorHandler, Block block)
	{
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(colorHandler, block);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorHandler, block);
	}

	/**
	 * Maps the fluid to our custom blockstate for all fluids (fluids.json).
	 *
	 * @param fluid The fluid
	 * @param variantName The variant for the fluid
	 */
	@SideOnly(Side.CLIENT)
	public static void registerFluidMapperAndMeshDef(Block fluid, final String variantName)
	{
		final ModelResourceLocation loc = new ModelResourceLocation(ModInfo.MODID_LOWER + ":fluids", variantName);
		Item itemblock = Item.getItemFromBlock(fluid);
		ModelBakery.registerItemVariants(itemblock);
		ModelLoader.setCustomMeshDefinition(itemblock, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return loc;
			}
		});
		ModelLoader.setCustomStateMapper(fluid, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return loc;
			}
		});
	}
}
