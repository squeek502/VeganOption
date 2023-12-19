package squeek.veganoption.content.modules;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

import java.util.function.Supplier;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class Gunpowder implements IContentModule
{
	public static Supplier<Item> sulfur;
	public static Supplier<Item> saltpeter;

	@Override
	public void create()
	{
		sulfur = REGISTER_ITEMS.register("sulfur", () -> new Item(new Item.Properties()));
		saltpeter = REGISTER_ITEMS.register("saltpeter", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.SULFUR).add(sulfur.get());
		provider.tagW(ContentHelper.ItemTags.SALTPETER).add(saltpeter.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(sulfur.get());
		provider.basicItem(saltpeter.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER)
			.requires(Items.CHARCOAL)
			.requires(ContentHelper.ItemTags.SULFUR)
			.requires(ContentHelper.ItemTags.SALTPETER)
			.unlockedBy("has_sulfur", provider.hasW(sulfur.get()))
			.save(output);
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"netherrack_sulfur",
			new SimpleBlockDropLootModifier(
				Blocks.NETHERRACK,
				sulfur.get(),
				ConstantValue.exactly(0.02f),
				ConstantValue.exactly(1)
			)
		);

		provider.add(
			"sandstone_saltpeter",
			new SimpleBlockDropLootModifier(
				Blocks.SANDSTONE,
				saltpeter.get(),
				ConstantValue.exactly(0.02f),
				UniformGenerator.between(1, 3)
			)
		);
	}
}
