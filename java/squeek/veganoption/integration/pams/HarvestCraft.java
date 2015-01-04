package squeek.veganoption.integration.pams;

import java.lang.reflect.Field;
import java.util.HashMap;
import net.minecraft.item.Item;
import squeek.veganoption.VeganOption;
import squeek.veganoption.integration.IIntegrator;

public class HarvestCraft implements IIntegrator
{
	// TODO: False Morel Soup

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
	}

	@Override
	public void postInit()
	{
	}
}
