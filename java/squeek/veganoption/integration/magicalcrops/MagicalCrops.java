package squeek.veganoption.integration.magicalcrops;

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

public class MagicalCrops extends IntegratorBase
{
	@Override
	public void oredict()
	{
		if (getItem("magicalcrops_SeedsGrape") != null)
			OreDictionary.registerOre(ContentHelper.grapeSeedOreDict, new ItemStack(getItem("magicalcrops_SeedsGrape")));
	}

	@Override
	public void init()
	{
		super.init();

		CompostRegistry.blacklist(new FoodSpecifier()
		{
			private final Set<String> itemNameBlacklist = new HashSet<String>(
					Arrays.asList(
									fullItemName("magicalcrops_PotionPetals"),
									fullItemName("magicalcrops_FoodJuice"),
									fullItemName("magicalcrops_FoodStew")
							)
					);

			@Override
			public boolean matches(ItemStack itemStack)
			{
				// meat and diamonds are bad for composting
				String itemName = Item.itemRegistry.getNameForObject(itemStack.getItem());
				return itemNameBlacklist.contains(itemName);
			}
		});
	}
}
