package squeek.veganoption.integration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.content.IContentModule;

public abstract class IntegratorBase extends IntegrationBase implements IContentModule
{
	// initialized by IntegrationHandler
	public String modID;

	public IntegratorBase()
	{
	}

	public IntegratorBase(String modId)
	{
		this.modID = modId;
	}

	public void preInit()
	{
		create();
		oredict();
	}

	public void init()
	{
		recipes();
	}

	public void postInit()
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			clientSide();
		finish();
	}

	@Override
	public void create()
	{
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

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSide()
	{
	}

	public String fullItemName(String itemName)
	{
		if (itemName.contains(":"))
			return itemName;
		else
			return modID + ":" + itemName;
	}

	public Item getItem(String itemName)
	{
		return Item.REGISTRY.getObject(new ResourceLocation(modID, itemName));
	}

	public Block getBlock(String blockName)
	{
		return Block.REGISTRY.getObject(new ResourceLocation(modID, blockName));
	}
}
