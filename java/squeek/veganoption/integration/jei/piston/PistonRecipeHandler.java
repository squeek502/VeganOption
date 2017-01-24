package squeek.veganoption.integration.jei.piston;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;

public class PistonRecipeHandler implements IRecipeHandler<PistonRecipeWrapper>
{
	@Override
	@Nonnull
	public Class<PistonRecipeWrapper> getRecipeClass()
	{
		return PistonRecipeWrapper.class;
	}

	@Override
	@Nonnull
	@Deprecated
	public String getRecipeCategoryUid()
	{
		return VOPlugin.VORecipeCategoryUid.PISTON;
	}

	@Override
	@Nonnull
	public String getRecipeCategoryUid(@Nonnull PistonRecipeWrapper recipe)
	{
		return VOPlugin.VORecipeCategoryUid.PISTON;
	}

	@Override
	@Nonnull
	public IRecipeWrapper getRecipeWrapper(@Nonnull PistonRecipeWrapper recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull PistonRecipeWrapper recipe)
	{
		return true;
	}
}
