package squeek.veganoption.content.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DescriptionRegistry
{
	public static List<ItemStack> itemStacksWithUsageDescriptions = new ArrayList<ItemStack>();
	public static List<ItemStack> itemStacksWithCraftingDescriptions = new ArrayList<ItemStack>();

	public static void registerAllDescriptions()
	{
		long millisecondsStart = System.currentTimeMillis();
		int numRegistered = 0;

		for (Item item : Item.REGISTRY)
		{
			if (item == null || item.getRegistryName() == null)
				continue;

			numRegistered += tryRegisterItemAndSubtypes(item);
		}

		for (Block block : Block.REGISTRY)
		{
			if (block == null || block.getRegistryName() == null)
				continue;

			if (Item.getItemFromBlock(block) == null)
				continue;

			numRegistered += tryRegisterItemAndSubtypes(Item.getItemFromBlock(block));
		}

		long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		String timeTakenString = "took " + (timeSpentInMilliseconds / 1000.0f) + " seconds";
		VeganOption.Log.info("Found and registered " + numRegistered + " items/blocks with description text (" + timeTakenString + ")");
	}

	public static int tryRegisterItemAndSubtypes(Item item)
	{
		int numRegistered = 0;

		List<ItemStack> stacks;
		if (item.getHasSubtypes())
		{
			stacks = new ArrayList<ItemStack>();
			item.getSubItems(item, null, stacks);
		}
		else
			stacks = Collections.singletonList(new ItemStack(item));

		for (ItemStack stack : stacks)
		{
			if (tryRegisterDescriptions(stack))
				numRegistered++;
		}

		return numRegistered;
	}

	public static boolean tryRegisterDescriptions(ItemStack itemStack)
	{
		boolean didRegister = false;
		if (hasUsageText(itemStack) && !MiscHelper.isItemStackInList(itemStacksWithUsageDescriptions, itemStack))
		{
			itemStacksWithUsageDescriptions.add(itemStack);
			didRegister = true;
		}
		if (hasCraftingText(itemStack) && !MiscHelper.isItemStackInList(itemStacksWithCraftingDescriptions, itemStack))
		{
			itemStacksWithCraftingDescriptions.add(itemStack);
			didRegister = true;
		}
		return didRegister;
	}

	public static boolean hasUsageText(ItemStack itemStack)
	{
		return LangHelper.existsRaw(itemStack.getUnlocalizedName() + ".vowiki.usage") || !RelationshipRegistry.getChildren(itemStack).isEmpty();
	}

	public static boolean hasCraftingText(ItemStack itemStack)
	{
		return LangHelper.existsRaw(itemStack.getUnlocalizedName() + ".vowiki.crafting") || !RelationshipRegistry.getParents(itemStack).isEmpty();
	}
}
