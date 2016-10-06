import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Stack;

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
		Node<K, T> curr = root, parent = null;
		Stack<IndexNode<K,T>> parents = new Stack<IndexNode<K,T>>();
		Stack<Integer> indices = new Stack<Integer>();
		int index = -1;
		while (curr instanceof IndexNode) {
			int i = 0;
			while(i < curr.keys.size() && curr.keys.get(i).compareTo(key) < 0) {
				i++;
			}
			parents.push((IndexNode<K,T>)curr);
			indices.push(i);
			parent = curr;
			curr = ((IndexNode<K, T>)curr).children.get(i);
			index = i;
		}
		
		((LeafNode<K, T>)curr).insertSorted(key, value);
		//TODO: Add detection for overflow
		if (curr.isOverflowed()) {
			if (parent == null) {
				Entry<K, Node<K,T>> entry = splitLeafNode((LeafNode<K, T>)curr);
				root = new IndexNode<K, T>(entry.getKey(), curr, entry.getValue());
			} else {
				Entry<K, Node<K,T>> entry = splitLeafNode((LeafNode<K, T>)curr);
				
				((IndexNode<K, T>)parent).insertSorted(entry, index);
			}
		}
//		//TODO: still need to detect IndexNode overflow
//		if (parent != null && parent.isOverflowed()) {
//			//Entry<K, Node<K,T>> entry = splitIndexNode((IndexNode<K,T>)parent);
//			// TODO: iteratively check parent's parent
//		}
		if (!indices.isEmpty()) {
			indices.pop();
		}
		while (!parents.isEmpty()) {
			IndexNode<K,T> last = parents.pop();
			 
			if (last.isOverflowed()) {
				Entry<K, Node<K,T>> entry = splitIndexNode(last);
				if (last == root) {
					root = new IndexNode<K,T>(entry.getKey(), last, entry.getValue());
				} else {
					int i = indices.pop();
					parents.peek().insertSorted(entry, i);
				}
			} else {
				break;
			}
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
		LeafNode<K,T> newLeaf = new LeafNode<K,T>(leaf.keys.get(BPlusTree.D), leaf.values.get(BPlusTree.D));
		for (int i = D + 1; i < leaf.keys.size(); i++) {
			newLeaf.insertSorted(leaf.keys.get(i), leaf.values.get(i));
		}
		for (int i = leaf.keys.size() - 1; i >= BPlusTree.D; i--) {
			leaf.keys.remove(i);
			leaf.values.remove(i);
		}
		newLeaf.nextLeaf = leaf.nextLeaf;
		newLeaf.previousLeaf = leaf;
		leaf.nextLeaf = newLeaf;
		return new AbstractMap.SimpleEntry<K, Node<K,T>>(newLeaf.keys.get(0), newLeaf);
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {
		ArrayList<K> keys = new ArrayList<K>(index.keys.size() - BPlusTree.D);
		ArrayList<Node<K,T>> nodes = new ArrayList<Node<K,T>>(index.children.size() - BPlusTree.D);
		for (int i = D; i < index.keys.size(); i++) {
			keys.add(index.keys.get(i));
		}
		for (int i = D; i < index.children.size(); i++) {
			nodes.add(index.children.get(i));
		}
		IndexNode<K,T> newIndex = new IndexNode<K,T>(keys, nodes);
		for (int i = index.keys.size() - 1; i >= BPlusTree.D - 1; i--) {
			index.keys.remove(i);
		}
		for (int i = index.children.size() - 1; i >= BPlusTree.D; i--) {
			index.children.remove(i);
		}
		return new AbstractMap.SimpleEntry<K, Node<K,T>>(newIndex.keys.get(0), newIndex);
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
		Stack<IndexNode<K,T>> parents = new Stack<IndexNode<K,T>>();
		Stack<Integer> indices = new Stack<Integer>();
		while (curr instanceof IndexNode) {
			if (curr.keys == null) {
				return;
			}
			int i = 0;
			while(i < curr.keys.size() && curr.keys.get(i).compareTo(key) < 0) {
				i++;
			}
			parents.push((IndexNode<K,T>)curr);
			indices.push(i);
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
			if (curr != root && curr.isUnderflowed()) {
				int index = indices.pop();
				if (index > 0) {
					// has left sibling
					IndexNode<K,T> parent = parents.peek();
					LeafNode<K,T> leftSibling = (LeafNode<K, T>) parent.children.get(index - 1);
					int position = handleLeafNodeUnderflow(leftSibling, (LeafNode<K, T>)curr, parent);
					if (position != -1) {
						parent.keys.remove(position);
					}
				} else {
					// has right sibling
					IndexNode<K,T> parent = parents.peek();
					LeafNode<K,T> rightSibling = (LeafNode<K,T>) parent.children.get(index + 1);
					int position = handleLeafNodeUnderflow((LeafNode<K, T>)curr, rightSibling, parent);
					if (position != -1) {
						parent.keys.remove(position);
					}
				}
			}
			while (!parents.isEmpty()) {
				IndexNode<K,T> last = parents.pop();
				if (last != root && last.isUnderflowed()) {
					int index = indices.pop();
					if (index > 0) {
						// has left sibling
						IndexNode<K,T> parent = parents.peek();
						IndexNode<K,T> leftSibling = (IndexNode<K,T>) parent.children.get(index - 1);
						int position = handleIndexNodeUnderflow(leftSibling, last, parent);
						if (position != -1) {
							parent.keys.remove(position);
						}
					} else {
						// has right sibling
						IndexNode<K,T> parent = parents.peek();
						IndexNode<K,T> rightSibling = (IndexNode<K,T>) parent.children.get(index + 1);
						int position = handleIndexNodeUnderflow(last, rightSibling, parent);
						if (position != -1) {
							parent.keys.remove(position);
						}
					}
				}
			}
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
		//TODO: need to change key when redistribution
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
