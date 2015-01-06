package squeek.veganoption.content.modules;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.registry.CompostRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

// currently depends on potatoStarch
public class MobHeads implements IContentModule
{
	public static Item papierMache;
	public static Item mobHeadBlank;

	// {"skeleton", "wither", "zombie", "steve", "creeper"}
	public static final ItemStack mobHeadSkeleton = new ItemStack(Items.skull, 1, 0);
	public static final ItemStack mobHeadWitherSkeleton = new ItemStack(Items.skull, 1, 1);
	public static final ItemStack mobHeadZombie = new ItemStack(Items.skull, 1, 2);
	public static final ItemStack mobHeadSteve = new ItemStack(Items.skull, 1, 3);
	public static final ItemStack mobHeadCreeper = new ItemStack(Items.skull, 1, 4);

	@Override
	public void create()
	{
		papierMache = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".papierMache")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":papier_mache");
		GameRegistry.registerItem(papierMache, "papierMache");

		mobHeadBlank = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".mobHeadBlank")
				.setCreativeTab(CreativeTabs.tabMaterials)
				.setTextureName(ModInfo.MODID_LOWER + ":blank_mob_head");
		GameRegistry.registerItem(mobHeadBlank, "mobHeadBlank");
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(papierMache, 8), new ItemStack(Items.water_bucket), ContentHelper.starchOreDict, new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper)));

		GameRegistry.addShapedRecipe(new ItemStack(mobHeadBlank), "///", "/m/", "///", '/', new ItemStack(papierMache), 'm', new ItemStack(Blocks.melon_block));

		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadSkeleton.copy(), "ddd", "dhd", "ddd", 'd', "dyeLightGray", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadWitherSkeleton.copy(), "ddd", "dhd", "ddd", 'd', "dyeBlack", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadSteve.copy(), "ddd", "dhd", "ddd", 'd', "dyeBrown", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadZombie.copy(), "ddd", "dhd", "ddd", 'd', "dyeGreen", 'h', mobHeadBlank));
		GameRegistry.addRecipe(new ShapedOreRecipe(mobHeadCreeper.copy(), "ddd", "dhd", "ddd", 'd', "dyeLime", 'h', mobHeadBlank));
	}

	@Override
	public void finish()
	{
		CompostRegistry.addBrown(papierMache);
	}
}
