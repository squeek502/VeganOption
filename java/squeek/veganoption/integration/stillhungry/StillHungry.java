package squeek.veganoption.integration.stillhungry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.integration.IntegratorBase;

public class StillHungry extends IntegratorBase
{
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
