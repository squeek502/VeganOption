package squeek.veganoption.helpers;

import java.lang.reflect.Method;
import squeek.veganoption.blocks.BlockFluidGeneric;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class FluidContainerHelper
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new FluidContainerHelper());
	}

	protected static Method getMovingObjectPositionFromPlayer = ReflectionHelper.findMethod(Item.class, null, new String[] {"getMovingObjectPositionFromPlayer", "func_77621_a", "a"}, World.class, EntityPlayer.class, boolean.class);

	// all this just for picking up generic fluids with a glass bottle
	//
	// note: this *could* be expanded to support all containers registered in the FluidContainerRegistry,
	// but that is likely to cause unwanted behaviour due to containers being registered
	// that are only intended to be filled via specific non-right-click methods (ex: TE florbs)
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.isCanceled() || event.useItem != Event.Result.DEFAULT)
			return;

		if (event.entityPlayer.getHeldItem() == null)
			return;

		if (event.entityPlayer.getHeldItem().getItem() != Items.glass_bottle)
			return;

		ItemStack emptyContainer = event.entityPlayer.getHeldItem();

		MovingObjectPosition movingObjectPosition = null;
		try
		{
			movingObjectPosition = (MovingObjectPosition) getMovingObjectPositionFromPlayer.invoke(emptyContainer.getItem(), event.world, event.entityPlayer, true);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		if (movingObjectPosition == null)
			return;

		if (movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
			return;

		int x = movingObjectPosition.blockX;
		int y = movingObjectPosition.blockY;
		int z = movingObjectPosition.blockZ;
		Block block = event.world.getBlock(x, y, z);

		if (!(block instanceof BlockFluidGeneric))
			return;

		ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(new FluidStack(((BlockFluidGeneric) block).getFluid(), FluidContainerRegistry.BUCKET_VOLUME), emptyContainer);

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
}
