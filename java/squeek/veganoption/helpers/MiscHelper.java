package squeek.veganoption.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MiscHelper
{
	public static final int MAX_REDSTONE_SIGNAL_STRENGTH = 15;
	public static final int TICKS_PER_SEC = 20;
	public static final int TICKS_PER_DAY = 24000; // 20 minutes realtime
	public static final int NINE_SLOT_WIDTH = 162;
	public static final int STANDARD_GUI_WIDTH = 176;
	public static final int STANDARD_SLOT_WIDTH = 18;
	public static final int STANDARD_SLOT_HEIGHT = 18;
	private static RecipeManager cachedRecipeManager;

	public static Item getMatchingItemFromList(Collection<Item> haystack, Item needle)
	{
		if (haystack != null)
		{
			return haystack.contains(needle) ? needle : null;
		}
		return null;
	}

	public static ItemStack getMatchingItemFromStackList(Collection<ItemStack> haystack, Item needle)
	{
		if (haystack != null)
		{
			for (ItemStack item : haystack)
			{
				if (item.getItem() == needle)
					return item;
			}
		}
		return null;
	}

	public static RecipeManager getRecipeManager()
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null)
			return server.getRecipeManager();

		try
		{
			Level clientLevel = ClientHelper.getLevel();
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

	public static void setCachedRecipeManager(RecipeManager manager)
	{
		cachedRecipeManager = manager;
	}

	/**
	 * Creates a shuffled copy of the provided list
	 */
	public static <T> List<T> newShuffledList(List<T> toShuffle)
	{
		List<T> shuffled = new ArrayList<T>(toShuffle);
		Collections.shuffle(shuffled);
		return shuffled;
	}

	public static ItemStack getItemStackByObjectName(String name)
	{
		return new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(name)));
	}

	public static Fluid getFluidByObjectName(String name)
	{
		return BuiltInRegistries.FLUID.get(new ResourceLocation(name));
	}
}
