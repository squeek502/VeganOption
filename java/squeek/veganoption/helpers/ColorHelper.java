package squeek.veganoption.helpers;

public class ColorHelper
{
	public static final int DEFAULT_TEXT_COLOR = 0x404040;
	public static final int DEFAULT_LIGHT_TEXT_COLOR = 0xbfbfbf;

	public static float[] toNormalizedRGB(int color)
	{
		return toNormalizedRGBA(color | 255 << 24);
	}

	public static float[] toNormalizedRGBA(int color)
	{
		int[] rgba = toRGBA(color);

		return new float[]{rgba[0] / 255f, rgba[1] / 255f, rgba[2] / 255f, rgba[3] / 255f};
	}

	public static int[] toRGBA(int color)
	{
		int alpha = color >> 24 & 255;
		int red = color >> 16 & 255;
		int green = color >> 8 & 255;
		int blue = color & 255;

		return new int[]{red, green, blue, alpha};
	}

	public static int fromRGBA(int r, int g, int b, int a)
	{
		return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
	}

	public static int fromRGB(int r, int g, int b)
	{
		return fromRGBA(r, g, b, 255);
	}

	public static int fromNormalizedRGBA(float r, float g, float b, float a)
	{
		return fromRGBA((int) r * 255, (int) g * 255, (int) b * 255, (int) a * 255);
	}

	public static int fromNormalizedRGB(float r, float g, float b)
	{
		return fromNormalizedRGBA(r, g, b, 1f);
	}

	public static int blendBetweenColors(double val, int minColor, int maxColor)
	{
		return blendBetweenColors(val, minColor, maxColor, 0d, 1d);
	}

	public static int blendBetweenColors(double val, int minColor, int maxColor, double min, double max)
	{
		if (min == max)
			return maxColor;

		double range = max - min;
		double ratioOfMax = (max - val) / range;
		double ratioOfMin = (val - min) / range;

		int[] minColorRGBA = toRGBA(minColor);
		int[] maxColorRGBA = toRGBA(maxColor);
		int[] color = new int[]{
			(int) (maxColorRGBA[0] * ratioOfMin + minColorRGBA[0] * ratioOfMax),
			(int) (maxColorRGBA[1] * ratioOfMin + minColorRGBA[1] * ratioOfMax),
			(int) (maxColorRGBA[2] * ratioOfMin + minColorRGBA[2] * ratioOfMax),
			(int) (maxColorRGBA[3] * ratioOfMin + minColorRGBA[3] * ratioOfMax)
		};

		return fromRGBA(color[0], color[1], color[2], color[3]);
	}
}