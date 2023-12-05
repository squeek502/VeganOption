package squeek.veganoption.content.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import squeek.veganoption.ModInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A shapeless recipe in which all tag ingredients must match each other.
 *
 * For example, ShapelessMatchingTagRecipe(output, "slimeball", "slimeball") would
 * require both inputs to be the same item (both Resin or both Slimeball, for example);
 * a mixture (1 Resin, 1 Slimeball) would not work
 */
public class ShapelessMatchingTagRecipe extends ShapelessRecipe
{
	private static final int CRAFTING_TABLE_DIM = 3;

	public Map<Ingredient, Integer> requiredMatchingIngredients = new HashMap<>();
	final String group;
	final CraftingBookCategory category;
	final ItemStack result;
	final NonNullList<Ingredient> ingredients;
	final boolean isSimple;

	@SuppressWarnings("unchecked")
	public ShapelessMatchingTagRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients)
	{
		super(group, category, result, ingredients);
		this.group = group;
		this.category = category;
		this.result = result;
		this.ingredients = ingredients;
		this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);

		for (Ingredient ingredient : ingredients)
		{
			int requiredMatches = 1;
			for (Ingredient ingredient1 : ingredients)
			{
				if (ingredient.equals(ingredient1))
					requiredMatches++;
			}
			if (requiredMatches > 1)
				requiredMatchingIngredients.put(ingredient, requiredMatches);
		}
	}

	@Override
	public boolean matches(CraftingContainer craftingContainer, Level level)
	{
		if (!super.matches(craftingContainer, level))
			return false;

		for (Entry<Ingredient, Integer> entry : requiredMatchingIngredients.entrySet())
		{
			Ingredient ingredient = entry.getKey();
			int requiredMatches = entry.getValue();

			for (ItemStack inputItem : craftingContainer.getItems())
			{
				if (!ingredient.test(inputItem))
					continue;
				int matches = 0;
				for (ItemStack possibleStack : ingredient.getItems())
				{
					if (inputItem.getItem() == possibleStack.getItem())
						matches++;
				}
				if (requiredMatches != matches)
					return false;
			}
		}
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return RecipeRegistration.TAG_MATCH_SHAPELESS_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType()
	{
		return RecipeType.CRAFTING;
	}

	// ugly copy of ShapelessRecipe.Serializer
	public static class Serializer implements RecipeSerializer<ShapelessMatchingTagRecipe> {
		private static final net.minecraft.resources.ResourceLocation NAME = new net.minecraft.resources.ResourceLocation(ModInfo.MODID_LOWER, RecipeRegistration.TAG_MATCH_SHAPELESS_NAME);
		private static final Codec<ShapelessMatchingTagRecipe> CODEC = RecordCodecBuilder.create(
			recipe -> recipe.group(
					ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe1 -> recipe1.group),
					CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe1 -> recipe1.category),
					CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(recipe1 -> recipe1.result),
					Ingredient.CODEC_NONEMPTY
						.listOf()
						.fieldOf("ingredients")
						.flatXmap(
							ingredients1 -> {
								Ingredient[] aingredient = ingredients1
									.toArray(Ingredient[]::new); //Forge skip the empty check and immediately create the array.
								if (aingredient.length == 0) {
									return DataResult.error(() -> "No ingredients for shapeless recipe");
								} else {
									return aingredient.length > CRAFTING_TABLE_DIM * CRAFTING_TABLE_DIM
										? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(CRAFTING_TABLE_DIM * CRAFTING_TABLE_DIM))
										: DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
								}
							},
							DataResult::success
						)
						.forGetter(recipe1 -> recipe1.ingredients)
				)
				.apply(recipe, ShapelessMatchingTagRecipe::new)
		);

		@Override
		public Codec<ShapelessMatchingTagRecipe> codec() {
			return CODEC;
		}

		public ShapelessMatchingTagRecipe fromNetwork(FriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

			for(int j = 0; j < nonnulllist.size(); ++j) {
				nonnulllist.set(j, Ingredient.fromNetwork(buffer));
			}

			ItemStack itemstack = buffer.readItem();
			return new ShapelessMatchingTagRecipe(s, craftingbookcategory, itemstack, nonnulllist);
		}

		public void toNetwork(FriendlyByteBuf buffer, ShapelessMatchingTagRecipe recipe) {
			buffer.writeUtf(recipe.group);
			buffer.writeEnum(recipe.category);
			buffer.writeVarInt(recipe.ingredients.size());

			for(Ingredient ingredient : recipe.ingredients) {
				ingredient.toNetwork(buffer);
			}

			buffer.writeItem(recipe.result);
		}
	}
}
