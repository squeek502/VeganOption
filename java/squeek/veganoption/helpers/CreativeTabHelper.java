package squeek.veganoption.helpers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
		protected ResourceLocation texture;
		public ItemCreativeTabIconProxy(String textureName)
		{
			super();
			this.texture = new ResourceLocation(textureName);
		}
	}

	public static CreativeTabs createTab(final String name, String textureName)
	{
		creativeTabIconItemMap.put(name, new ItemCreativeTabIconProxy(textureName));
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
