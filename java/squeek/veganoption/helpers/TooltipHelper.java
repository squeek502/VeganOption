package squeek.veganoption.helpers;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class TooltipHelper
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new TooltipHelper());
	}

	protected static final Set<Item> itemsThatHaveTooltips = new HashSet<Item>();

	public static void registerItem(Item item)
	{
		itemsThatHaveTooltips.add(item);
	}

	@SubscribeEvent
	public void getItemTooltip(ItemTooltipEvent event)
	{
		if (event.getItemStack() == null || event.getItemStack().getItem() == null)
			return;

		if (!itemsThatHaveTooltips.contains(event.getItemStack().getItem()))
			return;

		String unlocalizedTooltip = event.getItemStack().getItem().getUnlocalizedName() + ".tooltip";

		if (!LangHelper.existsRaw(unlocalizedTooltip))
			return;

		event.getToolTip().add(LangHelper.translateRaw(unlocalizedTooltip));
	}
}
