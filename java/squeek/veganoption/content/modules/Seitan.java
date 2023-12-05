package squeek.veganoption.content.modules;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.ObjectHolder;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.items.ItemWashableWheat;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

// TODO: Tooltips, usage, and recipe text
public class Seitan implements IContentModule
{
	public static RegistryObject<Item> wheatFlour;
	public static RegistryObject<Item> wheatDough;
	public static RegistryObject<Item> seitanUnwashed;
	public static RegistryObject<Item> seitanRaw;
	public static RegistryObject<Item> seitanCooked;

	@ObjectHolder(registryName = "minecraft:item", value = "minecraft:piston")
	public static Item wheatCrusher;

	@Override
	public void create()
	{
		wheatFlour = REGISTER_ITEMS.register("wheat_flour", ItemWashableWheat::new);
		wheatDough = REGISTER_ITEMS.register("wheat_dough", ItemWashableWheat::new);
		seitanUnwashed = REGISTER_ITEMS.register("seitan_unwashed", ItemWashableWheat::new);
		seitanRaw = REGISTER_ITEMS.register("seitan_raw", () -> new Item(new Item.Properties()));
		seitanCooked = REGISTER_ITEMS.register("seitan_cooked", () -> new Item(new Item.Properties()
			.food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).build())));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.WHEAT_FLOUR).add(wheatFlour.get());
		provider.tagW(ContentHelper.ItemTags.WHEAT_DOUGH).add(wheatDough.get());
		provider.tagW(ContentHelper.ItemTags.RAW_SEITAN).add(seitanRaw.get());

		// todo: cooked seitan as meat alternative.
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(ContentHelper.ItemTags.RAW_SEITAN), RecipeCategory.FOOD, seitanCooked.get(), 0.35f, ContentHelper.DEFAULT_SMELT_TIME);
	}

	@Override
	public void finish()
	{
		PistonCraftingRegistry.register(new PistonCraftingRecipe(new ItemStack(wheatFlour.get()), new InputItemStack(Items.WHEAT)));
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(seitanCooked.get());
		provider.basicItem(seitanRaw.get());
		provider.basicItem(seitanUnwashed.get());
		provider.basicItem(wheatDough.get());
		provider.basicItem(wheatFlour.get());
	}
}
