package squeek.veganoption.integration.jei.drops;

import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier;

import java.util.ArrayList;
import java.util.List;

public class DropsMaker
{
	public static List<DropsWrapper> getRecipes()
	{
		List<DropsWrapper> recipes = new ArrayList<DropsWrapper>();
		for (DropsModifier.DropInfo drop : Modifiers.drops.getAllDrops())
		{
			recipes.add(new DropsWrapper(drop));
		}
		return recipes;
	}
}
