package squeek.veganoption.content.modules;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.helpers.CreativeTabHelper;

public class CreativeTabProxy implements IContentModule
{

	public static RegistryObject<Item> proxyItem;

	@Override
	public void create()
	{
		proxyItem = CreativeTabHelper.FAKE_ITEMS.register("creative_tab", () -> new Item(new Item.Properties()));
	}
}
