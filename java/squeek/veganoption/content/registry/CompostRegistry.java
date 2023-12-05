package squeek.veganoption.content.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.ForgeRegistries;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;

import java.util.ArrayList;
import java.util.List;

public class CompostRegistry
{
	public static List<Item> browns = new ArrayList<>();
	public static List<Item> greens = new ArrayList<>();
	public static List<FoodSpecifier> uncompostableFoods = new ArrayList<>();

	public static void registerAllFoods()
	{
		long millisecondsStart = System.currentTimeMillis();
		int numRegistered = 0;

		for (Item item : ForgeRegistries.ITEMS)
		{
			if (item == null)
				continue;

			if (isCompostableFood(new ItemStack(item)))
			{
				addGreen(item);
				numRegistered++;
			}
		}

		long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		String timeTakenString = "took " + (timeSpentInMilliseconds / 1000.0f) + " seconds";
		VeganOption.Log.info("Found and registered " + numRegistered + " compostable foods (" + timeTakenString + ")");
	}

	public abstract static class FoodSpecifier
	{
		public abstract boolean matches(ItemStack itemStack);
	}

	public static boolean isCompostable(Item item)
	{
		return isGreen(item) || isBrown(item);
	}

	public static boolean isBrown(Item item)
	{
		return browns.contains(item);
	}

	public static boolean isGreen(Item item)
	{
		return greens.contains(item);
	}

	public static boolean isCompostableFood(ItemStack itemStack)
	{
		// TODO: optionally use AppleCore's method?
		if (itemStack != null && itemStack.isEdible() && (!(itemStack.getItem() instanceof BowlFoodItem) || itemStack.hasCraftingRemainingItem()))
		{
			for (FoodSpecifier uncompostableFood : uncompostableFoods)
			{
				if (uncompostableFood.matches(itemStack))
					return false;
			}
			return true;
		}
		return false;
	}

	public static void addBrown(Item item)
	{
		browns.add(item);
	}

	public static void addBrown(TagKey<Item> tag)
	{
		browns.addAll(ForgeRegistries.ITEMS.getValues().stream().filter(i -> ContentHelper.isItemTaggedAs(i, tag)).toList());
	}

	public static void addGreen(Item item)
	{
		greens.add(item);
	}

	public static void addGreen(TagKey<Item> tag)
	{
		greens.addAll(ForgeRegistries.ITEMS.getValues().stream().filter(i -> ContentHelper.isItemTaggedAs(i, tag)).toList());
	}

	public static void blacklist(FoodSpecifier foodSpecifier)
	{
		uncompostableFoods.add(foodSpecifier);
	}
}
