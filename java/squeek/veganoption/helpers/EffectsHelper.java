package squeek.veganoption.helpers;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Helper class for handling game effects, i.e., sounds and particles.
 */
public class EffectsHelper
{
	public static void doEntityBreakParticles(Level level, double x, double y, double z, Item breakFXItem)
	{
		doEntityBreakParticles(level, x, y, z, breakFXItem, 8);
	}

	public static void doEntityBreakParticles(Level level, double x, double y, double z, Item breakFXItem, double iterations)
	{
		ParticleOptions particles = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(breakFXItem));
		for (int i = 0; i < iterations; ++i) {
			level.addParticle(particles, x, y, z, ((double) RandomHelper.random.nextFloat() - 0.5D) * 0.08D, ((double) RandomHelper.random.nextFloat() - 0.5D) * 0.08D, ((double) RandomHelper.random.nextFloat() - 0.5D) * 0.08D);
		}
	}
}
