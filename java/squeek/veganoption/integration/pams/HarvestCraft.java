package squeek.veganoption.integration.pams;

import java.lang.reflect.Field;
import java.util.HashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.CompostRegistry;
import squeek.veganoption.content.CompostRegistry.FoodSpecifier;
import squeek.veganoption.integration.IIntegrator;

public class HarvestCraft implements IIntegrator
{
	public static final String rootPackage = "com.pam.harvestcraft.";

	public static HashMap<String, Item> itemCache = new HashMap<String, Item>();

	public static Class<?> ItemRegistry;
	static
	{
		try
		{
			ItemRegistry = Class.forName(rootPackage + "ItemRegistry");
		}
		catch (Exception e)
		{
			VeganOption.Log.error("Something went wrong when initializing HarvestCraft integration:");
			e.printStackTrace();
		}
	}

	public static Item getItem(String name)
	{
		Item item = itemCache.get(name);
		if (item == null)
		{
			try
			{
				Field itemField = ItemRegistry.getDeclaredField(name);
				item = (Item) itemField.get(null);
				itemCache.put(name, item);
			}
			catch (Exception e)
			{
			}
		}
		return item;
	}

	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{
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

	@Override
	public void postInit()
	{
	}
}
