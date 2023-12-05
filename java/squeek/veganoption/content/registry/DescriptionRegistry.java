package squeek.veganoption.content.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.ForgeRegistries;
import squeek.veganoption.VeganOption;
import squeek.veganoption.helpers.LangHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptionRegistry
{
	public static List<Item> itemsWithUsageDescriptions = new ArrayList<>();
	public static List<Item> itemsWithCraftingDescriptions = new ArrayList<>();
	public static Map<Item, String> itemsWithCustomUsageDescriptions = new HashMap<>();
	public static Map<Item, String> itemsWithCustomCraftingDescriptions = new HashMap<>();

	//todo: jei integration.
	public static void registerAllDescriptions()
	{
		long millisecondsStart = System.currentTimeMillis();
		int numRegistered = 0;

		for (Item item : ForgeRegistries.ITEMS)
		{
			if (tryRegisterItem(item))
				numRegistered++;
		}

		long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		String timeTakenString = "took " + (timeSpentInMilliseconds / 1000.0f) + " seconds";
		VeganOption.Log.info("Found and registered " + numRegistered + " items/blocks with description text (" + timeTakenString + ")");
	}

	public static boolean tryRegisterItem(Item item)
	{
		boolean didRegister = false;
		if (hasUsageText(item) && !itemsWithUsageDescriptions.contains(item))
		{
			itemsWithUsageDescriptions.add(item);
			didRegister = true;
		}
		if (hasCraftingText(item) && !itemsWithCraftingDescriptions.contains(item))
		{
			itemsWithCraftingDescriptions.add(item);
			didRegister = true;
		}
		return didRegister;
	}

	public static boolean hasUsageText(Item item)
	{
		return LangHelper.existsRaw(item.getDescriptionId() + ".vowiki.usage") || !RelationshipRegistry.getChildren(item).isEmpty();
	}

	public static boolean hasCraftingText(Item item)
	{
		return LangHelper.existsRaw(item.getDescriptionId() + ".vowiki.crafting") || !RelationshipRegistry.getParents(item).isEmpty();
	}

	public static void registerCustomUsageText(Item item, String unlocalizedUsageText)
	{
		itemsWithCustomUsageDescriptions.put(item, unlocalizedUsageText);
		itemsWithUsageDescriptions.add(item);
	}

	public static void registerCustomCraftingText(Item item, String unlocalizedCraftingText)
	{
		itemsWithCustomCraftingDescriptions.put(item, unlocalizedCraftingText);
		itemsWithCraftingDescriptions.add(item);
	}
}
