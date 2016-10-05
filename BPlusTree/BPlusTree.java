import java.util.AbstractMap;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		// Return null if key or root is empty
		if (key == null || root == null) {
			return null;
		}
		
		Node<K, T> curr = root;
		while (curr instanceof IndexNode) {
			if (curr.keys == null) {
				return null;
			}
			int i = 0;
			while(i < curr.keys.size() && curr.keys.get(i).compareTo(key) < 0) {
				i++;
			}
			
			curr = ((IndexNode<K, T>)curr).children.get(i);
			
			
		}
		if (curr instanceof LeafNode) {
			if (curr.keys == null) {
				return null;
			}
			int i = 0;
			while(i < curr.keys.size() && curr.keys.get(i).compareTo(key) < 0) {
				i++;
			}
			if (i < curr.keys.size() && curr.keys.get(i).compareTo(key) == 0) {
				return ((LeafNode<K, T>)curr).values.get(i);
			} else {
				return null;
			}
			
		} else {
			return null;
		}

	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		if (key == null) {
			return;
		}
		if (root == null) {
			root = new LeafNode<K, T>(key, value);
			return;
		}
		Node<K, T> curr = root;
		while (curr instanceof IndexNode) {
			int i = 0;
			while(i < curr.keys.size() && curr.keys.get(i).compareTo(key) < 0) {
				i++;
			}
			
			curr = ((IndexNode<K, T>)curr).children.get(i);
		}
		
		((LeafNode<K, T>)curr).insertSorted(key, value);
		//TODO: Add detection for overflow
		if (curr.isOverflowed()) {
			
		}
	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {

		return null;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {

		return null;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {
		if (key == null || root == null) {
			return;
		}
		Node<K, T> curr = root;
		while (curr instanceof IndexNode) {
			if (curr.keys == null) {
				return;
			}
			int i = 0;
			while(i < curr.keys.size() && curr.keys.get(i).compareTo(key) < 0) {
				i++;
			}
			
			curr = ((IndexNode<K, T>)curr).children.get(i);
		}
		if (curr.keys == null) {
			return;
		}
		int i = 0;
		while(i < curr.keys.size() && curr.keys.get(i).compareTo(key) < 0) {
			i++;
		}
		if (i < curr.keys.size() && curr.keys.get(i).compareTo(key) == 0) {
			curr.keys.remove(i);
			((LeafNode<K, T>)curr).values.remove(i);
			
			//TODO: Add handling for underflow
		} else {
			return;
		}
	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

}
