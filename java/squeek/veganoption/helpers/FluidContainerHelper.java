package squeek.veganoption.helpers;

import java.lang.reflect.Method;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import squeek.veganoption.content.modules.Ender;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidContainerHelper
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new FluidContainerHelper());
	}

	protected static Method rayTraceMethod = ReflectionHelper.findMethod(Item.class, null, new String[]{"rayTrace", "func_77621_a", "a"}, World.class, EntityPlayer.class, boolean.class);

	// fix non-water fluids being able to create water buckets
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onFillBucket(FillBucketEvent event)
	{
		if (event.isCanceled() || event.getResult() != Event.Result.DEFAULT)
			return;

		Block block = event.getWorld().getBlockState(event.getTarget().getBlockPos()).getBlock();

		// if we've gotten this far, then it shouldn't be able to be picked up by a bucket
		// ItemBucketGeneric would have handled it if it was possible to pick it up
		// this stops BlockFluidGenerics creating water buckets if they don't have a bucket item
		if (block instanceof BlockFluidClassic)
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
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		if (event.isCanceled() || event.getResult() != Event.Result.DEFAULT)
			return;

		// TODO: Support for the OFF_HAND?
		EntityPlayer player = event.getEntityPlayer();
		if (player.getHeldItem(EnumHand.MAIN_HAND) == null)
			return;

		if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() != Items.GLASS_BOTTLE)
			return;

		ItemStack emptyContainer = player.getHeldItem(EnumHand.MAIN_HAND);
		World world = event.getWorld();

		RayTraceResult rayTraceResult = null;
		try
		{
			rayTraceResult = (RayTraceResult) rayTraceMethod.invoke(emptyContainer.getItem(), world, player, true);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		if (rayTraceResult == null)
			return;

		if (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK)
			return;

		BlockPos pos = rayTraceResult.getBlockPos();
		Block block = world.getBlockState(pos).getBlock();

		// raw ender is not bottle fillable
		if (block == Ender.rawEnder)
		{
			event.setCanceled(true);
			event.setResult(Event.Result.DENY);
			return;
		}

		if (!(block instanceof BlockFluidClassic))
			return;

		if (!world.canMineBlockBody(event.getEntityPlayer(), pos))
			return;

		if (!event.getEntityPlayer().canPlayerEdit(pos, rayTraceResult.sideHit, emptyContainer))
			return;

		FluidStack fluidStack = new FluidStack(((BlockFluidClassic) block).getFluid(), Fluid.BUCKET_VOLUME);
		boolean didFill = tryFillContainer(event.getEntityPlayer(), emptyContainer, fluidStack) != null;

		// this cancels the interaction if the bottle is unable to be filled with the fluid,
		// which stops mod fluid blocks from creating water bottles, because all fluids *have* to use
		// Material.water to actually have the properties of a liquid...
		if (!didFill)
		{
			event.setCanceled(true);
			event.setResult(Event.Result.DENY);
		}
		else
		{
			world.setBlockToAir(pos);
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
				player.dropItem(filledContainer, false);
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
				player.dropItem(emptyContainer, false);
			}
		}

		return emptyContainer;
	}
}
