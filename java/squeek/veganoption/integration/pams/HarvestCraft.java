package squeek.veganoption.integration.pams;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.helpers.TooltipHelper;
import squeek.veganoption.integration.IntegratorBase;
import cpw.mods.fml.common.registry.GameRegistry;

public class HarvestCraft extends IntegratorBase
{
	public static final String rootPackage = "com.pam.harvestcraft.";
	public static Item bbqSauce;
	public static Item bbqTofu;

	@Override
	public void create()
	{
		bbqSauce = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".bbqSauce")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":bbq_sauce");
		GameRegistry.registerItem(bbqSauce, "bbqSauce");

		bbqTofu = new ItemFood(8, 0.8F, false)
				.setUnlocalizedName(ModInfo.MODID + ".bbqTofu")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":bbq_tofu");
		GameRegistry.registerItem(bbqTofu, "bbqTofu");
		TooltipHelper.registerItem(bbqTofu);
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.oilPresserOreDict, new ItemStack(getItem("juicerItem")));
		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(getItem("firmtofuItem")));
		OreDictionary.registerOre(ContentHelper.bbqSauceOreDict, new ItemStack(bbqSauce));
	}

	@Override
	public void recipes()
	{
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bbqSauce), "toolSaucepan", "foodKetchup", "foodVinegar", Items.sugar, "foodMustard", "listAllwater", "foodSalt", "foodBlackpepper"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bbqTofu), "toolBakeware", "foodFirmtofu", ContentHelper.bbqSauceOreDict));

		// exclude non-baked goods from egg replacer conversion
		final String[] foodNamesToExclude = new String[]
		{
		"boiledeggItem",
		"scrambledeggItem",
		"friedriceItem",
		"stuffedeggplantItem",
		"asparagusquicheItem",
		"custardItem",
		"omeletItem",
		"marshmellowsItem",
		"mayoItem",
		"coconutshrimpItem",
		"eggnogItem",
		"zucchinifriesItem",
		"friedeggItem"
		};
		for (String foodNameToExclude : foodNamesToExclude)
		{
			Item item = getItem(foodNameToExclude);

			if (item != null)
				Modifiers.recipes.excludeOutput(new ItemStack(item));
		}
	}

	@Override
	public void init()
	{
		super.init();
		CompostRegistry.blacklist(new FoodSpecifier()
		{
			@Override
			public boolean matches(ItemStack itemStack)
			{
				// this is a bad way to do this, but pam's foods do not make much distinction between foods with
				// and without meat, so effectively blacklist all but raw plant foods as a shortcut to weeding out meat.
				// (raw plants use cropX oredict while prepared foods use foodX oredict)
				int[] oreIDs = OreDictionary.getOreIDs(itemStack);
				for (int oreID : oreIDs)
				{
					String oreName = OreDictionary.getOreName(oreID);
					if (oreName.startsWith("food"))
						return true;
				}
				return false;
			}
		});
	}
}
