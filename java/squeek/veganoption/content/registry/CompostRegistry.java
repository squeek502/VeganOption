package squeek.veganoption.content.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Compostable items are registered using the tags veganoption:compostables/brown and veganoption:compostables/brown. Items can be
 * blacklisted by tagging them as veganoption:compostables/blacklist.
 * <br/>
 * Food items are automatically green, despite not being tagged, unless they are in a bowl or other craft-remainder item, or are flagged as
 * meat in the item's FoodProperties. Due to the large number of modded food items, this is handled at runtime.
 */
@Mod.EventBusSubscriber(modid = ModInfo.MODID_LOWER)
public class CompostRegistry
{
	private static final Set<Item> compostableFoods = new HashSet<>();

	@SubscribeEvent
	public static void registerAllFoods(TagsUpdatedEvent event)
	{
		long millisecondsStart = System.currentTimeMillis();
		int numRegistered = 0;

		for (Item item : BuiltInRegistries.ITEM)
		{
			ItemStack itemStack = new ItemStack(item);
			if (!isBlacklisted(item) && item.isEdible() && !(item instanceof BowlFoodItem) && !itemStack.hasCraftingRemainingItem() && !itemStack.getFoodProperties(null).isMeat())
			{
				compostableFoods.add(item);
				numRegistered++;
			}
		}

		long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		String timeTakenString = "took " + (timeSpentInMilliseconds / 1000.0f) + " seconds";
		VeganOption.Log.info("Found and registered " + numRegistered + " compostable foods (" + timeTakenString + ")");
		VeganOption.Log.info("Important: Foods registered at runtime may not necessarily have the item tag veganoption:compostables/green");
	}

	public static boolean isCompostable(ItemStack itemStack)
	{
		return isGreen(itemStack) || isBrown(itemStack);
	}

	public static boolean isBrown(ItemStack itemStack)
	{
		return isBrown(itemStack.getItem());
	}

	public static boolean isBrown(Item item)
	{
		return ContentHelper.isItemTaggedAs(item, ContentHelper.ItemTags.COMPOSTABLES_BROWN) && !isBlacklisted(item);
	}

	public static boolean isGreen(ItemStack itemStack)
	{
		return isGreen(itemStack.getItem());
	}

	public static boolean isGreen(Item item)
	{
		if (isBlacklisted(item))
			return false;

		if (ContentHelper.isItemTaggedAs(item, ContentHelper.ItemTags.COMPOSTABLES_GREEN) || compostableFoods.contains(item))
			return true;

		return compostableFoods.contains(item);
	}

	public static boolean isBlacklisted(Item item)
	{
		return ContentHelper.isItemTaggedAs(item, ContentHelper.ItemTags.COMPOSTABLES_BLACKLIST);
	}

	public static List<Item> getGreens()
	{
		List<Item> items = new ArrayList<>(BuiltInRegistries.ITEM.getTag(ContentHelper.ItemTags.COMPOSTABLES_GREEN).orElseThrow().stream().map(Holder::value).toList());
		items.addAll(compostableFoods);
		items.removeAll(BuiltInRegistries.ITEM.getTag(ContentHelper.ItemTags.COMPOSTABLES_BLACKLIST).orElseThrow().stream().map(Holder::value).toList());
		return items.stream().distinct().toList();
	}

	public static List<Item> getBrowns()
	{
		List<Item> items = new ArrayList<>(BuiltInRegistries.ITEM.getTag(ContentHelper.ItemTags.COMPOSTABLES_BROWN).orElseThrow().stream().map(Holder::value).toList());
		items.removeAll(BuiltInRegistries.ITEM.getTag(ContentHelper.ItemTags.COMPOSTABLES_BLACKLIST).orElseThrow().stream().map(Holder::value).toList());
		return items;
	}
}
