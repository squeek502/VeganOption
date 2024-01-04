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
		if (hasUsageText(stack) && itemsWithUsageDescriptions.stream().noneMatch(i -> i.getItem() == stack.getItem()))
		{
			itemsWithUsageDescriptions.add(stack);
			registeredLangKeys.add(stack.getDescriptionId() + ".vowiki.usage");
			didRegister = true;
		}
		if (hasCraftingText(stack) && itemsWithCraftingDescriptions.stream().noneMatch(i -> i.getItem() == stack.getItem()))
		{
			itemsWithCraftingDescriptions.add(stack);
			registeredLangKeys.add(stack.getDescriptionId() + ".vowiki.crafting");
			didRegister = true;
		}
		return didRegister;
	}

	private static boolean hasUsageText(ItemStack stack)
	{
		String key = stack.getDescriptionId() + ".vowiki.usage";
		return !registeredLangKeys.contains(key) && (LangHelper.existsRaw(key) || !RelationshipRegistry.getChildren(stack.getItem()).isEmpty());
	}

	private static boolean hasCraftingText(ItemStack stack)
	{
		String key = stack.getDescriptionId() + ".vowiki.crafting";
		return !registeredLangKeys.contains(key) && (LangHelper.existsRaw(key) || !RelationshipRegistry.getParents(stack.getItem()).isEmpty());
	}

	/**
	 * Sets the provided item as having unique descriptions for damaged versions. Damaged version must have a unique description ID.
	 */
	public static void setUniqueDamageableItem(Item item)
	{
		damageableItemsWithUniqueDescriptions.add(item);
	}

	public static boolean itemHasUniqueDamageDescriptions(ItemStack stack)
	{
		return damageableItemsWithUniqueDescriptions.contains(stack.getItem());
	}
}
