package squeek.veganoption.integration.pams;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.integration.IIntegrator;
import squeek.veganoption.integration.IntegrationHandler;
import cpw.mods.fml.common.registry.GameRegistry;

// TODO: More oils
public class HarvestCraft implements IIntegrator
{
	public static final String rootPackage = "com.pam.harvestcraft.";
	public static final String modId = IntegrationHandler.MODID_HARVESTCRAFT;

	public static Item getItem(String name)
	{
		return GameRegistry.findItem(modId, name);
	}

	@Override
	public void overrideContent()
	{
	}

	@Override
	public void preInit()
	{
		OreDictionary.registerOre(ContentHelper.oilPresserOreDict, new ItemStack(HarvestCraft.getItem("juicerItem")));
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
