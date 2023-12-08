package squeek.veganoption.content.modules;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
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

public class Resin implements IContentModule
{
	public static RegistryObject<Item> resin;
	public static RegistryObject<Item> rosin;

	@Override
	public void create()
	{
		resin = REGISTER_ITEMS.register("resin", () -> new Item(new Item.Properties()));
		rosin = REGISTER_ITEMS.register("rosin", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.ROSIN).add(rosin.get());
		provider.tagW(ContentHelper.ItemTags.RESIN).add(resin.get());
		provider.tagW(ContentHelper.ItemTags.SLIMEBALLS).add(resin.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(resin.get());
		provider.basicItem(rosin.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(resin.get()), RecipeCategory.MISC, rosin.get(), 0.2f, ContentHelper.DEFAULT_SMELT_TIME);
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"spruce_log_resin",
			new SimpleBlockDropLootModifier(
				Blocks.SPRUCE_LOG,
				resin.get(),
				ConstantValue.exactly(0.01f),
				ConstantValue.exactly(1)
			)
		);
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.SLIME_BALL), () -> Ingredient.of(ContentHelper.ItemTags.SLIMEBALLS));
	}
}
