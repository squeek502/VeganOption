package squeek.veganoption.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import squeek.veganoption.blocks.BlockJutePlant;
import squeek.veganoption.blocks.BlockRettable;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.blocks.tiles.TileEntityComposter;

public class WailaRegistrar
{
	public static void register(IWailaRegistrar registrar)
	{
		// rettables (jute)
		registrar.registerBodyProvider(new ProviderRettable(), BlockRettable.class);

		// composter
		registrar.registerBodyProvider(new ProviderComposter(), TileEntityComposter.class);
		registrar.registerNBTProvider(new ProviderComposter(), TileEntityComposter.class);

		registrar.registerBodyProvider(new ProviderBasin(), TileEntityBasin.class);

		registrar.registerBodyProvider(new ProviderJutePlant(), BlockJutePlant.class);
	}
}