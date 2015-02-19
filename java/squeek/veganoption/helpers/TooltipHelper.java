package squeek.veganoption.helpers;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
		if (event.itemStack == null || event.itemStack.getItem() == null)
			return;

		if (!itemsThatHaveTooltips.contains(event.itemStack.getItem()))
			return;

		String unlocalizedTooltip = event.itemStack.getItem().getUnlocalizedName() + ".tooltip";

		if (!LangHelper.existsRaw(unlocalizedTooltip))
			return;

		event.toolTip.add(LangHelper.translateRaw(unlocalizedTooltip));
	}
}
