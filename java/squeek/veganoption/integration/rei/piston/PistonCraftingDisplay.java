package squeek.veganoption.integration.rei.piston;

import com.google.common.collect.Lists;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.integration.rei.REIHelper;
import squeek.veganoption.integration.rei.VeganOptionClientPlugin;

import java.util.List;
import java.util.Optional;

public class PistonCraftingDisplay extends BasicDisplay
{
	public static PistonCraftingDisplay of(PistonCraftingRecipe recipe)
	{
		EntryIngredient[] inputItems = new EntryIngredient[recipe.itemInputs.size()];
		for (int i = 0; i < recipe.itemInputs.size(); i++)
		{
			InputItemStack input = recipe.itemInputs.get(i);
			EntryIngredient.Builder resolvedInputs = EntryIngredient.builder();
			for (ItemStack resolvedItem : input.resolve())
			{
				resolvedInputs.add(EntryStacks.of(resolvedItem));
			}
			inputItems[i] = resolvedInputs.build();
		}

		// Prevent the creation of an empty slot for fluid-only recipes
		EntryIngredient[] outputItems = recipe.itemOutputs.isEmpty() ? new EntryIngredient[] {} : new EntryIngredient[] { EntryIngredients.ofItemStacks(recipe.itemOutputs) };

		// Prevent the creation of empty slots for recipes that do not require or create fluids
		List<EntryIngredient> in = recipe.fluidInput.isEmpty() ? List.of(inputItems) : Lists.asList(EntryIngredients.of(REIHelper.getArchitecturyFluidStackFrom(recipe.fluidInput)), inputItems);
		List<EntryIngredient> out = recipe.fluidOutput.isEmpty() ? List.of(outputItems) : Lists.asList(EntryIngredients.of(REIHelper.getArchitecturyFluidStackFrom(recipe.fluidOutput)), outputItems);

		return new PistonCraftingDisplay(in, out, Optional.empty());
	}

	public PistonCraftingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
		super(inputs, outputs, location);
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier()
	{
		return VeganOptionClientPlugin.Categories.PISTON_CRAFTING;
	}
}
