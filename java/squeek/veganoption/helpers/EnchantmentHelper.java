package squeek.veganoption.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;

import static net.minecraft.enchantment.EnchantmentHelper.getMaxEnchantmentLevel;

public class EnchantmentHelper
{
	public static int getFortuneModifier(EntityLivingBase elb)
	{
		return getMaxEnchantmentLevel(Enchantments.FORTUNE, elb);
	}

	public static boolean getSilkTouchModifier(EntityLivingBase elb)
	{
		return getMaxEnchantmentLevel(Enchantments.SILK_TOUCH, elb) > 0;
	}
}
