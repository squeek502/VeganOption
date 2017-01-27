package squeek.veganoption.integration.jei.drops;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;

public class DropsHandler implements IRecipeHandler<DropsWrapper>
{
	@Override
	@Nonnull
	public Class<DropsWrapper> getRecipeClass()
	{
		return DropsWrapper.class;
	}

	@Override
	@Nonnull
	public String getRecipeCategoryUid()
	{
		return VOPlugin.VORecipeCategoryUid.DROPS;
	}

	@Override
	@Nonnull
	public String getRecipeCategoryUid(@Nonnull DropsWrapper recipe)
	{
		return VOPlugin.VORecipeCategoryUid.DROPS;
	}

	@Override
	@Nonnull
	public IRecipeWrapper getRecipeWrapper(@Nonnull DropsWrapper recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull DropsWrapper recipe)
	{
		return true;
	}
}
