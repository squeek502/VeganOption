package squeek.veganoption.content.registry;

import net.minecraft.item.ItemStack;
import squeek.veganoption.helpers.MiscHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RelationshipRegistry
{
	public static final HashMap<ItemStack, List<ItemStack>> parentToChildren = new HashMap<ItemStack, List<ItemStack>>();
	public static final HashMap<ItemStack, List<ItemStack>> childToParents = new HashMap<ItemStack, List<ItemStack>>();

	@Nonnull
	public static List<ItemStack> getParents(ItemStack child)
	{
		child = MiscHelper.getMatchingItemStackFromList(childToParents.keySet(), child);
		return child != null ? childToParents.get(child) : Collections.<ItemStack>emptyList();
	}

	@Nonnull
	public static List<ItemStack> getChildren(ItemStack parent)
	{
		parent = MiscHelper.getMatchingItemStackFromList(parentToChildren.keySet(), parent);
		return parent != null ? parentToChildren.get(parent) : Collections.<ItemStack>emptyList();
	}

	public static void addRelationship(ItemStack child, ItemStack parent)
	{
		ItemStack matchingChild = MiscHelper.getMatchingItemStackFromList(childToParents.keySet(), child);
		ItemStack matchingParent = MiscHelper.getMatchingItemStackFromList(parentToChildren.keySet(), parent);

		List<ItemStack> parentsOfChild = matchingChild != null ? childToParents.get(matchingChild) : new ArrayList<ItemStack>();
		List<ItemStack> childrenOfParent = matchingParent != null ? parentToChildren.get(matchingParent) : new ArrayList<ItemStack>();

		childrenOfParent.add(matchingChild != null ? matchingChild : child);
		parentsOfChild.add(matchingParent != null ? matchingParent : parent);

		childToParents.put(matchingChild != null ? matchingChild : child, parentsOfChild);
		parentToChildren.put(matchingParent != null ? matchingParent : parent, childrenOfParent);
	}
}
