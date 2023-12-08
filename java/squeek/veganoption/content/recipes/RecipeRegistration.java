package squeek.veganoption.content.recipes;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.Modifiers;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.REGISTER_RECIPESERIALIZERS;

public class RecipeRegistration
{
	public static RegistryObject<RecipeSerializer<ShapelessMatchingTagRecipe>> TAG_MATCH_SHAPELESS_SERIALIZER;
	public static RegistryObject<RecipeSerializer<ConversionRecipe>> CONVERSION_RECIPE_SERIALIZER;
	public static final String TAG_MATCH_SHAPELESS_NAME = "crafting_shapeless_matching_tags";
	public static final String CONVERSION_RECIPE_NAME = "conversion";

	public static void init()
	{
		TAG_MATCH_SHAPELESS_SERIALIZER = REGISTER_RECIPESERIALIZERS.register(TAG_MATCH_SHAPELESS_NAME, ShapelessMatchingTagRecipe.Serializer::new);
		CONVERSION_RECIPE_SERIALIZER = REGISTER_RECIPESERIALIZERS.register(CONVERSION_RECIPE_NAME, () -> new SimpleCraftingRecipeSerializer<>(ConversionRecipe::new));

		NeoForge.EVENT_BUS.register(Modifiers.recipes);
	}

	// todo: core module?
	public static void datagen(RecipeOutput output)
	{
		SpecialRecipeBuilder.special(CONVERSION_RECIPE_SERIALIZER.get()).save(output, new ResourceLocation(MODID_LOWER, CONVERSION_RECIPE_NAME));
	}
}
