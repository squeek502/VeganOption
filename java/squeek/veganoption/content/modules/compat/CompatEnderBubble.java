package squeek.veganoption.content.modules.compat;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.content.registry.RelationshipRegistry;

public class CompatEnderBubble implements IContentModule
{
	@Override
	public void create()
	{
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ENDER_PEARL)
			.requires(Ingredient.of(FrozenBubble.frozenBubble.get()))
			.requires(Ingredient.of(Ender.rawEnderBucket.get()))
			.unlockedBy("has_raw_ender", provider.hasW(Ender.rawEnderBucket.get()))
			.save(output);
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(FrozenBubble.frozenBubble.get(), Ender.rawEnderBucket.get());
	}
}
