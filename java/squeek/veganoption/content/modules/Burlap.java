package squeek.veganoption.content.modules;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;

import java.util.function.Supplier;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID_LOWER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Burlap implements IContentModule
{
	public static Supplier<Item> burlap;
	public static DeferredHolder<Item, DyeableArmorItem> burlapHelmet;
	public static DeferredHolder<Item, DyeableArmorItem> burlapChestplate;
	public static DeferredHolder<Item, DyeableArmorItem> burlapLeggings;
	public static DeferredHolder<Item, DyeableArmorItem> burlapBoots;

	@Override
	public void create()
	{
		burlap = REGISTER_ITEMS.register("burlap", () -> new Item(new Item.Properties()));
		burlapBoots = REGISTER_ITEMS.register("burlap_boots", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties()) {
			@Override
			public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer)
			{
				return true;
			}
		});
		burlapLeggings = REGISTER_ITEMS.register("burlap_leggings", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties()));
		burlapChestplate = REGISTER_ITEMS.register("burlap_chestplate", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
		burlapHelmet = REGISTER_ITEMS.register("burlap_helmet", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.LEATHER).add(burlap.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_BOOTS).add(burlapBoots.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_LEGGINGS).add(burlapLeggings.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_CHESTPLATES).add(burlapChestplate.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_HELMETS).add(burlapHelmet.get());

		// For recipe replacements
		provider.tagW(ContentHelper.ItemTags.LEATHER_BOOTS)
			.add(burlapBoots.get())
			.add(Items.LEATHER_BOOTS);
		provider.tagW(ContentHelper.ItemTags.LEATHER_LEGGINGS)
			.add(burlapLeggings.get())
			.add(Items.LEATHER_LEGGINGS);
		provider.tagW(ContentHelper.ItemTags.LEATHER_CHESTPLATES)
			.add(burlapChestplate.get())
			.add(Items.LEATHER_CHESTPLATE);
		provider.tagW(ContentHelper.ItemTags.LEATHER_HELMETS)
			.add(burlapHelmet.get())
			.add(Items.LEATHER_HELMET);

		provider.tagW(ItemTags.FREEZE_IMMUNE_WEARABLES)
			.add(burlapBoots.get())
			.add(burlapLeggings.get())
			.add(burlapChestplate.get())
			.add(burlapHelmet.get());
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, burlap.get())
			.pattern("~~")
			.pattern("~~")
			.define('~', ContentHelper.ItemTags.FIBRES)
			.unlockedBy("has_jute", provider.hasW(Jute.juteFibre.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapBoots.get())
			.pattern("X X")
			.pattern("X X")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapLeggings.get())
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapChestplate.get())
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapHelmet.get())
			.pattern("XXX")
			.pattern("X X")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_BOOTS), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER_BOOTS));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_LEGGINGS), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER_LEGGINGS));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_CHESTPLATE), () ->  Ingredient.of(ContentHelper.ItemTags.LEATHER_CHESTPLATES));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_HELMET), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER_HELMETS));

		Modifiers.recipes.excludeOutput(Items.LEATHER_BOOTS);
		Modifiers.recipes.excludeOutput(Items.LEATHER_LEGGINGS);
		Modifiers.recipes.excludeOutput(Items.LEATHER_CHESTPLATE);
		Modifiers.recipes.excludeOutput(Items.LEATHER_HELMET);
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(burlap.get());
		provider.withExistingParent(burlapBoots.getId().getPath(), provider.mcLoc("leather_boots"));
		provider.withExistingParent(burlapChestplate.getId().getPath(), provider.mcLoc("leather_chestplate"));
		provider.withExistingParent(burlapLeggings.getId().getPath(), provider.mcLoc("leather_leggings"));
		provider.withExistingParent(burlapHelmet.getId().getPath(), provider.mcLoc("leather_helmet"));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerColorHandlers(RegisterColorHandlersEvent.Item event)
	{
		event.register(
			(stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack),
			burlapBoots.get(), burlapLeggings.get(), burlapChestplate.get(), burlapHelmet.get()
		);
	}
}
