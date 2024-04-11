package squeek.veganoption.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.helpers.CreativeTabHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JeiPlugin
public class VeganOptionPlugin implements IModPlugin
{
	static final ResourceLocation CRAFTING_TABLE_GUI = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");;
	static final RecipeType<CompostingCategory.FakeCompostingRecipe> COMPOSTING = RecipeType.create(ModInfo.MODID_LOWER, "composting", CompostingCategory.FakeCompostingRecipe.class);
	static final RecipeType<PistonCraftingRecipe> PISTON_CRAFTING = RecipeType.create(ModInfo.MODID_LOWER, "piston_crafting", PistonCraftingRecipe.class);
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.MODID_LOWER, "jei_plugin");

	public VeganOptionPlugin()
	{
		// no op
	}

	@Nonnull
	@Override
	public ResourceLocation getPluginUid()
	{
		return ID;
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(
			VanillaTypes.ITEM_STACK,
			CreativeTabHelper.FAKE_ITEMS
				.getEntries()
				.stream()
				.map(h -> new ItemStack(h.get()))
				.collect(Collectors.toList()));

		jeiRuntime.getRecipeManager().hideRecipes(RecipeTypes.CRAFTING, Modifiers.recipes.oldRecipeHolders);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(new CompostingCategory(guiHelper));
		registration.addRecipeCategories(new PistonCraftingCategory(guiHelper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		registration.addRecipes(RecipeTypes.CRAFTING, Modifiers.recipes.newRecipeHolders);

		registration.addRecipes(COMPOSTING, List.of(
			new CompostingCategory.FakeCompostingRecipe(new ItemStack(Composting.compostItem.get()), 2, 1),
			new CompostingCategory.FakeCompostingRecipe(new ItemStack(Composting.rottenPlants.get()), 1, 0)));

		registration.addRecipes(PISTON_CRAFTING, PistonCraftingRegistry.getRecipes());

		DescriptionRegistry.registerAllDescriptions();

		Map<ItemStack, List<Component>> allReferences = new HashMap<>();
		for (ItemStack item : DescriptionRegistry.itemsWithUsageDescriptions)
		{
			List<Component> references = new ArrayList<>();
			registration.addItemStackInfo(item, getWikiComponentsForSection(
				LangHelper.translateRaw(DescriptionRegistry.getUsageKey(item), LangHelper.wrapInFormat(item.getDescriptionId(), ChatFormatting.BLACK)),
				item,
				"jei.usage",
				"jei.byproducts",
				RelationshipRegistry.getChildren(item.getItem()).stream().map(ItemStack::new).toList(),
				references));
			allReferences.put(item, references);
		}

		for (ItemStack item : DescriptionRegistry.itemsWithCraftingDescriptions)
		{
			List<Component> references = allReferences.containsKey(item) ? allReferences.get(item) : new ArrayList<>();
			registration.addItemStackInfo(item, getWikiComponentsForSection(
				LangHelper.translateRaw(DescriptionRegistry.getCraftingKey(item), LangHelper.wrapInFormat(item.getDescriptionId(), ChatFormatting.BLACK)),
				item,
				"jei.crafting",
				"jei.byproduct.of",
				RelationshipRegistry.getParents(item.getItem()).stream().map(ItemStack::new).toList(),
				references));
			allReferences.put(item, references);
		}
		for (Map.Entry<ItemStack, List<Component>> entry : allReferences.entrySet())
		{
			if (entry.getValue().isEmpty())
				continue;
			entry.getValue().add(0, Component.translatable(LangHelper.prependModId("jei.references")).withStyle(ChatFormatting.BOLD));
			registration.addItemStackInfo(entry.getKey(), entry.getValue().toArray(new Component[0]));
		}
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(new ItemStack(Composting.composter.get()), COMPOSTING);
		registration.addRecipeCatalyst(new ItemStack(Items.PISTON), PISTON_CRAFTING);
	}

	private boolean isReferenceNew(ItemStack topic, @Nullable ItemStack referencedItem, Component reference, List<Component> referenced, List<ItemStack> related)
	{
		if (referencedItem != null)
			return referencedItem.getItem() != topic.getItem() && MiscHelper.getMatchingItemFromStackList(related, referencedItem.getItem()) == null;
		return referenced.stream().noneMatch(c -> c.getString().equals(reference.getString()));
	}

	private Component[] getWikiComponentsForSection(String translatedText, ItemStack item, String mainSectionKey, String relatedSectionKey, List<ItemStack> relatedItems, List<Component> references)
	{
		List<Component> components = new ArrayList<>();
		components.add(Component.translatable(LangHelper.prependModId(mainSectionKey)).withStyle(ChatFormatting.BOLD));
		String usageText = DescriptionRegistry.processWikiText(
			translatedText,
			item,
			(referencedItemStack, referenceMatcher, referencedBuffer) -> {
				Component reference = Component.translatable(LangHelper.prependModId("jei.references.list"), LangHelper.translateRaw(referencedItemStack.getDescriptionId()));
				if (isReferenceNew(item, referencedItemStack, reference, references, relatedItems))
					references.add(reference);
				referenceMatcher.appendReplacement(referencedBuffer, LangHelper.wrapInFormat(referencedItemStack.getDescriptionId(), ChatFormatting.DARK_BLUE));
			},
			(referencedFluid, referenceMatcher, referencedBuffer) -> {
				Component reference = Component.translatable(LangHelper.prependModId("jei.references.list"), LangHelper.translateRaw(referencedFluid.getFluidType().getDescriptionId()));
				if (isReferenceNew(item, null, reference, references, relatedItems))
					references.add(reference);
				referenceMatcher.appendReplacement(referencedBuffer, LangHelper.wrapInFormat(referencedFluid.getFluidType().getDescriptionId(), ChatFormatting.DARK_BLUE));
			});
		components.add(Component.literal(usageText));
		if (!relatedItems.isEmpty())
		{
			// empty component does not add a newline, and \n adds 2.
			components.add(Component.literal(" "));
			components.add(Component.translatable(LangHelper.prependModId(relatedSectionKey)).withStyle(ChatFormatting.BOLD));
			components.addAll(relatedItems.stream().map(i -> Component.literal(LangHelper.translate("jei.references.list", LangHelper.translateRaw(i.getDescriptionId())))).toList());
		}

		return components.toArray(new Component[0]);
	}
}
