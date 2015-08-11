package squeek.veganoption.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import squeek.veganoption.blocks.BlockBedGeneric;
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
		// x,y,z are necessary to allow specifying specific keys to sync for tileentities
		// see: https://bitbucket.org/ProfMobius/waila/issue/85/always-synchronize-x-y-and-z-for-tile
		registrar.registerSyncedNBTKey("x", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("y", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("z", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("Compost", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("Temperature", TileEntityComposter.class);
		registrar.registerSyncedNBTKey("Start", TileEntityComposter.class);

		registrar.registerStackProvider(new ProviderBed(), BlockBedGeneric.class);

		registrar.registerBodyProvider(new ProviderBasin(), TileEntityBasin.class);
	}
}