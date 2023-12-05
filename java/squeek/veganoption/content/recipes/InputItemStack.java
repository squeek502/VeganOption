package squeek.veganoption.content.recipes;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

// todo: create a proper ingredient that works with json.
public class InputItemStack
{
	private Ingredient ingredient;
	private int count;

	public InputItemStack(Ingredient ingredient, int count)
	{
		this.ingredient = ingredient;
		this.count = count;
	}

	public InputItemStack(ItemStack itemStack)
	{
		this(Ingredient.of(itemStack), itemStack.getCount());
	}

	public InputItemStack(Item item)
	{
		this(Ingredient.of(item), 1);
	}

	public InputItemStack(TagKey<Item> tag)
	{
		this(tag, 1);
	}

	public InputItemStack(TagKey<Item> tag, int count)
	{
		this(Ingredient.of(tag), count);
	}

	public boolean matches(ItemStack input)
	{
		return ingredient.test(input) && getCount() == input.getCount();
	}

	public int getCount()
	{
		return count;
	}
}
