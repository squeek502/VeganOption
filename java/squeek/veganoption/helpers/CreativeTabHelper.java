package squeek.veganoption.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import squeek.veganoption.content.modules.CreativeTabProxy;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class CreativeTabHelper
{
	private static final DeferredRegister<CreativeModeTab> REGISTER_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID_LOWER);
	public static final DeferredRegister<Item> FAKE_ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID_LOWER);

	public static void createTab(IEventBus bus)
	{
		FAKE_ITEMS.register(bus);
		REGISTER_TAB.register(MODID_LOWER, () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup." + MODID_LOWER))
			.icon(() -> new ItemStack(CreativeTabProxy.proxyItem.get()))
			.displayItems((enabledFeatures, entries) -> entries.acceptAll(REGISTER_ITEMS.getEntries().stream().map(i -> new ItemStack(i.get())).toList()))
			.build());
		REGISTER_TAB.register(bus);
	}
}
