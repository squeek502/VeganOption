package squeek.veganoption.integration.jei.description;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;

public class CraftingDescHandler implements IRecipeHandler<CraftingDescWrapper>
{
	@Override
	@Nonnull
	public Class<CraftingDescWrapper> getRecipeClass()
	{
		return CraftingDescWrapper.class;
	}

	@Override
	@Nonnull
	public String getRecipeCategoryUid(@Nonnull CraftingDescWrapper recipe)
	{
		return VOPlugin.VORecipeCategoryUid.CRAFTING;
	}

	@Override
	@Nonnull
	public IRecipeWrapper getRecipeWrapper(@Nonnull CraftingDescWrapper recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CraftingDescWrapper recipe)
	{
		return true;
	}
}
