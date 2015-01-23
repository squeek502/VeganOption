package squeek.veganoption.integration.forestry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.integration.IntegratorBase;

public class ExtraTrees extends IntegratorBase
{
	@Override
	public void oredict()
	{
		Item foodItem = getItem("food");
		if (foodItem != null)
		{
			// coconut
			ItemStack coconut = new ItemStack(foodItem, 1, 50);
			OreDictionary.registerOre(ContentHelper.coconutOreDict, coconut);

			// nuts
			ItemStack pecan = new ItemStack(foodItem, 1, 27);
			ItemStack brazilNut = new ItemStack(foodItem, 1, 31);
			ItemStack beechNut = new ItemStack(foodItem, 1, 26);
			ItemStack butterNut = new ItemStack(foodItem, 1, 25);
			ItemStack hazelNut = new ItemStack(foodItem, 1, 24);
			ItemStack almond = new ItemStack(foodItem, 1, 9);
			OreDictionary.registerOre(ContentHelper.nutOreDict, pecan);
			OreDictionary.registerOre(ContentHelper.nutOreDict, brazilNut);
			OreDictionary.registerOre(ContentHelper.nutOreDict, beechNut);
			OreDictionary.registerOre(ContentHelper.nutOreDict, butterNut);
			OreDictionary.registerOre(ContentHelper.nutOreDict, hazelNut);
			OreDictionary.registerOre(ContentHelper.nutOreDict, almond);
		}
	}

	@Override
	public void init()
	{
		super.init();

		CompostRegistry.blacklist(new FoodSpecifier()
		{
			private final Set<String> itemNameBlacklist = new HashSet<String>(
					Arrays.asList(
							fullItemName("drink")
							)
					);

			@Override
			public boolean matches(ItemStack itemStack)
			{
				String itemName = Item.itemRegistry.getNameForObject(itemStack.getItem());
				return itemNameBlacklist.contains(itemName);
			}
		});
	}
}
