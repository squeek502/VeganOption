package squeek.veganoption.helpers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class CreativeTabHelper
{
	public static final Map<String, Item> creativeTabIconItemMap = new HashMap<String, Item>();

	public static CreativeTabs createTab(final String name, Item item)
	{
		creativeTabIconItemMap.put(name, item);
		return new CreativeTabs(name)
		{
			@Nonnull
			@Override
			@SideOnly(Side.CLIENT)
			public ItemStack getTabIconItem()
			{
				return new ItemStack(creativeTabIconItemMap.get(name));
			}
		};
	}
}
