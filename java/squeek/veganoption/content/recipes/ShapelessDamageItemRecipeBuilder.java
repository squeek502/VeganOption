package squeek.veganoption.content.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Ugly copy of ShapelessRecipeBuilder.
public class ShapelessDamageItemRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder
{
	private final RecipeCategory category;
	private final Item result;
	private final int count;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	@Nullable
	private String group;

	public ShapelessDamageItemRecipeBuilder(RecipeCategory category, ItemLike result, int count)
	{
		this.category = category;
		this.result = result.asItem();
		this.count = count;
	}

	public static ShapelessDamageItemRecipeBuilder shapeless(RecipeCategory category, ItemLike result)
	{
		return new ShapelessDamageItemRecipeBuilder(category, result, 1);
	}

	public static ShapelessDamageItemRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count)
	{
		return new ShapelessDamageItemRecipeBuilder(category, result, count);
	}

	public ShapelessDamageItemRecipeBuilder requires(TagKey<Item> tag)
	{
		return this.requires(Ingredient.of(tag));
	}

	public ShapelessDamageItemRecipeBuilder requires(ItemLike itemLike)
	{
		return this.requires(itemLike, 1);
	}

	public ShapelessDamageItemRecipeBuilder requires(ItemLike itemLike, int count)
	{
		for (int i = 0; i < count; ++i)
		{
			this.requires(Ingredient.of(itemLike));
		}

		return this;
	}

	public ShapelessDamageItemRecipeBuilder requires(Ingredient ingredient)
	{
		return this.requires(ingredient, 1);
	}

	public ShapelessDamageItemRecipeBuilder requires(Ingredient ingredient, int count)
	{
		for (int i = 0; i < count; ++i)
		{
			this.ingredients.add(ingredient);
		}

		return this;
	}

	public ShapelessDamageItemRecipeBuilder unlockedBy(String name, Criterion<?> predicate)
	{
		this.criteria.put(name, predicate);
		return this;
	}

	public ShapelessDamageItemRecipeBuilder group(@Nullable String group)
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
		output.accept(
			new ShapelessDamageItemRecipeBuilder.Result(
				id,
				this.result,
				this.count,
				this.group == null ? "" : this.group,
				determineBookCategory(this.category),
				this.ingredients,
				advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/"))
			)
		);
	}

	private void ensureValid(ResourceLocation id)
	{
		if (this.criteria.isEmpty())
		{
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}

	public static class Result extends CraftingResult
	{
		private final ResourceLocation id;
		private final Item result;
		private final int count;
		private final String group;
		private final List<Ingredient> ingredients;
		private final AdvancementHolder advancement;

		public Result(
			ResourceLocation id,
			Item result,
			int count,
			String group,
			CraftingBookCategory category,
			List<Ingredient> ingredients,
			AdvancementHolder advancements
		)
		{
			super(category);
			this.id = id;
			this.result = result;
			this.count = count;
			this.group = group;
			this.ingredients = ingredients;
			this.advancement = advancements;
		}

		@Override
		public void serializeRecipeData(JsonObject json)
		{
			super.serializeRecipeData(json);
			if (!this.group.isEmpty())
			{
				json.addProperty("group", this.group);
			}

			JsonArray jsonarray = new JsonArray();

			for (Ingredient ingredient : this.ingredients)
			{
				jsonarray.add(ingredient.toJson(false));
			}

			json.add("ingredients", jsonarray);
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
			if (this.count > 1)
			{
				jsonobject.addProperty("count", this.count);
			}

			json.add("result", jsonobject);
		}

		@Override
		public RecipeSerializer<?> type()
		{
			return RecipeRegistration.DAMAGE_ITEM_SHAPELESS_SERIALIZER.get();
		}

		@Override
		public ResourceLocation id()
		{
			return this.id;
		}

		@Override
		public AdvancementHolder advancement()
		{
			return this.advancement;
		}
	}
}
