package squeek.veganoption.integration.jei.description;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;

public class UsageDescHandler implements IRecipeHandler<UsageDescWrapper>
{
	@Override
	@Nonnull
	public Class<UsageDescWrapper> getRecipeClass()
	{
		return UsageDescWrapper.class;
	}

	@Override
	@Nonnull
	public String getRecipeCategoryUid()
	{
		return VOPlugin.VORecipeCategoryUid.USAGE;
	}

	@Override
	@Nonnull
	public String getRecipeCategoryUid(@Nonnull UsageDescWrapper recipe)
	{
		return VOPlugin.VORecipeCategoryUid.USAGE;
	}

	@Override
	@Nonnull
	public IRecipeWrapper getRecipeWrapper(@Nonnull UsageDescWrapper recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull UsageDescWrapper recipe)
	{
		return true;
	}
}
