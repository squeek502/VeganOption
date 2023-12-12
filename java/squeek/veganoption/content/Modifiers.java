package squeek.veganoption.content;

import squeek.veganoption.content.modifiers.BottleModifier;
import squeek.veganoption.content.modifiers.CraftingModifier;
import squeek.veganoption.content.modifiers.EggModifier;
import squeek.veganoption.content.modifiers.RecipeModifier;

public class Modifiers
{
	public static final RecipeModifier recipes = new RecipeModifier();
	public static final CraftingModifier crafting = new CraftingModifier();
	public static final EggModifier eggs = new EggModifier();
	public static final BottleModifier bottles = new BottleModifier();
}
