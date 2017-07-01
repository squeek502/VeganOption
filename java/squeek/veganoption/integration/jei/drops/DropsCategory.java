package squeek.veganoption.integration.jei.drops;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;

public class DropsCategory extends BlankRecipeCategory<DropsWrapper>
{
	private final String localizedName;

	private final IDrawable background;
	private final IDrawable arrow;

	private static final int craftInputSlot = 0;
	private static final int craftOutputSlotMin = 1;
	private static final int craftOutputSlotMax = 2;

	public static final int width = 75;
	public static final int height = 40;

	public DropsCategory(IGuiHelper guiHelper)
	{
		ResourceLocation location = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
		arrow = guiHelper.createDrawable(location, 90, 35, 22, 15);
		background = guiHelper.createBlankDrawable(width, height);
		localizedName = LangHelper.translate("jei.drops");
	}

	@Override
	@Nonnull
	public String getUid()
	{
		return VOPlugin.VORecipeCategoryUid.DROPS;
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

		arrow.draw(minecraft, width / 2 - arrow.getWidth() / 2, 17);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull DropsWrapper recipeWrapper, @Nonnull IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(craftInputSlot, true, 0, 13);
		guiItemStacks.init(craftOutputSlotMin, false, 57, 13);
		guiItemStacks.init(craftOutputSlotMax, false, 87, 13);

		ItemStack minItemStack = recipeWrapper.drop.drop.itemStack.copy();
		minItemStack.setCount(recipeWrapper.drop.drop.dropsMin);

		guiItemStacks.set(ingredients);
		guiItemStacks.set(craftOutputSlotMin, minItemStack);

		if (recipeWrapper.drop.drop.dropsMin != recipeWrapper.drop.drop.dropsMax)
		{
			ItemStack maxItemStack = recipeWrapper.drop.drop.itemStack.copy();
			maxItemStack.setCount(recipeWrapper.drop.drop.dropsMax);
			guiItemStacks.set(craftOutputSlotMax, maxItemStack);
		}
	}
}
