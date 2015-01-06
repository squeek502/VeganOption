package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class Jute implements IContentModule
{
	public static BlockRettable juteBundled;
	public static Item juteStalk;
	public static Item juteFibre;

	@Override
	public void create()
	{
		juteFibre = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".juteFibre")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":jute_fibre");
		GameRegistry.registerItem(juteFibre, "juteFibre");

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

		BlockSpecifier doubleFernSpecifier = new BlockSpecifier(Blocks.double_plant, 3)
		{
			@Override
			public boolean metaMatches(int meta)
			{
				return this.meta == BlockDoublePlant.func_149890_d(meta);
			}
		};
		Modifiers.drops.addDropsToBlock(doubleFernSpecifier, new DropSpecifier(new ItemStack(juteStalk), 1, 3));
	}

	@Override
	public void finish()
	{
		CompostRegistry.addGreen(Jute.juteStalk);
		RelationshipRegistry.addRelationship(new ItemStack(juteFibre), new ItemStack(juteBundled));
	}
}
