package squeek.veganoption.content.recipes;

import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.content.ContentHelper;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Based on {@link SimpleCookingRecipeBuilder}, but with an additional field for output item count.
 */
public class CookingRecipeWithCountBuilder implements RecipeBuilder
{
	private final RecipeCategory category;
	private final CookingBookCategory bookCategory;
	private final ItemLike result;
	private final int count;
	private final Ingredient input;
	private final float xp;
	private final int cookTime;
	private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	@Nonnull
	private String group = "";

	public CookingRecipeWithCountBuilder(RecipeCategory category, CookingBookCategory bookCategory, ItemLike result, int count, Ingredient input, float xp, int cookTime, RecipeSerializer<? extends AbstractCookingRecipe> serializer)
	{
		this.category = category;
		this.bookCategory = bookCategory;
		this.result = result;
		this.count = count;
		this.input = input;
		this.xp = xp;
		this.cookTime = cookTime;
		this.serializer = serializer;
	}

	public static CookingRecipeWithCountBuilder smelting(RecipeCategory category, ItemLike result, int count, Ingredient input, float xp, int cookTime)
	{
		return new CookingRecipeWithCountBuilder(category, determineCookingBookCategory(result), result, count, input, xp, cookTime, RecipeRegistration.SMELTING_COUNT_SERIALIZER.get());
	}

	public static CookingRecipeWithCountBuilder smelting(RecipeCategory category, ItemLike result, int count, Ingredient input, float xp)
	{
		return smelting(category, result, count, input, xp, ContentHelper.DEFAULT_SMELT_TIME);
	}

	private static CookingBookCategory determineCookingBookCategory(ItemLike output)
	{
		if (output.asItem().isEdible())
			return CookingBookCategory.FOOD;
		if (output.asItem() instanceof BlockItem)
			return CookingBookCategory.BLOCKS;
		return CookingBookCategory.MISC;
	}

	@Override
	public RecipeBuilder unlockedBy(String key, Criterion<?> criterion)
	{
		criteria.put(key, criterion);
		return this;
	}

	@Override
	public RecipeBuilder group(@Nullable String group)
	{
		this.group = group == null ? "" : group;
		return this;
	}

	@Override
	public Item getResult()
	{
		return result.asItem();
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation loc)
	{
		ensureValid(loc);
		Advancement.Builder advBuilder = output.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(loc))
			.rewards(AdvancementRewards.Builder.recipe(loc))
			.requirements(AdvancementRequirements.Strategy.OR);
		criteria.forEach(advBuilder::addCriterion);
		output.accept(new CookingRecipeWithCountBuilder.Result(
			loc,
			group,
			bookCategory,
			input,
			result.asItem(),
			count,
			xp,
			cookTime,
			advBuilder.build(loc.withPrefix("recipes/" + category.getFolderName() + "/")),
			serializer
		));
	}

	private void ensureValid(ResourceLocation loc) {
		if (criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + loc);
		}
	}

	record Result(
		ResourceLocation id,
		String group,
		CookingBookCategory category,
		Ingredient ingredient,
		Item result,
		int count,
		float experience,
		int cookingTime,
		AdvancementHolder advancement,
		RecipeSerializer<? extends AbstractCookingRecipe> type
	) implements FinishedRecipe
	{
		@Override
		public void serializeRecipeData(@Nonnull JsonObject root) {
			if (!group.isEmpty()) {
				root.addProperty("group", group);
			}

			root.addProperty("category", category.getSerializedName());
			root.add("ingredient", ingredient.toJson(false));

			JsonObject resultObj = new JsonObject();
			resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(result).toString());
			resultObj.addProperty("count", count);
			root.add("result", resultObj);

			root.addProperty("experience", experience);
			root.addProperty("cookingtime", cookingTime);
		}
	}
}
