package squeek.veganoption.content.modules;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.recipes.CookingRecipeWithCountBuilder;
import squeek.veganoption.content.recipes.ShapelessDamageItemRecipeBuilder;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class Bioplastic implements IContentModule
{
	public static RegistryObject<Item> bioplastic;
	public static RegistryObject<Item> plasticRod;

	@Override
	public void create()
	{
		bioplastic = REGISTER_ITEMS.register("bioplastic", () -> new Item(new Item.Properties()));
		plasticRod = REGISTER_ITEMS.register("plastic_rod", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.PLASTIC_SHEET).add(bioplastic.get());
		provider.tagW(ContentHelper.ItemTags.PLASTIC_ROD).add(plasticRod.get());
		provider.tagW(ContentHelper.ItemTags.RODS).addTag(ContentHelper.ItemTags.PLASTIC_ROD);
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		CookingRecipeWithCountBuilder.smelting(RecipeCategory.MISC, bioplastic.get(), 2, Ingredient.of(ContentHelper.ItemTags.STARCH), 0.35f)
			.unlockedBy("has_potato_starch", provider.hasW(Egg.potatoStarch.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, plasticRod.get(), 4)
			.pattern("p")
			.pattern("p")
			.define('p', ContentHelper.ItemTags.PLASTIC_SHEET)
			.unlockedBy("has_plastic_sheet", provider.hasW(bioplastic.get()))
			.save(output);

		ShapelessDamageItemRecipeBuilder.shapeless(RecipeCategory.BREWING, Items.BLAZE_ROD)
			.requires(ContentHelper.ItemTags.PLASTIC_ROD)
			.requires(ContentHelper.ItemTags.ROSIN)
			.requires(ContentHelper.ItemTags.WAX)
			.requires(Items.FLINT_AND_STEEL)
			.unlockedBy("has_plastic_rod", provider.hasW(plasticRod.get()))
			.save(output);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(bioplastic.get());
		provider.basicItem(plasticRod.get());
	}
}
