package squeek.veganoption.integration.thaumcraft;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.integration.IntegratorBase;

public class Thaumcraft extends IntegratorBase
{
	@Override
	public void init()
	{
		super.init();

		CompostRegistry.blacklist(new FoodSpecifier()
		{
			private final Set<String> itemNameBlacklist = new HashSet<String>(
					Arrays.asList(
									fullItemName("ItemNuggetFish"),
									fullItemName("ItemNuggetChicken"),
									fullItemName("ItemNuggetBeef"),
									fullItemName("ItemNuggetPork")
							)
					);

			@Override
			public boolean matches(ItemStack itemStack)
			{
				ResourceLocation itemRL = Item.REGISTRY.getNameForObject(itemStack.getItem());

				if (itemRL == null)
					return false;

				String itemName = itemRL.toString();
				return itemNameBlacklist.contains(itemName);
			}
		});
	}
}
