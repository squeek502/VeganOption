package squeek.veganoption.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import squeek.veganoption.ModInfo;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = ModInfo.MODID_LOWER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiscHelper
{
	public static final int MAX_REDSTONE_SIGNAL_STRENGTH = 15;
	public static final int TICKS_PER_SEC = 20;
	public static final int TICKS_PER_DAY = 24000; // 20 minutes realtime
	public static final int NINE_SLOT_WIDTH = 162;
	public static final int STANDARD_GUI_WIDTH = 176;
	public static final int STANDARD_SLOT_WIDTH = 18;
	private static RecipeManager cachedRecipeManager;

	public static Item getMatchingItemFromList(Collection<Item> haystack, Item needle)
	{
		if (haystack != null)
		{
			return haystack.contains(needle) ? needle : null;
		}
		return null;
	}

	public static boolean isItemTaggedAs(Item item, TagKey<Item> tag)
	{
		return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
	}

	public static RecipeManager getRecipeManager()
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null)
			return server.getRecipeManager();

		try
		{
			Level clientLevel = Minecraft.getInstance().level;
			if (clientLevel != null)
				return clientLevel.getRecipeManager();
		}
		catch (Throwable t)
		{
			// ignore - normal.
			// Being here just means we're on the server or on initial startup.
		}

		if (cachedRecipeManager != null)
			return cachedRecipeManager;

		throw new IllegalStateException("[Vegan Option] RecipeManager not found on server, client, or startup cache!");
	}

	@SubscribeEvent
	public static void setRecipeManagerCache(AddReloadListenerEvent event)
	{
		cachedRecipeManager = event.getServerResources().getRecipeManager();
	}
}
