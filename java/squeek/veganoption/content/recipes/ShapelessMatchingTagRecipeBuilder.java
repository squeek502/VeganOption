package squeek.veganoption.content.recipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

// Ugly copy of ShapelessRecipeBuilder.
public class ShapelessMatchingTagRecipeBuilder implements RecipeBuilder
{
	private final RecipeCategory category;
	private final Item result;
	private final int count;
	private final ItemStack resultStack;
	private final NonNullList<Ingredient> ingredients = NonNullList.create();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	@Nullable
	private String group;

	public ShapelessMatchingTagRecipeBuilder(RecipeCategory category, ItemLike result, int count)
	{
		this(category, new ItemStack(result, count));
	}

	public ShapelessMatchingTagRecipeBuilder(RecipeCategory category, ItemStack result)
	{
		this.category = category;
		this.result = result.getItem();
		this.count = result.getCount();
		this.resultStack = result;
	}

	public static ShapelessMatchingTagRecipeBuilder shapeless(RecipeCategory category, ItemLike result)
	{
		return new ShapelessMatchingTagRecipeBuilder(category, result, 1);
	}

	public static ShapelessMatchingTagRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count)
	{
		return new ShapelessMatchingTagRecipeBuilder(category, result, count);
	}

	public ShapelessMatchingTagRecipeBuilder requires(TagKey<Item> tag)
	{
		return this.requires(Ingredient.of(tag));
	}

	public ShapelessMatchingTagRecipeBuilder requires(ItemLike itemLike)
	{
		return this.requires(itemLike, 1);
	}

	public ShapelessMatchingTagRecipeBuilder requires(ItemLike itemLike, int count)
	{
		for (int i = 0; i < count; ++i)
		{
			this.requires(Ingredient.of(itemLike));
		}

		return this;
	}

	public ShapelessMatchingTagRecipeBuilder requires(Ingredient ingredient)
	{
		return this.requires(ingredient, 1);
	}

	public ShapelessMatchingTagRecipeBuilder requires(Ingredient ingredient, int count)
	{
		for (int i = 0; i < count; ++i)
		{
			this.ingredients.add(ingredient);
		}

		return this;
	}

	public ShapelessMatchingTagRecipeBuilder unlockedBy(String name, Criterion<?> predicate)
	{
		this.criteria.put(name, predicate);
		return this;
	}

	public ShapelessMatchingTagRecipeBuilder group(@Nullable String group)
	{
		this.group = group;
		return this;
	}

	@Override
	public Item getResult()
	{
		return this.result;
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation id)
	{
		this.ensureValid(id);
		Advancement.Builder advancement$builder = output.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
			.rewards(AdvancementRewards.Builder.recipe(id))
			.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement$builder::addCriterion);
		ShapelessMatchingTagRecipe recipe = new ShapelessMatchingTagRecipe(
			Objects.requireNonNullElse(this.group, ""),
			RecipeBuilder.determineBookCategory(this.category),
			this.resultStack,
			this.ingredients
		);
		output.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
	}

	private void ensureValid(ResourceLocation id)
	{
		if (this.criteria.isEmpty())
		{
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}
}
