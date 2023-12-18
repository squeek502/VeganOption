package squeek.veganoption.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TemperatureHelper
{
	public static final float CELSIUS_TO_FAHRENHEIT_MULT = 9f / 5f;
	public static final float CELSIUS_TO_FARENHEIT_OFFSET = 32;
	public static final float FAHRENHEIT_TO_CELSIUS_MULT = 5f / 9f;
	public static final float FARENHEIT_TO_CELSIUS_OFFSET = -32;

	public static final float FLOAT_TEMP_TO_CELSIUS = 20f;

	public static float getBiomeTemperature(Level level, BlockPos pos)
	{
		return level.getBiome(pos).value().getBaseTemperature() * FLOAT_TEMP_TO_CELSIUS;
	}

	public static float celsiusToFahrenheit(float celsius)
	{
		return (CELSIUS_TO_FAHRENHEIT_MULT * celsius) + CELSIUS_TO_FARENHEIT_OFFSET;
	}

	public static float fahrenheitToCelsius(float fahrenheit)
	{
		return FAHRENHEIT_TO_CELSIUS_MULT * (fahrenheit + FARENHEIT_TO_CELSIUS_OFFSET);
	}
}
