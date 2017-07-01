package squeek.veganoption.integration.jei.description;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class DescriptionWrapper extends BlankRecipeWrapper
{
	@Nonnull
	public final ItemStack itemStack;
	@Nonnull
	public final List<ItemStack> related;
	@Nonnull
	public final List<ItemStack> referenced;
	@Nonnull
	public final List<String> text;

	public DescriptionWrapper(@Nonnull ItemStack itemStack, @Nonnull List<ItemStack> related, @Nonnull List<ItemStack> referenced, @Nonnull List<String> text)
	{
		this.itemStack = itemStack;
		this.related = related;
		this.referenced = referenced;
		this.text = text;
	}

	protected abstract String getRelatedTitle();

	abstract boolean isMainSlotInput();

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		FontRenderer fontRenderer = minecraft.fontRenderer;
		IDrawable slotDrawable = VOPlugin.jeiHelpers.getGuiHelper().getSlotDrawable();
		int xPos = (recipeWidth - slotDrawable.getWidth()) / 2;
		int yPos = 0;
		slotDrawable.draw(minecraft, xPos, yPos);
		yPos += slotDrawable.getHeight() + DescriptionCategory.PADDING;

		if (related.size() > 0)
		{
			String relatedTitle = getRelatedTitle();
			xPos = DescriptionCategory.WIDTH / 2 - fontRenderer.getStringWidth(relatedTitle) / 2;
			fontRenderer.drawString(relatedTitle, xPos, yPos, ColorHelper.DEFAULT_TEXT_COLOR);

			int relatedWidth = related.size() * GuiHelper.STANDARD_SLOT_WIDTH;
			xPos = (recipeWidth - relatedWidth) / 2;
			yPos += fontRenderer.FONT_HEIGHT;

			GlStateManager.color(1f, 1f, 1f, 1);
			for (ItemStack ignored : related)
			{
				slotDrawable.draw(minecraft, xPos, yPos);
				xPos += GuiHelper.STANDARD_SLOT_WIDTH;
			}

			yPos += fontRenderer.FONT_HEIGHT * DescriptionMaker.DESC_DISPLACEMENT_RELATED - 4;
		}

		for (String descriptionLine : text)
		{
			xPos = DescriptionCategory.WIDTH / 2 - fontRenderer.getStringWidth(descriptionLine) / 2;
			fontRenderer.drawString(descriptionLine, xPos, yPos, ColorHelper.DEFAULT_TEXT_COLOR);
			yPos += fontRenderer.FONT_HEIGHT;
		}

		if (referenced.size() > 0)
		{
			final String referencesString = LangHelper.translate("jei.references");
			xPos = (recipeWidth - fontRenderer.getStringWidth(referencesString)) / 2;
			yPos = DescriptionCategory.HEIGHT - GuiHelper.STANDARD_SLOT_WIDTH - fontRenderer.FONT_HEIGHT;

			fontRenderer.drawString(referencesString, xPos, yPos, ColorHelper.DEFAULT_TEXT_COLOR);

			int relatedWidth = referenced.size() * GuiHelper.STANDARD_SLOT_WIDTH;
			xPos = (recipeWidth - relatedWidth) / 2;
			yPos += fontRenderer.FONT_HEIGHT;

			GlStateManager.color(1f, 1f, 1f, 1);
			for (ItemStack ignored : referenced)
			{
				slotDrawable.draw(minecraft, xPos, yPos);
				xPos += GuiHelper.STANDARD_SLOT_WIDTH;
			}
		}
	}
}
