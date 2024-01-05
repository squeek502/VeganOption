package squeek.veganoption.integration.rei.wiki;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.network.chat.Component;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.rei.VeganOptionClientPlugin;

public class CraftingDescriptionCategory extends DescriptionCategory<CraftingDescriptionDisplay>
{
	@Override
	public CategoryIdentifier<? extends CraftingDescriptionDisplay> getCategoryIdentifier()
	{
		return VeganOptionClientPlugin.Categories.WIKI_CRAFTING;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.crafting"));
	}

	@Override
	protected Component getRelatedTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.byproduct.of"));
	}
}
