package squeek.veganoption.integration.tic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.modules.Bioplastic;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.integration.IntegratorBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TConstruct extends IntegratorBase
{
	public static final String ITEMNAME_JERKY = "jerky";
	public static final String ITEMNAME_GOLDENHEAD = "goldHead";
	public static final String ITEMNAME_DIAMONDAPPLE = "diamondApple";
	public static final String ITEMNAME_STRANGEFOOD = "strangeFood";

	@Override
	public void init()
	{
		super.init();

		CompostRegistry.blacklist(new FoodSpecifier()
		{
			private final Set<String> itemNameBlacklist = new HashSet<String>(
				Arrays.asList(
					fullItemName(ITEMNAME_JERKY),
					fullItemName(ITEMNAME_GOLDENHEAD),
					fullItemName(ITEMNAME_DIAMONDAPPLE),
					fullItemName(ITEMNAME_STRANGEFOOD)
				)
			);

			@Override
			public boolean matches(ItemStack itemStack)
			{
				// meat and diamonds are bad for composting
				ResourceLocation itemRL = Item.REGISTRY.getNameForObject(itemStack.getItem());

				if (itemRL == null)
					return false;

				String itemName = itemRL.toString();

				return itemNameBlacklist.contains(itemName);
			}
		});
	}
}
