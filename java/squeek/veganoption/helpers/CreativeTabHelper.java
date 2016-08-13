package squeek.veganoption.helpers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabHelper
{
	public static final Map<String, Item> creativeTabIconItemMap = new HashMap<String, Item>();

	public static CreativeTabs createTab(final String name, Item item)
	{
		creativeTabIconItemMap.put(name, item);
		return new CreativeTabs(name)
		{
			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem()
			{
				return creativeTabIconItemMap.get(name);
			}
		};
	}
}
