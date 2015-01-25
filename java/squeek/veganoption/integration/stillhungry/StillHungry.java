package squeek.veganoption.integration.stillhungry;

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

public class StillHungry extends IntegratorBase
{
	@Override
	public void oredict()
	{
		if (getItem("StillHungry_rice") != null)
			OreDictionary.registerOre(ContentHelper.riceOreDict, new ItemStack(getItem("StillHungry_rice")));
		if (getItem("StillHungry_oil") != null)
			OreDictionary.registerOre(ContentHelper.vegetableOilOreDict, new ItemStack(getItem("StillHungry_oil")));
		if (getItem("StillHungry_grapeSeed") != null)
			OreDictionary.registerOre(ContentHelper.grapeSeedOreDict, new ItemStack(getItem("StillHungry_grapeSeed")));
	}

	@Override
	public void init()
	{
		super.init();

		// basically blacklist everything except the various raw crops
		CompostRegistry.blacklist(new FoodSpecifier()
		{
			private final Set<String> itemNameWhitelist = new HashSet<String>(
					Arrays.asList(
									fullItemName("StillHungry_strawberry"),
									fullItemName("StillHungry_grapes")
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
