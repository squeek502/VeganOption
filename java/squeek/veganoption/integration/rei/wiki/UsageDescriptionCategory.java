package squeek.veganoption.integration.rei.wiki;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.network.chat.Component;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.rei.VeganOptionClientPlugin;

public class UsageDescriptionCategory extends DescriptionCategory<UsageDescriptionDisplay>
{
	@Override
	public CategoryIdentifier<? extends UsageDescriptionDisplay> getCategoryIdentifier()
	{
		return VeganOptionClientPlugin.Categories.WIKI_USAGE;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.usage"));
	}

	@Override
	protected Component getRelatedTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.byproducts"));
	}
}
