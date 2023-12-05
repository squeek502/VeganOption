package squeek.veganoption.integration;

import squeek.veganoption.content.IContentModule;

public abstract class IntegratorBase extends IntegrationBase implements IContentModule
{
	// initialized by IntegrationHandler
	public String modID;

	protected IntegratorBase(String modId)
	{
		this.modID = modId;
	}

	protected String prefix(String itemName)
	{
		if (itemName.contains(":"))
			return itemName;
		else
			return modID + ":" + itemName;
	}
}
