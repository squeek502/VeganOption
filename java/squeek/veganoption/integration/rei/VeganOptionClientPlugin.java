package squeek.veganoption.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.helpers.CreativeTabHelper;
import squeek.veganoption.integration.rei.composting.CompostingCategory;
import squeek.veganoption.integration.rei.composting.CompostingDisplay;
import squeek.veganoption.integration.rei.piston.PistonCraftingCategory;
import squeek.veganoption.integration.rei.piston.PistonCraftingDisplay;
import squeek.veganoption.integration.rei.wiki.*;

@REIPluginClient
public class VeganOptionClientPlugin implements REIClientPlugin
{
	@Override
	public void registerDisplays(DisplayRegistry registry)
	{
		int i = 0;
		for (CraftingRecipe recipe : Modifiers.recipes.recipes)
		{
			registry.add(DefaultCraftingDisplay.of(new RecipeHolder<>(new ResourceLocation(ModInfo.MODID_LOWER, "conversion_recipe_" + i), recipe)));
			i++;
		}

		for (PistonCraftingRecipe recipe : PistonCraftingRegistry.getRecipes())
		{
			registry.add(PistonCraftingDisplay.of(recipe));
		}

		registry.add(CompostingDisplay.of(EntryIngredients.of(Composting.rottenPlants.get()), 1, 0));
		registry.add(CompostingDisplay.of(EntryIngredients.of(Composting.compostItem.get()), 2, 1));

		DescriptionRegistry.registerAllDescriptions();

		DescriptionMaker craftingMaker = new CraftingDescriptionMaker();
		DescriptionMaker usageMaker = new UsageDescriptionMaker();

		for (ItemStack topic : DescriptionRegistry.itemsWithUsageDescriptions)
		{
			for (DescriptionDisplay display : usageMaker.createDisplays(topic))
			{
				registry.add(display);
			}
		}

		for (ItemStack topic : DescriptionRegistry.itemsWithCraftingDescriptions)
		{
			for (DescriptionDisplay display : craftingMaker.createDisplays(topic))
			{
				registry.add(display);
			}
		}
	}

	@Override
	public void registerCategories(CategoryRegistry registry)
	{
		registry.add(new PistonCraftingCategory());
		registry.add(new CompostingCategory());
		registry.add(new CraftingDescriptionCategory());
		registry.add(new UsageDescriptionCategory());
	}

	@Override
	public void registerEntries(EntryRegistry registry)
	{
		registry.removeEntryIf(s -> CreativeTabHelper.FAKE_ITEMS.getEntries().stream().anyMatch(h -> h.getId().equals(s.getIdentifier())));
	}

	public static class Categories
	{
		public static final CategoryIdentifier<PistonCraftingDisplay> PISTON_CRAFTING = CategoryIdentifier.of(new ResourceLocation(ModInfo.MODID_LOWER, "piston_crafting"));
		public static final CategoryIdentifier<CompostingDisplay> COMPOSTING = CategoryIdentifier.of(new ResourceLocation(ModInfo.MODID_LOWER, "composting"));
		public static final CategoryIdentifier<CraftingDescriptionDisplay> WIKI_CRAFTING = CategoryIdentifier.of(new ResourceLocation(ModInfo.MODID_LOWER, "crafting"));
		public static final CategoryIdentifier<UsageDescriptionDisplay> WIKI_USAGE = CategoryIdentifier.of(new ResourceLocation(ModInfo.MODID_LOWER, "usage"));
	}
}
