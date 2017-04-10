package squeek.veganoption.integration.jei.description;

import mezz.jei.api.IGuiHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;

public class UsageDescCategory extends DescriptionCategory
{
	public UsageDescCategory(IGuiHelper guiHelper)
	{
		super(guiHelper, LangHelper.translate("nei.usage"));
	}

	@Override
	@Nonnull
	public String getUid()
	{
		return VOPlugin.VORecipeCategoryUid.USAGE;
	}
}
