package squeek.veganoption.content.registry;

import net.minecraft.world.item.Item;
import squeek.veganoption.helpers.MiscHelper;

import javax.annotation.Nonnull;
import java.util.*;

public class RelationshipRegistry
{
	public static final Map<Item, List<Item>> parentToChildren = new HashMap<>();
	public static final Map<Item, List<Item>> childToParents = new HashMap<>();

	@Nonnull
	public static List<Item> getParents(Item child)
	{
		child = MiscHelper.getMatchingItemFromList(childToParents.keySet(), child);
		return child != null ? childToParents.get(child) : Collections.emptyList();
	}

	@Nonnull
	public static List<Item> getChildren(Item parent)
	{
		parent = MiscHelper.getMatchingItemFromList(parentToChildren.keySet(), parent);
		return parent != null ? parentToChildren.get(parent) : Collections.emptyList();
	}

	public static void addRelationship(Item child, Item parent)
	{
		Item matchingChild = MiscHelper.getMatchingItemFromList(childToParents.keySet(), child);
		Item matchingParent = MiscHelper.getMatchingItemFromList(parentToChildren.keySet(), parent);

		List<Item> parentsOfChild = matchingChild != null ? childToParents.get(matchingChild) : new ArrayList<>();
		List<Item> childrenOfParent = matchingParent != null ? parentToChildren.get(matchingParent) : new ArrayList<>();

		childrenOfParent.add(matchingChild != null ? matchingChild : child);
		parentsOfChild.add(matchingParent != null ? matchingParent : parent);

		childToParents.put(matchingChild != null ? matchingChild : child, parentsOfChild);
		parentToChildren.put(matchingParent != null ? matchingParent : parent, childrenOfParent);
	}
}
