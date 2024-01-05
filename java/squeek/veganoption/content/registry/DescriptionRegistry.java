package squeek.veganoption.content.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import squeek.veganoption.VeganOption;
import squeek.veganoption.helpers.LangHelper;

import java.util.ArrayList;
import java.util.List;

public class DescriptionRegistry
{
	public static List<ItemStack> itemsWithUsageDescriptions = new ArrayList<>();
	public static List<ItemStack> itemsWithCraftingDescriptions = new ArrayList<>();
	private static final List<Item> damageableItemsWithUniqueDescriptions = new ArrayList<>();
	/** Used to prevent duplicate entries from being registered. Necessary for damageable items with unique descriptions. */
	private static final List<String> registeredLangKeys = new ArrayList<>();
	private static final String USAGE_SUFFIX = ".vowiki.usage";
	private static final String CRAFTING_SUFFIX = ".vowiki.crafting";

	public static void registerAllDescriptions()
	{
		long millisecondsStart = System.currentTimeMillis();
		int numRegistered = 0;

		for (Item item : BuiltInRegistries.ITEM)
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

		if (damageableItemsWithUniqueDescriptions.contains(item))
		{
			ItemStack stack = new ItemStack(item);
			for (int damage = 1; damage <= stack.getMaxDamage(); damage++)
			{
				stack.setDamageValue(damage);
				boolean registeredStack = tryRegisterItemStack(stack);
				if (registeredStack)
					didRegister = true;
			}
		}
		didRegister = tryRegisterItemStack(new ItemStack(item)) || didRegister;
		return didRegister;
	}

	public static boolean tryRegisterItemStack(ItemStack stack)
	{
		boolean didRegister = false;
		if (canRegisterUsageText(stack))
		{
			itemsWithUsageDescriptions.add(stack);
			registeredLangKeys.add(stack.getDescriptionId() + ".vowiki.usage");
			didRegister = true;
		}
		if (canRegisterCraftingText(stack))
		{
			itemsWithCraftingDescriptions.add(stack);
			registeredLangKeys.add(stack.getDescriptionId() + ".vowiki.crafting");
			didRegister = true;
		}
		return didRegister;
	}

	private static boolean hasUsageText(ItemStack stack)
	{
		return LangHelper.existsRaw(getUsageKey(stack)) || !RelationshipRegistry.getChildren(stack.getItem()).isEmpty();
	}

	private static boolean hasCraftingText(ItemStack stack)
	{
		return LangHelper.existsRaw(getCraftingKey(stack)) || !RelationshipRegistry.getParents(stack.getItem()).isEmpty();
	}

	private static boolean canRegisterUsageText(ItemStack stack)
	{
		return hasUsageText(stack) &&
			!registeredLangKeys.contains(getUsageKey(stack)) &&
			itemsWithUsageDescriptions.stream().noneMatch(i -> i.getItem() == stack.getItem());
	}

	private static boolean canRegisterCraftingText(ItemStack stack)
	{
		return hasCraftingText(stack) &&
			!registeredLangKeys.contains(getCraftingKey(stack)) &&
			itemsWithCraftingDescriptions.stream().noneMatch(i -> i.getItem() == stack.getItem());
	}

	/**
	 * Sets the provided item as having unique descriptions for damaged versions. Damaged version must have a unique description ID.
	 */
	public static void setUniqueDamageableItem(Item item)
	{
		damageableItemsWithUniqueDescriptions.add(item);
	}

	public static String getUsageKey(ItemStack stack)
	{
		return stack.getDescriptionId() + USAGE_SUFFIX;
	}

	public static String getCraftingKey(ItemStack stack)
	{
		return stack.getDescriptionId() + CRAFTING_SUFFIX;
	}
}
