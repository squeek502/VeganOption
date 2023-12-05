package squeek.veganoption.content.recipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.RegistryObject;

import static squeek.veganoption.VeganOption.REGISTER_RECIPESERIALIZERS;

public class RecipeRegistration
{
	public static RegistryObject<RecipeSerializer<ShapelessMatchingTagRecipe>> TAG_MATCH_SHAPELESS_SERIALIZER;
	public static final String TAG_MATCH_SHAPELESS_NAME = "crafting_shapeless_matching_tags";

	public static void init()
	{
		TAG_MATCH_SHAPELESS_SERIALIZER = REGISTER_RECIPESERIALIZERS.register(TAG_MATCH_SHAPELESS_NAME, ShapelessMatchingTagRecipe.Serializer::new);
	}
}
