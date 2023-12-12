package squeek.veganoption.helpers;

import net.minecraft.util.RandomSource;

import java.util.Random;

public class RandomHelper
{
	public static final Random random = new Random();
	public static final RandomSource randomSource = RandomSource.create();

	public static int getRandomIntFromRange(int min, int max)
	{
		return getRandomIntFromRange(random, min, max);
	}

	public static int getRandomIntFromRange(Random random, int min, int max)
	{
		return min + (max > min ? random.nextInt(1 + max - min) : 0);
	}
}