package squeek.veganoption.content.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import squeek.veganoption.content.Modifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversionRecipe extends CustomRecipe
{
	private final Map<List<ItemStack>, CraftingRecipe> cachedRecipes = new HashMap<>();

	public ConversionRecipe(CraftingBookCategory category)
	{
		super(category);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level level)
	{
		for (CraftingRecipe recipe : Modifiers.recipes.recipes)
		{
			if (recipe.matches(inv, level))
			{
				cachedRecipes.put(inv.getItems(), recipe);
				return true;
			}
		}

		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess access)
	{
		return cachedRecipes.get(inv.getItems()).assemble(inv, access);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return null;
	}
}
