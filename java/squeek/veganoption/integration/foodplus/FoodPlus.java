package squeek.veganoption.integration.foodplus;

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

public class FoodPlus extends IntegratorBase
{
	@Override
	public void oredict()
	{
		if (getItem("coconut") != null)
			OreDictionary.registerOre(ContentHelper.coconutOreDict, new ItemStack(getItem("coconut")));
		if (getItem("rice") != null)
			OreDictionary.registerOre(ContentHelper.riceOreDict, new ItemStack(getItem("rice")));
		if (getItem("peanut") != null)
			OreDictionary.registerOre(ContentHelper.nutOreDict, new ItemStack(getItem("peanut")));
		if (getItem("walnut") != null)
			OreDictionary.registerOre(ContentHelper.nutOreDict, new ItemStack(getItem("walnut")));
	}

	@Override
	public void init()
	{
		super.init();

		// basically blacklist everything except the various raw crops
		// TODO: deeper integration
		CompostRegistry.blacklist(new FoodSpecifier()
		{
			private final Set<String> itemNameWhitelist = new HashSet<String>(
					Arrays.asList(
									fullItemName("strawberry"),
									fullItemName("tomato"),
									fullItemName("rice"),
									fullItemName("seaweed"),
									fullItemName("tea_leaves"),
									fullItemName("orange"),
									fullItemName("pear"),
									fullItemName("banana"),
									fullItemName("cherry"),
									fullItemName("coconut"),
									fullItemName("kiwi"),
									fullItemName("peanut"),
									fullItemName("walnut")
							)
					);

			@Override
			public boolean matches(ItemStack itemStack)
			{
				String itemName = Item.itemRegistry.getNameForObject(itemStack.getItem());

				if (!itemName.startsWith(modID))
					return false;

				return !itemNameWhitelist.contains(itemName);
			}
		});
	}
}
