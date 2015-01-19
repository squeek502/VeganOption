package squeek.veganoption.integration.bop;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.integration.IntegratorBase;

public class BiomesOPlenty extends IntegratorBase
{
	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.sunflowerSeedOreDict, new ItemStack(getItem("food"), 1, 3));
	}
}
