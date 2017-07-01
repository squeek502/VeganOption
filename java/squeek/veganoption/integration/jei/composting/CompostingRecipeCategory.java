package squeek.veganoption.integration.jei.composting;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompostingRecipeCategory extends BlankRecipeCategory<CompostingRecipeWrapper>
{
	private final String localizedName;

	private final IDrawable background;
	private final IDrawable arrowAndOutputSlot;
	private final IDrawable slot;
	private final IDrawableAnimated arrowAnimated;

	private static final int craftOutputSlot = 0;

	public static final int width = 116;
	public static final int height = 40;

	public CompostingRecipeCategory(IGuiHelper guiHelper)
	{
		ResourceLocation location = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
		arrowAndOutputSlot = guiHelper.createDrawable(location, 90, 30, 55, 26);
		slot = guiHelper.getSlotDrawable();
		background = guiHelper.createBlankDrawable(width, height);
		localizedName = LangHelper.translate("jei.composting");

		ResourceLocation animatedArrowLocation = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(animatedArrowLocation, 176, 14, 24, 17);
		arrowAnimated = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	@Nonnull
	public String getUid()
	{
		return VOPlugin.VORecipeCategoryUid.COMPOSTING;
	}

	@Override
	@Nonnull
	public String getTitle()
	{
		return localizedName;
	}

	@Nonnull
	@Override
	public String getModName()
	{
		return ModInfo.MODID;
	}

	@Override
	@Nonnull
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft)
	{
		super.drawExtras(minecraft);

		arrowAndOutputSlot.draw(minecraft, width - arrowAndOutputSlot.getWidth(), height / 2 - arrowAndOutputSlot.getHeight() / 2);

		// green slots
		GlStateManager.color(.75f, 1f, .75f, 1);
		slot.draw(minecraft, 0, 11);
		slot.draw(minecraft, 18, 11);
		// brown slot
		GlStateManager.color(1f, 1f, .75f, 1);
		slot.draw(minecraft, 36, 11);
		GlStateManager.color(1f, 1f, 1f, 1);

		arrowAnimated.draw(minecraft, width - arrowAndOutputSlot.getWidth(), height / 2 - arrowAnimated.getHeight() / 2);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CompostingRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(craftOutputSlot, false, width - 9 - 13, height / 2 - 9);

		guiItemStacks.init(1, true, 0, 11);
		guiItemStacks.init(2, true, 18, 11);
		guiItemStacks.init(3, true, 36, 11);

		guiItemStacks.set(craftOutputSlot, ingredients.getOutputs(ItemStack.class).get(0));

		if (recipeLayout.getFocus().getValue() != null && recipeWrapper.numGreens > 1)
		{
			guiItemStacks.set(1, ingredients.getInputs(ItemStack.class).get(0));

			// workaround for both green slots showing the same item and not cycling when the focus is a green item:
			// remove the focused item from the second green input list
			List<ItemStack> alteredGreenStack = new ArrayList<ItemStack>(ingredients.getInputs(ItemStack.class).get(1));
			ItemStack focusStack = (ItemStack) recipeLayout.getFocus().getValue();
			ItemStack stackToRemove = VOPlugin.jeiItemStackHelper.getMatch(alteredGreenStack, focusStack);

			if (stackToRemove != null)
				alteredGreenStack.remove(stackToRemove);

			guiItemStacks.set(2, alteredGreenStack);

			guiItemStacks.set(3, ingredients.getInputs(ItemStack.class).get(2));
		}
		else
		{
			guiItemStacks.set(ingredients);
		}
	}
}
