package squeek.veganoption.integration.jei.drops;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import squeek.veganoption.content.modifiers.DropsModifier;

import javax.annotation.Nonnull;

public class DropsWrapper extends BlankRecipeWrapper
{
	public final DropsModifier.DropInfo drop;

	public DropsWrapper(DropsModifier.DropInfo drop)
	{
		this.drop = drop;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients)
	{
		ingredients.setInput(ItemStack.class, drop.dropper.itemStackForComparison);
		ingredients.setOutput(ItemStack.class, drop.drop.itemStack);
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

		FontRenderer fontRenderer = minecraft.fontRenderer;
		String dropChance = String.format("%.0f%%", drop.drop.dropChance * 100f);
		fontRenderer.drawString(dropChance, recipeWidth / 2 - fontRenderer.getStringWidth(dropChance) / 2, 8, 0x8b8b8b, false);

		if (drop.drop.dropsMin != drop.drop.dropsMax)
		{
			fontRenderer.drawString("-", recipeWidth / 2 + 42, 19, 0x8b8b8b, false);
		}
	}
}
