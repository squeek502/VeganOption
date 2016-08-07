package squeek.veganoption.content;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IContentModule
{
	/**
	 * Instantiate and register blocks and items
	 */
	public void create();

	/**
	 * Register things with the OreDictionary
	 */
	public void oredict();

	/**
	 * Add recipes
	 */
	public void recipes();

	/**
	 * Handle anything else (called from postInit)
	 */
	public void finish();

	/**
	 * Handle client-side registration (called from postInit)
	 */
	@SideOnly(Side.CLIENT)
	public void clientSide();
}
