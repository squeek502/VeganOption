package squeek.veganoption.content.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;

import javax.annotation.Nonnull;

public class Burlap implements IContentModule
{
	public static Item burlap;
	public static ItemArmor burlapHelmet;
	public static ItemArmor burlapChestplate;
	public static ItemArmor burlapLeggings;
	public static ItemArmor burlapBoots;

	@Override
	public void create()
	{
		burlap = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".burlap")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "burlap");
		GameRegistry.register(burlap);

		burlapHelmet = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD)
				.setUnlocalizedName(ModInfo.MODID + ".helmetBurlap")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "helmetBurlap");
		GameRegistry.register(burlapHelmet);

		burlapChestplate = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST)
				.setUnlocalizedName(ModInfo.MODID + ".chestplateBurlap")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "chestplateBurlap");
		GameRegistry.register(burlapChestplate);

		burlapLeggings = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.LEGS)
				.setUnlocalizedName(ModInfo.MODID + ".leggingsBurlap")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "leggingsBurlap");
		GameRegistry.register(burlapLeggings);

		burlapBoots = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET)
				.setUnlocalizedName(ModInfo.MODID + ".bootsBurlap")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "bootsBurlap");
		GameRegistry.register(burlapBoots);
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.leatherOreDict, new ItemStack(Items.LEATHER));
		OreDictionary.registerOre(ContentHelper.leatherOreDict, new ItemStack(burlap));
		OreDictionary.registerOre(ContentHelper.leatherBootsOreDict, new ItemStack(Items.LEATHER_BOOTS));
		OreDictionary.registerOre(ContentHelper.leatherBootsOreDict, new ItemStack(burlapBoots));
		OreDictionary.registerOre(ContentHelper.leatherLeggingsOreDict, new ItemStack(Items.LEATHER_LEGGINGS));
		OreDictionary.registerOre(ContentHelper.leatherLeggingsOreDict, new ItemStack(burlapLeggings));
		OreDictionary.registerOre(ContentHelper.leatherChestplateOreDict, new ItemStack(Items.LEATHER_CHESTPLATE));
		OreDictionary.registerOre(ContentHelper.leatherChestplateOreDict, new ItemStack(burlapChestplate));
		OreDictionary.registerOre(ContentHelper.leatherHelmetOreDict, new ItemStack(Items.LEATHER_HELMET));
		OreDictionary.registerOre(ContentHelper.leatherHelmetOreDict, new ItemStack(burlapHelmet));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.excludeOutput(new ItemStack(Items.LEATHER_HELMET));
		Modifiers.recipes.excludeOutput(new ItemStack(Items.LEATHER_CHESTPLATE));
		Modifiers.recipes.excludeOutput(new ItemStack(Items.LEATHER_LEGGINGS));
		Modifiers.recipes.excludeOutput(new ItemStack(Items.LEATHER_BOOTS));
		Modifiers.recipes.convertInput(new ItemStack(Items.LEATHER), ContentHelper.leatherOreDict);
		Modifiers.recipes.convertInput(new ItemStack(Items.LEATHER_BOOTS), ContentHelper.leatherBootsOreDict);
		Modifiers.recipes.convertInput(new ItemStack(Items.LEATHER_LEGGINGS), ContentHelper.leatherLeggingsOreDict);
		Modifiers.recipes.convertInput(new ItemStack(Items.LEATHER_CHESTPLATE), ContentHelper.leatherChestplateOreDict);
		Modifiers.recipes.convertInput(new ItemStack(Items.LEATHER_HELMET), ContentHelper.leatherHelmetOreDict);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(burlap), "~~", "~~", '~', ContentHelper.bastFibreOreDict));

		GameRegistry.addRecipe(new ItemStack(burlapHelmet), "XXX", "X X", 'X', new ItemStack(burlap));
		GameRegistry.addRecipe(new ItemStack(burlapChestplate), "X X", "XXX", "XXX", 'X', new ItemStack(burlap));
		GameRegistry.addRecipe(new ItemStack(burlapLeggings), "XXX", "X X", "X X", 'X', new ItemStack(burlap));
		GameRegistry.addRecipe(new ItemStack(burlapBoots), "X X", "X X", 'X', new ItemStack(burlap));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.STRING), "~~", '~', ContentHelper.bastFibreOreDict));
	}

	@Override
	public void finish()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePost()
	{
		// Gross duplicate of the leather armor color handler
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor()
		{
			public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex)
			{
				return tintIndex > 0 ? -1 : ((ItemArmor)stack.getItem()).getColor(stack);
			}
		}, burlapHelmet, burlapChestplate, burlapLeggings, burlapBoots);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(burlap);
		ContentHelper.registerTypicalItemModel(burlapHelmet);
		ContentHelper.registerTypicalItemModel(burlapChestplate);
		ContentHelper.registerTypicalItemModel(burlapLeggings);
		ContentHelper.registerTypicalItemModel(burlapBoots);
	}
}
