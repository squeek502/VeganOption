package squeek.veganoption.content.modules;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;

// currently depends on Kapok
public class Feather implements IContentModule
{
	public static Item fauxFeather;

	@Override
	public void create()
	{
		fauxFeather = new Item()
			.setUnlocalizedName(ModInfo.MODID + ".fauxFeather")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "fauxFeather");
		GameRegistry.register(fauxFeather);
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.featherOreDict, new ItemStack(Items.FEATHER));
		OreDictionary.registerOre(ContentHelper.featherOreDict, new ItemStack(fauxFeather));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Items.FEATHER), ContentHelper.featherOreDict);

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(fauxFeather), ContentHelper.kapokOreDict, ContentHelper.plasticRodOreDict));
	}

	@Override
	public void finish()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePost()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(fauxFeather);
	}

}
