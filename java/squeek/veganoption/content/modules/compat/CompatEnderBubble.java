package squeek.veganoption.content.modules.compat;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.content.registry.RelationshipRegistry;

public class CompatEnderBubble implements IContentModule
{

	@Override
	public void create()
	{
		FluidContainerRegistry.registerFluidContainer(new FluidStack(Ender.fluidRawEnder, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.ENDER_PEARL), new ItemStack(FrozenBubble.frozenBubble));
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(Items.ENDER_PEARL), new ItemStack(FrozenBubble.frozenBubble), new ItemStack(Ender.bucketRawEnder));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(FrozenBubble.frozenBubble, 1, 1), new ItemStack(Ender.rawEnder));
	}

}
