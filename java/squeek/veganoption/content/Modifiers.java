package squeek.veganoption.content;

import squeek.veganoption.content.modifiers.CraftingModifier;
import squeek.veganoption.content.modifiers.DropsModifier;
import squeek.veganoption.content.modifiers.RecipeModifier;

public class Modifiers
{
	public static final RecipeModifier recipes = new RecipeModifier();
	public static final DropsModifier drops = new DropsModifier();
	public static final CraftingModifier crafting = new CraftingModifier();
}
