package squeek.veganoption.integration.jei.description;

import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class CraftingDescWrapper extends DescriptionWrapper
{
	public CraftingDescWrapper(ItemStack itemStack, List<ItemStack> related, List<ItemStack> referenced, List<String> text)
	{
		super(itemStack, related, referenced, text);
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients)
	{
		ingredients.setOutput(ItemStack.class, itemStack);
	}
}
