package squeek.veganoption.integration.tic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.content.modules.MobHeads;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.integration.IntegratorBase;
import cpw.mods.fml.common.registry.GameRegistry;

// TODO: Clay bucket support for VO fluids?
public class IguanaTweaksTConstruct extends IntegratorBase
{
	public static final int META_ENDERMAN = 0;
	public static final int META_PIGZOMBIE = 1;
	public static final int META_BLAZE = 2;
	public static final int META_BLIZZ = 3;

	@Override
	public void recipes()
	{
		Item skullItem = getItem("skullItem");
		if (skullItem != null)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(skullItem, 1, META_ENDERMAN),
					"bbb",
					"mhm",
					"bbb",
					'b', "dyeBlack",
					'm', "dyeMagenta",
					'h', MobHeads.mobHeadBlank
					));
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(skullItem, 1, META_PIGZOMBIE),
					"gpp",
					"ghp",
					"ggp",
					'g', "dyeGreen",
					'p', "dyePink",
					'h', MobHeads.mobHeadBlank
					));
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(skullItem, 1, META_BLAZE),
					"ooo",
					"oho",
					"ooo",
					'o', "dyeOrange",
					'h', MobHeads.mobHeadBlank
					));

			if (IntegrationHandler.integrationExists(IntegrationHandler.MODID_THERMAL_EXPANSION))
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(
						new ItemStack(skullItem, 1, META_BLIZZ),
						"ooo",
						"oho",
						"ooo",
						'o', "dyeLightBlue",
						'h', MobHeads.mobHeadBlank
						));
			}
		}
	}
}
