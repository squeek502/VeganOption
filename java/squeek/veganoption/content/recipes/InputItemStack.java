package squeek.veganoption.content.recipes;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class InputItemStack
{
	public ItemStack wrappedItemStack = null;
	public List<ItemStack> oreDictItemStacks = null;
	protected int oreDictStackSize = 1;

	public InputItemStack(String oredict, int stackSize)
	{
		this(oredict);
		this.oreDictStackSize = stackSize;
	}

	public InputItemStack(ItemStack itemStack)
	{
		this((Object) itemStack);
	}

	public InputItemStack(Item item)
	{
		this((Object) item);
	}

	public InputItemStack(Block block)
	{
		this((Object) block);
	}

	@SuppressWarnings("unchecked")
	public InputItemStack(Object object)
	{
		if (object instanceof String)
			oreDictItemStacks = OreDictionary.getOres((String) object);
		else if (object instanceof ArrayList)
			oreDictItemStacks = (List<ItemStack>) object;
		else if (object instanceof ItemStack)
			wrappedItemStack = (ItemStack) object;
		else if (object instanceof Block)
			wrappedItemStack = new ItemStack((Block) object, 1, OreDictionary.WILDCARD_VALUE);
		else if (object instanceof Item)
			wrappedItemStack = new ItemStack((Item) object, 1, OreDictionary.WILDCARD_VALUE);
		else
			throw new RuntimeException("Unsupported InputItemStack input: " + object);
	}

	protected InputItemStack(List<ItemStack> itemStacks)
	{
		this.oreDictItemStacks = itemStacks;
	}

	public boolean matches(ItemStack input)
	{
		return matches(input, false);
	}

	public boolean matches(ItemStack input, boolean strict)
	{
		if (wrappedItemStack != null)
			return OreDictionary.itemMatches(wrappedItemStack, input, strict);
		else if (oreDictItemStacks != null)
		{
			for (ItemStack oreDictItemStack : oreDictItemStacks)
			{
				if (OreDictionary.itemMatches(oreDictItemStack, input, strict))
					return true;
			}
		}
		return false;
	}

	public int stackSize()
	{
		return wrappedItemStack != null ? wrappedItemStack.stackSize : (oreDictItemStacks != null ? oreDictStackSize : 0);
	}

	public boolean isOreDict()
	{
		return oreDictItemStacks != null;
	}
}
