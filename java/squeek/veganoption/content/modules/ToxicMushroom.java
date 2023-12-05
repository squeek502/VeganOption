package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class ToxicMushroom implements IContentModule
{
	public static RegistryObject<Item> falseMorel;
	public static RegistryObject<Item> falseMorelFermented;

	@Override
	public void create()
	{
		falseMorel = REGISTER_ITEMS.register("false_morel", () -> new Item(new Item.Properties().food(Foods.SPIDER_EYE)));
		falseMorelFermented = REGISTER_ITEMS.register("false_morel_fermented", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.REAGENT_POISONOUS)
			.add(falseMorel.get())
			.add(Items.SPIDER_EYE);
		provider.tagW(ContentHelper.ItemTags.REAGENT_FERMENTED)
			.add(falseMorelFermented.get())
			.add(Items.FERMENTED_SPIDER_EYE);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(falseMorel.get());
		provider.basicItem(falseMorelFermented.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, falseMorelFermented.get())
			.requires(falseMorel.get())
			.requires(Items.BROWN_MUSHROOM)
			.requires(Items.SUGAR)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output);
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"mycelium_false_morel",
			new SimpleBlockDropLootModifier(
				Blocks.MYCELIUM,
				falseMorel.get(),
				ConstantValue.exactly(0.15f),
				ConstantValue.exactly(1)
			)
		);
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(Ingredient.of(Items.SPIDER_EYE), Ingredient.of(ContentHelper.ItemTags.REAGENT_POISONOUS));
		Modifiers.recipes.excludeOutput(Items.FERMENTED_SPIDER_EYE);
		Modifiers.recipes.convertInput(Ingredient.of(Items.FERMENTED_SPIDER_EYE), Ingredient.of(ContentHelper.ItemTags.REAGENT_FERMENTED));
	}
}
