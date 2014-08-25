package squeek.veganoption.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;

public class ItemFrozenBubble extends Item
{
	@Override
	public String getPotionEffect(ItemStack p_150896_1_)
	{
		return PotionHelper.field_151423_m;
	}
}
