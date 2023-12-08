package squeek.veganoption.content.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

public class SmeltingRecipeWithCount extends SmeltingRecipe
{
	public SmeltingRecipeWithCount(String group, CookingBookCategory bookCategory, Ingredient input, ItemStack result, float xp, int cookTime)
	{
		super(group, bookCategory, input, result, xp, cookTime);
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return RecipeRegistration.SMELTING_COUNT_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<SmeltingRecipeWithCount>
	{
		private static final Codec<SmeltingRecipeWithCount> CODEC = RecordCodecBuilder.create(
			recipe -> recipe.group(
					ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe1 -> recipe1.group),
					CookingBookCategory.CODEC.fieldOf("category").forGetter(recipe1 -> recipe1.category),
					Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe1 -> recipe1.ingredient),
					CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(recipe1 -> recipe1.result),
					Codec.FLOAT.fieldOf("experience").forGetter(recipe1 -> recipe1.experience),
					Codec.INT.fieldOf("cookingtime").forGetter(recipe1 -> recipe1.cookingTime)
				)
				.apply(recipe, SmeltingRecipeWithCount::new)
		);

		@Override
		public Codec<SmeltingRecipeWithCount> codec()
		{
			return CODEC;
		}

		@Override
		public @Nullable SmeltingRecipeWithCount fromNetwork(FriendlyByteBuf buf)
		{
			String group = buf.readUtf();
			CookingBookCategory bookCategory = buf.readEnum(CookingBookCategory.class);
			Ingredient input = Ingredient.fromNetwork(buf);
			ItemStack result = buf.readItem();
			float xp = buf.readFloat();
			int cookTime = buf.readVarInt();
			return new SmeltingRecipeWithCount(group, bookCategory, input, result, xp, cookTime);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, SmeltingRecipeWithCount recipe)
		{
			buf.writeUtf(recipe.group);
			buf.writeEnum(recipe.category());
			recipe.ingredient.toNetwork(buf);
			buf.writeItem(recipe.result);
			buf.writeFloat(recipe.experience);
			buf.writeVarInt(recipe.cookingTime);
		}
	}
}
