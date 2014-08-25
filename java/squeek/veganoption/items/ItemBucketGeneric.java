package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidFinite;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemBucketGeneric extends ItemBucket
{
	public Block filledWith;

	public ItemBucketGeneric(Block filledWith)
	{
		super(filledWith);
		this.filledWith = filledWith;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onFillBucket(FillBucketEvent event)
	{
		if (event.isCanceled() || event.getResult() != Event.Result.DEFAULT)
			return;

		Block block = event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);

		if (block == filledWith && event.world.getBlockMetadata(event.target.blockX, event.target.blockY, event.target.blockZ) == 7)
		{
			event.world.setBlockToAir(event.target.blockX, event.target.blockY, event.target.blockZ);
			event.result = new ItemStack(this);
			event.setResult(Event.Result.ALLOW);
		}
	}
	
	public int getPlacedLiquidMetadata()
	{
		return filledWith instanceof BlockFluidFinite ? 7 : 0;
	}
	
	@Override
	public boolean tryPlaceContainedLiquid(World p_77875_1_, int p_77875_2_, int p_77875_3_, int p_77875_4_)
	{
        if (this.filledWith == Blocks.air)
        {
            return false;
        }
        else
        {
            Material material = p_77875_1_.getBlock(p_77875_2_, p_77875_3_, p_77875_4_).getMaterial();
            boolean flag = !material.isSolid();

            if (!p_77875_1_.isAirBlock(p_77875_2_, p_77875_3_, p_77875_4_) && !flag)
            {
                return false;
            }
            else
            {
                if (p_77875_1_.provider.isHellWorld && this.filledWith == Blocks.flowing_water)
                {
                    p_77875_1_.playSoundEffect((double)((float)p_77875_2_ + 0.5F), (double)((float)p_77875_3_ + 0.5F), (double)((float)p_77875_4_ + 0.5F), "random.fizz", 0.5F, 2.6F + (p_77875_1_.rand.nextFloat() - p_77875_1_.rand.nextFloat()) * 0.8F);

                    for (int l = 0; l < 8; ++l)
                    {
                        p_77875_1_.spawnParticle("largesmoke", (double)p_77875_2_ + Math.random(), (double)p_77875_3_ + Math.random(), (double)p_77875_4_ + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                }
                else
                {
                    if (!p_77875_1_.isRemote && flag && !material.isLiquid())
                    {
                        p_77875_1_.func_147480_a(p_77875_2_, p_77875_3_, p_77875_4_, true);
                    }

                    p_77875_1_.setBlock(p_77875_2_, p_77875_3_, p_77875_4_, this.filledWith, getPlacedLiquidMetadata(), 3);
                }

                return true;
            }
        }
	}
}
