package squeek.veganoption.content.modules;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.helpers.CreativeTabHelper;

import java.util.function.Supplier;

public class CreativeTabProxy implements IContentModule
{

	public static Supplier<Item> proxyItem;

	@Override
	public void create()
	{
		proxyItem = CreativeTabHelper.FAKE_ITEMS.register("creative_tab", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(proxyItem.get());
	}
}
