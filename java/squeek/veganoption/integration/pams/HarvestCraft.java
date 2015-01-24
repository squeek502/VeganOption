package squeek.veganoption.integration.pams;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.integration.IntegratorBase;

public class HarvestCraft extends IntegratorBase
{
	public static final String rootPackage = "com.pam.harvestcraft.";

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.oilPresserOreDict, new ItemStack(getItem("juicerItem")));
		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(getItem("firmtofuItem")));
	}

	@Override
	public void recipes()
	{
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
