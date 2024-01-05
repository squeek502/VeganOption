package squeek.veganoption.integration.rei;

import net.neoforged.neoforge.fluids.FluidStack;

public class REIHelper
{
	public static dev.architectury.fluid.FluidStack getArchitecturyFluidStackFrom(FluidStack neoForgeFluidStack)
	{
		return dev.architectury.fluid.FluidStack.create(neoForgeFluidStack.getFluid(), neoForgeFluidStack.getAmount());
	}
}
