package squeek.veganoption.integration.jei.description;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.GuiHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class DescriptionCategory extends BlankRecipeCategory<DescriptionWrapper>
{
	private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 130;
	public static final int Y_START = 22;
	public static final int PADDING = 4;
	public static final int MAX_LINES_PER_PAGE = (HEIGHT - Y_START) / fontRenderer.FONT_HEIGHT - 1;
	private final IDrawable background;
	private final IGuiHelper guiHelper;
	private final String localizedName;
	private final String uid;

	public DescriptionCategory(IGuiHelper guiHelper, String uid, String localizedName)
	{
		background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
		this.guiHelper = guiHelper;
		this.localizedName = localizedName;
		this.uid = uid;
	}

	@Override
	@Nonnull
	public String getUid()
	{
		return uid;
	}

	@Override
	@Nonnull
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	@Nonnull
	public String getTitle()
	{
		return localizedName;
	}

	@Override
	public String getModName()
	{
		return ModInfo.MODID;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull DescriptionWrapper recipeWrapper, @Nonnull IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		boolean isMainSlotAnInput = recipeWrapper.isMainSlotInput();

		int xPos = (WIDTH - GuiHelper.STANDARD_SLOT_WIDTH) / 2;
		int yPos = 0;
		int slotIndex = 0;
		guiItemStacks.init(slotIndex, isMainSlotAnInput, xPos, yPos);
		List<ItemStack> mainStack = isMainSlotAnInput ? ingredients.getInputs(ItemStack.class).get(0) : ingredients.getOutputs(ItemStack.class).get(0);
		guiItemStacks.set(slotIndex, mainStack);

		boolean isSlotInput = !isMainSlotAnInput;

		yPos += GuiHelper.STANDARD_SLOT_WIDTH + fontRenderer.FONT_HEIGHT + PADDING;
		xPos = (WIDTH - recipeWrapper.related.size() * GuiHelper.STANDARD_SLOT_WIDTH) / 2;
		for (ItemStack relatedStack : recipeWrapper.related)
		{
			slotIndex++;
			guiItemStacks.init(slotIndex, isSlotInput, xPos, yPos);
			guiItemStacks.set(slotIndex, relatedStack);
			xPos += GuiHelper.STANDARD_SLOT_WIDTH;
		}

		yPos = HEIGHT - GuiHelper.STANDARD_SLOT_WIDTH;
		xPos = (WIDTH - recipeWrapper.referenced.size() * GuiHelper.STANDARD_SLOT_WIDTH) / 2;
		for (ItemStack referencedStack : recipeWrapper.referenced)
		{
			slotIndex++;
			guiItemStacks.init(slotIndex, isSlotInput, xPos, yPos);
			guiItemStacks.set(slotIndex, referencedStack);
			xPos += GuiHelper.STANDARD_SLOT_WIDTH;
		}
	}
}
