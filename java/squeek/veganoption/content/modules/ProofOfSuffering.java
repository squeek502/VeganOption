package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class ProofOfSuffering implements IContentModule
{
	public static RegistryObject<Item> fragmentOfSuffering;
	public static RegistryObject<Item> proofOfSuffering;

	@Override
	public void create()
	{
		fragmentOfSuffering = REGISTER_ITEMS.register("suffering_fragment", () -> new Item(new Item.Properties()));
		proofOfSuffering = REGISTER_ITEMS.register("suffering_proof", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.REAGENT_TEAR)
			.add(Items.GHAST_TEAR)
			.add(proofOfSuffering.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, proofOfSuffering.get())
			.pattern("xxx")
			.pattern("x*x")
			.pattern("xxx")
			.define('x', fragmentOfSuffering.get())
			.define('*', Items.GOLD_NUGGET) // todo: tag
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) // todo
			.save(output);
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"soul_sand_fragment_of_suffering",
			new SimpleBlockDropLootModifier(
				Blocks.SOUL_SAND,
				fragmentOfSuffering.get(),
				ConstantValue.exactly(0.05f),
				UniformGenerator.between(1, 3)
			)
		);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(fragmentOfSuffering.get());
		provider.basicItem(proofOfSuffering.get());
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(Ingredient.of(Items.GHAST_TEAR), Ingredient.of(ContentHelper.ItemTags.REAGENT_TEAR));
		PotionBrewing.addMix(Potions.AWKWARD, proofOfSuffering.get(), Potions.REGENERATION);
		PotionBrewing.addMix(Potions.WATER, proofOfSuffering.get(), Potions.MUNDANE);
	}
}
