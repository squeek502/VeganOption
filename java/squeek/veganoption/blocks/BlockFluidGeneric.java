package squeek.veganoption.blocks;

import java.lang.reflect.Method;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.ModInfo;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluidGeneric extends BlockFluidClassic
{
	public String iconName;
	@SideOnly(Side.CLIENT)
	public IIcon stillIcon;
	@SideOnly(Side.CLIENT)
	public IIcon flowIcon;

	public BlockFluidGeneric(Fluid fluid, Material material, String iconName)
	{
		super(fluid, material);
		this.iconName = iconName;

		MinecraftForge.EVENT_BUS.register(this);
	}

	protected static Method getMovingObjectPositionFromPlayer = ReflectionHelper.findMethod(Item.class, null, new String[] {"getMovingObjectPositionFromPlayer", "func_77621_a", "a"}, World.class, EntityPlayer.class, boolean.class);

	// all this just for picking up fluids generically
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.isCanceled() || event.useItem != Event.Result.DEFAULT)
			return;

		if (event.entityPlayer.getHeldItem() == null)
			return;

		if (!FluidContainerRegistry.isEmptyContainer(event.entityPlayer.getHeldItem()))
			return;

		ItemStack emptyContainer = event.entityPlayer.getHeldItem();

		MovingObjectPosition movingObjectPosition = null;
		try
		{
			movingObjectPosition = (MovingObjectPosition) getMovingObjectPositionFromPlayer.invoke(emptyContainer.getItem(), event.world, event.entityPlayer, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (movingObjectPosition == null)
			return;

		if (movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
			return;

		int x = movingObjectPosition.blockX;
		int y = movingObjectPosition.blockY;
		int z = movingObjectPosition.blockZ;

		if (event.world.getBlock(x, y, z) != this)
			return;

		ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(new FluidStack(getFluid(), FluidContainerRegistry.BUCKET_VOLUME), emptyContainer);

		if (filledContainer == null)
			return;

		if (!event.world.canMineBlock(event.entityPlayer, x, y, z))
			return;

		if (!event.entityPlayer.canPlayerEdit(x, y, z, movingObjectPosition.sideHit, emptyContainer))
			return;

		if (!event.entityPlayer.capabilities.isCreativeMode)
			--emptyContainer.stackSize;

		if (emptyContainer.stackSize <= 0)
		{
			event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, filledContainer);
		}
		else if (!event.entityPlayer.inventory.addItemStackToInventory(filledContainer))
		{
			event.entityPlayer.dropPlayerItemWithRandomChoice(filledContainer, false);
		}

		event.world.setBlockToAir(x, y, z);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return (side == 0 || side == 1) ? stillIcon : flowIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		stillIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + iconName + "_still");
		flowIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + iconName + "_flow");
		getFluid().setIcons(stillIcon, flowIcon);
	}
}
