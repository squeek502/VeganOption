package squeek.veganoption.content.modules;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.helpers.CreativeTabHelper;

public class CreativeTabProxy implements IContentModule
{
	public static Item proxyItem;

	@Override
	public void create()
	{
		proxyItem = new Item()
			.setUnlocalizedName(ModInfo.MODID + ".creative_tab")
			.setRegistryName(ModInfo.MODID_LOWER, "creative_tab");
		GameRegistry.register(proxyItem);
		VeganOption.creativeTab = CreativeTabHelper.createTab(ModInfo.MODID, proxyItem);
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
	}

	@Override
	public void finish()
	{
	}

	@Override
	public void clientSidePost()
	{
	}

	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(proxyItem);
	}
}
