package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

public class Jute implements IContentModule
{
	public static BlockRettable juteBundled;
	public static Block jutePlant;
	public static Item jutePlantItemBlock;
	public static Item juteStalk;
	public static Item juteFibre;
	public static Item juteSeeds;
	public static DropSpecifier juteDrops;

	@Override
	public void create()
	{
		juteFibre = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteFibre")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "juteFibre");
		GameRegistry.register(juteFibre);

		juteStalk = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteStalk")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "juteStalk");
		GameRegistry.register(juteStalk);

		juteBundled = (BlockRettable) new BlockRettable(juteFibre, 8, 15)
				.setHardness(0.5F)
				.setUnlocalizedName(ModInfo.MODID + ".juteBundled")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "juteBundled");
		juteBundled.setHarvestLevel("axe", 0);
		GameRegistry.register(juteBundled);
		GameRegistry.register(new ItemBlock(juteBundled).setRegistryName(juteBundled.getRegistryName()));

		jutePlant = new BlockJutePlant()
				.setUnlocalizedName(ModInfo.MODID + ".jutePlant")
				.setRegistryName(ModInfo.MODID_LOWER, "jutePlant");
		GameRegistry.register(jutePlant);
		jutePlantItemBlock = new ItemBlockJutePlant(jutePlant).setRegistryName(jutePlant.getRegistryName());
		GameRegistry.register(jutePlantItemBlock);

		juteSeeds = new ItemSeedsGeneric(jutePlant, EnumPlantType.Plains)
				.setUnlocalizedName(ModInfo.MODID + ".juteSeeds")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "juteSeeds");
		GameRegistry.register(juteSeeds);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSide()
	{
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockRettable.BlockRettableColorHandler(), juteBundled);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemBlockJutePlant.ColorHandler(), jutePlantItemBlock);
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockJutePlant.BlockJutePlantColorHandler(), jutePlant);
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

		DropsModifier.NEIBlockSpecifier juteBundledBlockSpecifier = new DropsModifier.NEIBlockSpecifier(juteBundled, OreDictionary.WILDCARD_VALUE, new ItemStack(juteBundled, 1, BlockRettable.numRettingStages));
		DropsModifier.NEIDropSpecifier juteDropSpecifier = new DropsModifier.NEIDropSpecifier(new ItemStack(juteBundled.rettedItem), 1f, juteBundled.minRettedItemDrops, juteBundled.maxRettedItemDrops);
		Modifiers.drops.addDropsToBlock(juteBundledBlockSpecifier, juteDropSpecifier);

		BlockSpecifier doubleFernSpecifier = new BlockSpecifier(Blocks.DOUBLE_PLANT)
		{
			@Override
			public boolean stateMatches(IBlockState state)
			{
				return state.getValue(BlockDoublePlant.VARIANT) == BlockDoublePlant.EnumPlantType.FERN;
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

		IBlockState state = event.getState();
		Block block = state.getBlock();
		if (block != Blocks.DOUBLE_PLANT)
			return;

		if (state.getValue(BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER)
			return;

		World world = event.getWorld();
		BlockPos posBelow = event.getPos().down();
		IBlockState stateBelow = world.getBlockState(event.getPos().down());
		Block blockBelow = stateBelow.getBlock();
		if (blockBelow != block)
			return;

		if (stateBelow.getValue(BlockDoublePlant.VARIANT) != BlockDoublePlant.EnumPlantType.FERN)
			return;

		if (event.getPlayer().getHeldItemMainhand() != null && event.getPlayer().getHeldItemMainhand().getItem() instanceof ItemShears)
			return;

		for (ItemStack drop : juteDrops.getDrops(event.getPlayer(), squeek.veganoption.helpers.EnchantmentHelper.getFortuneModifier(event.getPlayer()), squeek.veganoption.helpers.EnchantmentHelper.getSilkTouchModifier(event.getPlayer())))
		{
			Block.spawnAsEntity(event.getWorld(), event.getPos().down(), drop);
		}
	}
}
