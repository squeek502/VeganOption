package squeek.veganoption.content;


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
}
