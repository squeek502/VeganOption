package squeek.veganoption.content;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IContentModule
{
	/**
	 * Instantiate and register blocks and items
	 */
	void create();

	/**
	 * Register things with the OreDictionary
	 */
	void oredict();

	/**
	 * Add recipes
	 */
	void recipes();

	/**
	 * Handle anything else (called from postInit)
	 */
	void finish();

	/**
	 * Handle client-side postInit registration (called before finish()).
	 */
	@SideOnly(Side.CLIENT)
	void clientSidePost();

	/**
	 * Handle client-side preInit registration (called after create() and oredict()).
	 */
	@SideOnly(Side.CLIENT)
	void clientSidePre();
}
