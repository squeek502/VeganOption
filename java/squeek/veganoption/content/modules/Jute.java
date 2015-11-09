package squeek.veganoption.content.modules;

import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockJutePlant;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.items.ItemBlockJutePlant;
import squeek.veganoption.items.ItemSeedsGeneric;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class Jute implements IContentModule
{
	public static BlockRettable juteBundled;
	public static Block jutePlant;
	public static Item juteStalk;
	public static Item juteFibre;
	public static Item juteSeeds;
	public static final int FERN_METADATA = 3;
	public static DropSpecifier juteDrops;

	@Override
	public void create()
	{
		juteFibre = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteFibre")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_fibre");
		GameRegistry.registerItem(juteFibre, "juteFibre");

		juteStalk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteStalk")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_stalk");
		GameRegistry.registerItem(juteStalk, "juteStalk");

		juteBundled = (BlockRettable) new BlockRettable(juteFibre, 8, 15)
				.setHardness(0.5F)
				.setStepSound(Block.soundTypeGrass)
				.setBlockName(ModInfo.MODID + ".juteBundled")
				.setCreativeTab(VeganOption.creativeTab)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":jute_block");
		juteBundled.setHarvestLevel("axe", 0);
		GameRegistry.registerBlock(juteBundled, "juteBundled");

		jutePlant = new BlockJutePlant()
				.setBlockName(ModInfo.MODID + ".jutePlant")
				.setBlockTextureName(ModInfo.MODID_LOWER + ":jute_plant");
		GameRegistry.registerBlock(jutePlant, ItemBlockJutePlant.class, "jutePlant");

		juteSeeds = new ItemSeedsGeneric(jutePlant, EnumPlantType.Plains)
				.setUnlocalizedName(ModInfo.MODID + ".juteSeeds")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_seeds");
		GameRegistry.registerItem(juteSeeds, "juteSeeds");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.bastFibreOreDict, new ItemStack(juteFibre));
	}

	@Override
	public void recipes()
	{
		GameRegistry.addShapedRecipe(new ItemStack(juteBundled), "///", "///", "///", '/', new ItemStack(juteStalk));

		DropsModifier.NEIBlockSpecifier juteBundledBlockSpecifier = new DropsModifier.NEIBlockSpecifier(juteBundled, OreDictionary.WILDCARD_VALUE, new ItemStack(juteBundled, 1, juteBundled.numRettingStages));
		DropsModifier.NEIDropSpecifier juteDropSpecifier = new DropsModifier.NEIDropSpecifier(new ItemStack(juteBundled.rettedItem), 1f, juteBundled.minRettedItemDrops, juteBundled.maxRettedItemDrops);
		Modifiers.drops.addDropsToBlock(juteBundledBlockSpecifier, juteDropSpecifier);

		BlockSpecifier doubleFernSpecifier = new BlockSpecifier(Blocks.double_plant, FERN_METADATA)
		{
			@Override
			public boolean metaMatches(int meta)
			{
				return this.meta == BlockDoublePlant.func_149890_d(meta);
			}
		};
		juteDrops = new DropSpecifier(new ItemStack(juteStalk), 1, 3);
		Modifiers.drops.addDropsToBlock(doubleFernSpecifier, juteDrops);

		// need to catch the top of the fern breaking really early, as it bypasses the harvest event
		// so register this as an event handler and handle it in onBlockBreak (see onFernTopBreak function below)
		MinecraftForge.EVENT_BUS.register(this);

		GameRegistry.addShapelessRecipe(new ItemStack(juteSeeds), new ItemStack(juteStalk));
	}

	@Override
	public void finish()
	{
		CompostRegistry.addGreen(Jute.juteStalk);
		RelationshipRegistry.addRelationship(new ItemStack(juteFibre), new ItemStack(juteBundled));
		RelationshipRegistry.addRelationship(new ItemStack(jutePlant), new ItemStack(juteSeeds));
	}

	public static final Method doublePlantDropBlockAsItem = ReflectionHelper.findMethod(Block.class, Blocks.double_plant, new String[]{"dropBlockAsItem", "func_149642_a", "a"}, World.class, int.class, int.class, int.class, ItemStack.class);

	/**
	 * Catch the top of a fern being broken and do the drops manually
	 * In the double plant code, it forces the blocks to air and bypasses
	 * the harvest drops event, so we need to catch it early and do some
	 * less than ideal checks
	 */
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onFernTopBreak(BlockEvent.BreakEvent event)
	{
		if (event.isCanceled())
			return;

		if (event.block != Blocks.double_plant)
			return;

		if (!BlockDoublePlant.func_149887_c(event.blockMetadata))
			return;

		if (event.world.getBlock(event.x, event.y - 1, event.z) != event.block)
			return;

		if (BlockDoublePlant.func_149890_d(event.world.getBlockMetadata(event.x, event.y - 1, event.z)) != FERN_METADATA)
			return;

		if (event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemShears)
			return;

		for (ItemStack drop : juteDrops.getDrops(event.getPlayer(), EnchantmentHelper.getFortuneModifier(event.getPlayer()), EnchantmentHelper.getSilkTouchModifier(event.getPlayer())))
		{
			try
			{
				doublePlantDropBlockAsItem.invoke(event.block, event.world, event.x, event.y - 1, event.z, drop);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
