package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.loot.SimpleBlockDropLootModifier;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

public class DollsEye implements IContentModule
{
	public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
		.nutrition(2)
		.saturationMod(0.8f)
		.effect(() -> new MobEffectInstance(MobEffects.POISON, 5, 0), 1f)
		.build();

	public static RegistryObject<Item> dollsEye;

	@Override
	public void create()
	{
		dollsEye = REGISTER_ITEMS.register("dolls_eye", () -> new Item(new Item.Properties().food(FOOD_PROPERTIES)));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		// Spider Eye crafting replacement is handled in ToxicMushroom (False Morel)
		provider.tagW(ContentHelper.ItemTags.REAGENT_POISONOUS).add(dollsEye.get());
	}

	@Override
	public void datagenLootModifiers(GlobalLootModifierProvider provider)
	{
		provider.add(
			"forest_tall_grass_dolls_eye",
			new SimpleBlockDropLootModifier(
				new LootItemCondition[] {
					new LootItemBlockStatePropertyCondition.Builder(Blocks.TALL_GRASS).build(),
					LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(Biomes.FOREST))
						.or(LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(Biomes.BIRCH_FOREST)))
						.or(LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(Biomes.DARK_FOREST)))
						.or(LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(Biomes.SWAMP)))
						.or(LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(Biomes.MANGROVE_SWAMP))).build()
				},
				dollsEye.get(),
				ConstantValue.exactly(0.01f),
				UniformGenerator.between(1, 2)
			));
	}

	@Override
	public void finish()
	{
		PotionBrewing.addMix(Potions.AWKWARD, dollsEye.get(), Potions.POISON);
		PotionBrewing.addMix(Potions.WATER, dollsEye.get(), Potions.MUNDANE);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(dollsEye.get());
	}
}
