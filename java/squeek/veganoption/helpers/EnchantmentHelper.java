package squeek.veganoption.helpers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantments;

import static net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel;

public class EnchantmentHelper
{
	public static int getFortuneModifier(LivingEntity entity)
	{
		return getEnchantmentLevel(Enchantments.BLOCK_FORTUNE, entity);
	}

	public static boolean getSilkTouchModifier(LivingEntity entity)
	{
		return getEnchantmentLevel(Enchantments.SILK_TOUCH, entity) > 0;
	}
}
