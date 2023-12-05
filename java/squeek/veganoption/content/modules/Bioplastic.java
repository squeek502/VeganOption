package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.*;
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
		// todo: investigate balance here. vanilla made it so we cant output more than 1 item. originally this produced 2 bioplastic sheets.
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(ContentHelper.ItemTags.STARCH), RecipeCategory.MISC, bioplastic.get(), 0.35f, ContentHelper.DEFAULT_SMELT_TIME);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, plasticRod.get(), 4)
			.pattern("p")
			.pattern("p")
			.define('p', ContentHelper.ItemTags.PLASTIC_SHEET)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) // todo
			.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, Items.BLAZE_ROD) // Brewing... I think?
			.requires(ContentHelper.ItemTags.PLASTIC_ROD)
			.requires(ContentHelper.ItemTags.ROSIN)
			.requires(ContentHelper.ItemTags.WAX)
			.requires(Items.FLINT_AND_STEEL) // todo: test. may need its own recipe type for some stupid reason.
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) // todo
			.save(output);

		// todo: kinda hacky implementation here
		Modifiers.crafting.addInputsToKeepForOutput(Items.BLAZE_ROD, Items.FLINT_AND_STEEL);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(bioplastic.get());
		provider.basicItem(plasticRod.get());
	}
}
