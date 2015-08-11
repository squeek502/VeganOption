package squeek.veganoption.helpers;

import java.lang.reflect.Method;
import squeek.veganoption.blocks.BlockFluidGeneric;
import squeek.veganoption.content.modules.Ender;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class FluidContainerHelper
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new FluidContainerHelper());
	}

	protected static Method getMovingObjectPositionFromPlayer = ReflectionHelper.findMethod(Item.class, null, new String[]{"getMovingObjectPositionFromPlayer", "func_77621_a", "a"}, World.class, EntityPlayer.class, boolean.class);

	// fix non-water fluids being able to create water buckets
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onFillBucket(FillBucketEvent event)
	{
		if (event.isCanceled() || event.getResult() != Event.Result.DEFAULT)
			return;

		Block block = event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);

		// if we've gotten this far, then it shouldn't be able to be picked up by a bucket
		// ItemBucketGeneric would have handled it if it was possible to pick it up
		// this stops BlockFluidGenerics creating water buckets if they don't have a bucket item
		if (block instanceof BlockFluidGeneric)
		{
			event.setCanceled(true);
			event.setResult(Event.Result.DENY);
		}
	}

	// all this just for picking up generic fluids with a glass bottle
	// and fixing non-water fluids being able to create water bottles
	//
	// note: this *could* be expanded to support all containers registered in the FluidContainerRegistry,
	// but that is likely to cause unwanted behaviour due to containers being registered
	// that are only intended to be filled via specific non-right-click methods (ex: TE florbs)
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
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

		// raw ender is not bottle fillable
		if (block == Ender.rawEnder)
		{
			event.setCanceled(true);
			event.useItem = Event.Result.DENY;
			return;
		}

		if (!(block instanceof BlockFluidGeneric))
			return;

		if (!event.world.canMineBlock(event.entityPlayer, x, y, z))
			return;

		if (!event.entityPlayer.canPlayerEdit(x, y, z, movingObjectPosition.sideHit, emptyContainer))
			return;

		FluidStack fluidStack = new FluidStack(((BlockFluidGeneric) block).getFluid(), FluidContainerRegistry.BUCKET_VOLUME);
		boolean didFill = tryFillContainer(event.entityPlayer, emptyContainer, fluidStack) != null;

		// this cancels the interaction if the bottle is unable to be filled with the fluid,
		// which stops mod fluid blocks from creating water bottles, because all fluids *have* to use
		// Material.water to actually have the properties of a liquid...
		if (!didFill)
		{
			event.setCanceled(true);
			event.useItem = Event.Result.DENY;
		}
		else
		{
			event.world.setBlockToAir(x, y, z);
		}
	}

	public static ItemStack tryFillContainer(EntityPlayer player, ItemStack emptyContainer, FluidStack fluidStack)
	{
		ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluidStack, emptyContainer);

		if (filledContainer == null)
			return null;

		if (!player.capabilities.isCreativeMode)
		{
			--emptyContainer.stackSize;

			if (emptyContainer.stackSize <= 0)
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, filledContainer);
				if (!player.worldObj.isRemote)
					player.inventoryContainer.detectAndSendChanges();
			}
			else if (!player.inventory.addItemStackToInventory(filledContainer))
			{
				player.dropPlayerItemWithRandomChoice(filledContainer, false);
			}
		}

		return filledContainer;
	}

	public static ItemStack tryEmptyContainer(EntityPlayer player, ItemStack filledContainer)
	{
		ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(filledContainer);

		if (emptyContainer == null)
			return null;

		if (!player.capabilities.isCreativeMode)
		{
			--filledContainer.stackSize;

			if (filledContainer.stackSize <= 0)
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, emptyContainer);
				if (!player.worldObj.isRemote)
					player.inventoryContainer.detectAndSendChanges();
			}
			else if (!player.inventory.addItemStackToInventory(emptyContainer))
			{
				player.dropPlayerItemWithRandomChoice(emptyContainer, false);
			}
		}

		return emptyContainer;
	}
}
