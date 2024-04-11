package squeek.veganoption.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static squeek.veganoption.integration.jei.VeganOptionPlugin.CRAFTING_TABLE_GUI;

class CompostingCategory implements IRecipeCategory<CompostingCategory.FakeCompostingRecipe>
{
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawable arrowAndOutputSlot;

	CompostingCategory(IGuiHelper guiHelper)
	{
		background = guiHelper.createBlankDrawable(120, 34);
		icon = guiHelper.createDrawableItemStack(new ItemStack(Composting.composterItem.get()));
		slot = guiHelper.getSlotDrawable();
		arrowAndOutputSlot = guiHelper.createDrawable(CRAFTING_TABLE_GUI, 90, 30, 55, 26);
	}

	@Nonnull
	@Override
	public RecipeType<FakeCompostingRecipe> getRecipeType()
	{
		return VeganOptionPlugin.COMPOSTING;
	}

	@Nonnull
	@Override
	public Component getTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.composting"));
	}

	@Nonnull
	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull FakeCompostingRecipe recipe, @Nonnull IFocusGroup focuses)
	{
		// green
		List<ItemStack> greenInputs = CompostRegistry.getGreens().stream().map(ItemStack::new).toList();
		IRecipeSlotBuilder green1 = builder
			.addSlot(RecipeIngredientRole.INPUT, 2, 9)
			.addTooltipCallback(ColorDefinition.GREEN);
		if (recipe.numGreens() >= 1)
			green1.addIngredients(Ingredient.of(greenInputs.stream()));
		IRecipeSlotBuilder green2 = builder
			.addSlot(RecipeIngredientRole.INPUT, 20, 9)
			.addTooltipCallback(ColorDefinition.GREEN);
		if (recipe.numGreens() == 2)
		{
			Stream<ItemStack> shuffledGreens = MiscHelper.newShuffledList(greenInputs).stream();
			green2.addIngredients(Ingredient.of(shuffledGreens));
		}

		// brown
		IRecipeSlotBuilder brown = builder
			.addSlot(RecipeIngredientRole.INPUT, 38, 9)
			.addTooltipCallback(ColorDefinition.BROWN);
		if (recipe.numBrowns() >= 1)
			brown.addIngredients(Ingredient.of(CompostRegistry.getBrowns().stream().map(ItemStack::new)));

		// output
		builder
			.addSlot(RecipeIngredientRole.OUTPUT, 96, 9)
			.addItemStack(recipe.out());
	}

	@Override
	public void draw(@Nonnull FakeCompostingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		arrowAndOutputSlot.draw(guiGraphics, getWidth() - arrowAndOutputSlot.getWidth() - 3, getHeight() / 2 - arrowAndOutputSlot.getHeight() / 2);

		RenderSystem.setShaderColor(ColorDefinition.GREEN.r, ColorDefinition.GREEN.g, ColorDefinition.GREEN.b, 1f);
		slot.draw(guiGraphics, 1, 8);
		slot.draw(guiGraphics, 19, 8);

		RenderSystem.setShaderColor(ColorDefinition.BROWN.r, ColorDefinition.BROWN.g, ColorDefinition.BROWN.b, 1f);
		slot.draw(guiGraphics, 37, 8);
		
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

	record FakeCompostingRecipe(ItemStack out, int numGreens, int numBrowns)
	{
	}

	private enum ColorDefinition implements IRecipeSlotTooltipCallback
	{
		GREEN(.75f, 1f, .75f, ChatFormatting.GREEN),
		BROWN(.85f, .75f, .5f, ChatFormatting.GOLD);

		final float r, g, b;
		final ChatFormatting tooltipColor;

		ColorDefinition(float r, float g, float b, ChatFormatting tooltipColor)
		{
			this.r = r;
			this.g = g;
			this.b = b;
			this.tooltipColor = tooltipColor;
		}

		@Override
		public void onTooltip(@Nonnull IRecipeSlotView recipeSlotView, List<Component> tooltip)
		{
			Component name = tooltip.get(0);
			if (name != null)
			{
				name.getSiblings().set(0, name.getSiblings().get(0).copy().withStyle(tooltipColor));
				tooltip.set(0, name);
			}
		}
	}
}
