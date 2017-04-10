package squeek.veganoption.integration.jei.description;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nullable;
import java.util.List;

import static squeek.veganoption.integration.jei.description.DescriptionCategory.fontRenderer;

public abstract class DescriptionWrapper extends BlankRecipeWrapper
{
	public final ItemStack itemStack;
	@Nullable
	public final List<ItemStack> related;
	@Nullable
	public final List<ItemStack> referenced;
	public final List<String> text;

	public DescriptionWrapper(ItemStack itemStack, @Nullable List<ItemStack> related, @Nullable List<ItemStack> referenced, List<String> text)
	{
		this.itemStack = itemStack;
		this.related = related;
		this.referenced = referenced;
		this.text = text;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		IDrawable slotDrawable = VOPlugin.jeiHelpers.getGuiHelper().getSlotDrawable();
		int xPos = (recipeWidth - slotDrawable.getWidth()) / 2;
		int yPos = 0;
		slotDrawable.draw(minecraft, xPos, yPos);
		yPos += slotDrawable.getHeight() + 4;

		if (related != null && related.size() > 0)
		{
			String relatedTitle = (this instanceof UsageDescWrapper) ? LangHelper.translate("nei.byproducts") : LangHelper.translate("nei.byproduct.of");
			xPos = DescriptionCategory.WIDTH / 2 - minecraft.fontRendererObj.getStringWidth(relatedTitle) / 2;
			minecraft.fontRendererObj.drawString(relatedTitle, xPos, yPos, ColorHelper.DEFAULT_TEXT_COLOR);

			int relatedWidth = related.size() * 18;
			xPos = (recipeWidth - relatedWidth) / 2;
			yPos += minecraft.fontRendererObj.FONT_HEIGHT;

			GlStateManager.color(1f, 1f, 1f, 1);
			for (ItemStack ignored : related)
			{
				slotDrawable.draw(minecraft, xPos, yPos);
				xPos += 18;
			}

			yPos += minecraft.fontRendererObj.FONT_HEIGHT * DescriptionMaker.DESC_DISPLACEMENT_RELATED - 4;
		}

		xPos = 0;
		for (String descriptionLine : text)
		{
			xPos = DescriptionCategory.WIDTH / 2 - minecraft.fontRendererObj.getStringWidth(descriptionLine) / 2;
			minecraft.fontRendererObj.drawString(descriptionLine, xPos, yPos, ColorHelper.DEFAULT_TEXT_COLOR);
			yPos += minecraft.fontRendererObj.FONT_HEIGHT;
		}

		if (referenced != null && referenced.size() > 0)
		{
			final String referencesString = LangHelper.translate("nei.references");
			xPos = (recipeWidth - minecraft.fontRendererObj.getStringWidth(referencesString)) / 2;
			yPos = DescriptionCategory.HEIGHT - GuiHelper.STANDARD_SLOT_WIDTH - fontRenderer.FONT_HEIGHT;

			minecraft.fontRendererObj.drawString(referencesString, xPos, yPos, ColorHelper.DEFAULT_TEXT_COLOR);

			int relatedWidth = referenced.size() * GuiHelper.STANDARD_SLOT_WIDTH;
			xPos = (recipeWidth - relatedWidth) / 2;
			yPos += minecraft.fontRendererObj.FONT_HEIGHT;

			GlStateManager.color(1f, 1f, 1f, 1);
			for (ItemStack ignored : referenced)
			{
				slotDrawable.draw(minecraft, xPos, yPos);
				xPos += GuiHelper.STANDARD_SLOT_WIDTH;
			}
		}
	}
}
