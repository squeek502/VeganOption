package squeek.veganoption.helpers;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabHelper
{
	public static final Map<String, Item> creativeTabIconItemMap = new HashMap<String, Item>();

	public static class ItemCreativeTabIconProxy extends Item
	{
		public ItemCreativeTabIconProxy()
		{
			super();
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void onTextureStichedPre(TextureStitchEvent.Pre event)
		{
			if (event.map.getTextureType() == getSpriteNumber())
				itemIcon = event.map.registerIcon(getIconString());
		}
	}

	public static CreativeTabs createTab(final String name, String textureName)
	{
		creativeTabIconItemMap.put(name, new ItemCreativeTabIconProxy().setTextureName(textureName));
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
