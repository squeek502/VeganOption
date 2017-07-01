package squeek.veganoption.helpers;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import squeek.veganoption.content.modules.Ender;

import java.lang.reflect.Method;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

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

		if (event.getTarget() == null)
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

		EntityPlayer player = event.getEntityPlayer();
		ItemStack heldItem = event.getItemStack();
		if (heldItem == null || heldItem.getItem() != Items.GLASS_BOTTLE)
			return;

		World world = event.getWorld();

		RayTraceResult rayTraceResult = null;
		try
		{
			rayTraceResult = (RayTraceResult) rayTraceMethod.invoke(heldItem.getItem(), world, player, true);
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

		if (!event.getEntityPlayer().canPlayerEdit(pos, rayTraceResult.sideHit, heldItem))
			return;

		FluidStack fluidStack = new FluidStack(((BlockFluidClassic) block).getFluid(), Fluid.BUCKET_VOLUME);
		boolean didFill = fillContainer(fluidStack, heldItem) > 0;

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

	public static boolean isFluidContainer(ItemStack container)
	{
		return container != null && (container.hasCapability(FLUID_HANDLER_CAPABILITY, null));
	}

	public static boolean isEmptyContainer(ItemStack container)
	{
		if (isFluidContainer(container))
		{
			IFluidHandler handler = FluidUtil.getFluidHandler(container);
			if (handler != null)
			{
				for (IFluidTankProperties prop : handler.getTankProperties())
				{
					if (prop.getContents() != null)
						return prop.getContents().amount <= 0;
				}
			}
		}
		return false;
	}

	public static int fillContainer(FluidStack fluid, ItemStack into)
	{
		if (isFluidContainer(into))
		{
			IFluidHandler intoCapability = FluidUtil.getFluidHandler(into);
			if (intoCapability != null)
				return intoCapability.fill(fluid, true);
		}
		return 0;
	}

	public static void drainHandlerIntoContainer(IFluidHandler from, FluidStack toFill, ItemStack into)
	{
		if (isFluidContainer(into))
		{
			IFluidHandler intoCapability = FluidUtil.getFluidHandler(into);
			if (intoCapability != null)
				from.drain(intoCapability.fill(toFill, true), true);
		}
	}

	public static void drainContainerIntoHandler(ItemStack from, IFluidHandler into)
	{
		if (isFluidContainer(from))
		{
			IFluidHandler fromCapability = FluidUtil.getFluidHandler(from);
			if (fromCapability != null)
			{
				for (IFluidTankProperties fromTank : fromCapability.getTankProperties())
					fromCapability.drain(into.fill(fromTank.getContents(), true), true);
			}
		}
	}
}
