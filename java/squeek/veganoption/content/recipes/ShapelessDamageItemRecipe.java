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
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.RandomHelper;

/**
 * A variation of the ShapelessRecipe which damages damageable items in the recipe rather than deleting them.
 */
public class ShapelessDamageItemRecipe extends ShapelessRecipe
{
	private static final int CRAFTING_TABLE_DIM = 3;

	final String group;
	final CraftingBookCategory category;
	final ItemStack result;
	final NonNullList<Ingredient> ingredients;
	final boolean isSimple;

	@SuppressWarnings("unchecked")
	public ShapelessDamageItemRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients)
	{
		super(group, category, result, ingredients);
		this.group = group;
		this.category = category;
		this.result = result;
		this.ingredients = ingredients;
		this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
	{
		NonNullList<ItemStack> craftRemainders = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
		for (int i = 0; i < inv.getContainerSize(); i++)
		{
			ItemStack stackInSlot = inv.getItem(i);
			if (stackInSlot.hasCraftingRemainingItem())
			{
				craftRemainders.set(i, stackInSlot.getCraftingRemainingItem());
			}
			else if (stackInSlot.isDamageableItem())
			{
				ItemStack copy = stackInSlot.copy();
				if (copy.hurt(1, RandomHelper.randomSource, null))
					copy.shrink(1);
				craftRemainders.set(i, copy);
			}
		}
		return craftRemainders;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return RecipeRegistration.DAMAGE_ITEM_SHAPELESS_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType()
	{
		return RecipeType.CRAFTING;
	}

	// ugly copy of ShapelessRecipe.Serializer
	public static class Serializer implements RecipeSerializer<ShapelessDamageItemRecipe> {
		private static final net.minecraft.resources.ResourceLocation NAME = new net.minecraft.resources.ResourceLocation(ModInfo.MODID_LOWER, RecipeRegistration.TAG_MATCH_SHAPELESS_NAME);
		private static final Codec<ShapelessDamageItemRecipe> CODEC = RecordCodecBuilder.create(
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
				.apply(recipe, ShapelessDamageItemRecipe::new)
		);

		@Override
		public Codec<ShapelessDamageItemRecipe> codec() {
			return CODEC;
		}

		@Override
		public ShapelessDamageItemRecipe fromNetwork(FriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

			for(int j = 0; j < nonnulllist.size(); ++j) {
				nonnulllist.set(j, Ingredient.fromNetwork(buffer));
			}

			ItemStack itemstack = buffer.readItem();
			return new ShapelessDamageItemRecipe(s, craftingbookcategory, itemstack, nonnulllist);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, ShapelessDamageItemRecipe recipe) {
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
