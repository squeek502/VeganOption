package squeek.veganoption.integration;

import java.lang.reflect.Field;
import java.util.HashMap;
import net.minecraft.item.Item;
import squeek.veganoption.VeganOption;
import cpw.mods.fml.common.Loader;

public class HarvestCraft
{
	public static final boolean exists = Loader.isModLoaded("harvestcraft");
	public static final String rootPackage = "com.pam.harvestcraft.";

	public static HashMap<String, Item> itemCache = new HashMap<String, Item>();

	public static Class<?> ItemRegistry;
	static
	{
		if (exists)
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
}
