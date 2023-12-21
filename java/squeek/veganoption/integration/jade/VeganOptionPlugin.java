package squeek.veganoption.integration.jade;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import squeek.veganoption.blocks.BlockRettable;

@WailaPlugin
public class VeganOptionPlugin implements IWailaPlugin
{
	@Override
	public void registerClient(IWailaClientRegistration registerer)
	{
		registerer.registerBlockComponent(RettableProvider.getInstance(), BlockRettable.class);
	}
}
