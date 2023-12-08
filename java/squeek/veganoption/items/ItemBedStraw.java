package squeek.veganoption.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.helpers.LangHelper;

import java.util.List;

public class ItemBedStraw extends BedItem
{
	public ItemBedStraw(BedBlock bed)
	{
		super(bed, new Item.Properties().stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced)
	{
		tooltip.add(LangHelper.tooltip("straw_bed"));
		super.appendHoverText(stack, level, tooltip, advanced);
	}
}
