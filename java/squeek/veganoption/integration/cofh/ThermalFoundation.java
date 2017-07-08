package squeek.veganoption.integration.cofh;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.integration.IntegratorBase;

public class ThermalFoundation extends IntegratorBase
{
	public static final String slagOreDict = "itemSlag";
	public static final String mithrilDustOreDict = "dustMithril";
	public static final int blizzRodMeta = 2048;
	public static final int blitzRodMeta = 2050;
	public static final int basalzRodMeta = 2052;
	public static final int rosinMeta = 832;

	@Override
	public void recipes()
	{
		super.recipes();

		Item material = getItem("material");

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(material, 1, blizzRodMeta), ContentHelper.plasticRodOreDict, new ItemStack(Items.SNOWBALL), mithrilDustOreDict));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(material, 1, blitzRodMeta), ContentHelper.plasticRodOreDict, ContentHelper.featherOreDict, ContentHelper.sulfurOreDict));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(material, 1, basalzRodMeta), ContentHelper.plasticRodOreDict, slagOreDict, ContentHelper.saltpeterOreDict));
	}

	@Override
	public void oredict()
	{
		super.oredict();

		Item material = getItem("material");

		OreDictionary.registerOre(ContentHelper.rosinOreDict, new ItemStack(material, 1, rosinMeta));
		OreDictionary.registerOre(ContentHelper.rosinMaterialOreDict, new ItemStack(material, 1, rosinMeta));
	}
}
