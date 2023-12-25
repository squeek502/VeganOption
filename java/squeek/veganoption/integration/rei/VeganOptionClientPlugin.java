package squeek.veganoption.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.helpers.CreativeTabHelper;

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
	}

	@Override
	public void registerEntries(EntryRegistry registry)
	{
		registry.removeEntryIf(s -> CreativeTabHelper.FAKE_ITEMS.getEntries().stream().anyMatch(h -> h.getId().equals(s.getIdentifier())));
	}
}
