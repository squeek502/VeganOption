package squeek.veganoption.integration.jei.composting;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;

public class CompostingRecipeHandler implements IRecipeHandler<CompostingRecipeWrapper>
{
	@Override
	@Nonnull
	public Class<CompostingRecipeWrapper> getRecipeClass()
	{
		return CompostingRecipeWrapper.class;
	}

	@Override
	@Nonnull
	public String getRecipeCategoryUid(CompostingRecipeWrapper recipe)
	{
		return VOPlugin.VORecipeCategoryUid.COMPOSTING;
	}

	@Override
	@Nonnull
	public IRecipeWrapper getRecipeWrapper(CompostingRecipeWrapper recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(CompostingRecipeWrapper recipe)
	{
		return true;
	}
}
