package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

// currently depends on Kapok
public class Feather implements IContentModule
{
	public static RegistryObject<Item> fauxFeather;

	@Override
	public void create()
	{
		fauxFeather = REGISTER_ITEMS.register("faux_feather", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.FEATHERS).add(fauxFeather.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, fauxFeather.get())
			.requires(ContentHelper.ItemTags.FLUFFY_MATERIAL)
			.requires(ContentHelper.ItemTags.PLASTIC_ROD)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) //todo
			.save(output);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.withExistingParent(fauxFeather.getId().getPath(), provider.mcLoc("feather"));
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(Ingredient.of(Items.FEATHER), Ingredient.of(ContentHelper.ItemTags.FEATHERS));
	}
}
