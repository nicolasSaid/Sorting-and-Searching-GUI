
public class BinarySearchTree implements BinarySearchTreeFunctions {
	private Node root;
	
	public BinarySearchTree() {
		root = null;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void setRoot(Node root) {
		this.root = root;
	}
		
	public void insertNode(Node z) {
		/*if(root == null) {
			setRoot(z);
			return;
		}
		Node temp = root;
		while(temp.getLeft() != null || temp.getRight() != null) {
			if(temp.getKey() < z.getKey()) {
				if(temp.getRight() == null) {
					temp.setRight(z);
					return;
				} else {
					temp = temp.getRight();
				}
			} else if(temp.getKey() > z.getKey()) {
				if(temp.getLeft() == null) {
					temp.setLeft(z);
					return;
				} else {
					temp = temp.getLeft();
				}
			} else {
				return;
			}
		}
		if(temp.getKey() > z.getKey()) {
			temp.setLeft(z);
			return;
		} else if(temp.getKey() < z.getKey()) {
			temp.setRight(z);
			return;
		}*/
		Node x = getRoot();
		Node y = null;
		while (x != null) { //...............................if x is null, y has an empty child for z
    		y = x; //.......................................y becomes x's parent
    		if(z.getKey() < x.getKey()){
        		x = x.getLeft(); //  .......................update x to it's left child
    		} else {
        		x = x.getRight(); //........................update x it it's right child
    		}
		}
		z.setParent(y); //..................................set z's parent to y (x's parent)
		if (y == null)
    		setRoot(z); //..................................set the root to z
		else if (z.getKey() < y.getKey())
    		y.setLeft(z); // ...............................update z's parent's left child to z
		else
    		y.setRight(z);
	}
	
	public void preOrderWalk(Node x) {
		if(x != null) {
			System.out.println(x.toString());
			preOrderWalk(x.getLeft());
			preOrderWalk(x.getRight());
		}
	}
	
	public void preOrderWalk(Node x, java.util.ArrayList<String> list) {
		if(x != null) {
			list.add(x.toString());
			preOrderWalk(x.getLeft(), list);
			preOrderWalk(x.getRight(), list);
		}
	}
	
	public void preOrderWalk(Node x, String id, java.util.ArrayList<String> result)	{
		if(x != null) {
			System.out.println(x.toString() + " " + id);
			result.add(x.getKey() + " " + id);
			preOrderWalk(x.getLeft(), "0"+id, result);
			preOrderWalk(x.getRight(), "1"+id, result);
		}
	}
	
	public void inOrderWalk(Node x) {
		if(x != null) {
			inOrderWalk(x.getLeft());
			System.out.println(x.toString());
			inOrderWalk(x.getRight());
		}
	}
	
	public void inOrderWalk(Node x, java.util.ArrayList<String> list) {
		if(x != null) {
			inOrderWalk(x.getLeft(), list);
			list.add(x.toString());
			inOrderWalk(x.getRight(), list);
		}
	}
	
	public void postOrderWalk(Node x) {
		if(x != null) {
			postOrderWalk(x.getLeft());
			postOrderWalk(x.getRight());
			System.out.println(x.toString());
		}
	}
	
	public void postOrderWalk(Node x, java.util.ArrayList<String> list) {
		if(x != null) {
			postOrderWalk(x.getLeft(), list);
			postOrderWalk(x.getRight(), list);
			list.add(x.toString());
		}
	}
		
	public Node getMax(Node x) {
		if(x.getRight() != null) {
			return(getMax(x.getRight()));
		}
		return x;
	}
	
	public Node getMin(Node x) {
		if(x.getLeft() != null) {
			return(getMin(x.getLeft()));
		}
		return x;
	}
	
	public Node getSuccessor(Node x) {
		if(x.getRight() != null) {
			return getMin(x.getRight());
		} else {
			while(x.getParent() != null && x.getParent().getKey() < x.getKey()) {
				x = x.getParent();
			}
		}
		return x.getParent();
	}
	
	public Node getPredecessor(Node x) {
		if(x.getLeft() != null) {
			return getMax(x.getLeft());
		} else {
			while(x.getParent() != null && x.getParent().getKey() > x.getKey()) {
				x = x.getParent();
			}
		}
		return x.getParent();
	}
	
	public Node getNode(Node x, int key) {
		if(x == null) {
			return null;
		}
		if(x.getKey() < key) {
			return(getNode(x.getRight(), key));
		} else if (x.getKey() > key) {
			return(getNode(x.getLeft(), key));
		}
		return x;
	}
	
	public int getHeight(Node x) {
		if(x == null) {
			return -1;
		}
		return 1 + Math.max(getHeight(x.getLeft()), getHeight(x.getRight()));
	}
	
	public void shiftNode(Node u, Node v) {
		if(u.getParent() == null) {
			setRoot(v);
		} else {
			if(u == u.getParent().getLeft()) {
				u.getParent().setLeft(v);
			} else {
				u.getParent().setRight(v);;
			}
		}
		if(v != null) {
			v.setParent(u.getParent());
		}
	}
	
	public void deleteNode(Node z) {
		if(z.getLeft() == null) {
			shiftNode(z,z.getRight());
		}else {
			if(z.getRight() == null) {
				shiftNode(z,z.getLeft());
			}else {
				Node y = getSuccessor(z);
				if(y.getParent() != z) {
					shiftNode(y,y.getRight());
					y.setRight(z.getRight());
					y.getRight().setParent(y);
				}
				shiftNode(z,y);
				y.setLeft(z.getLeft());
				y.getLeft().setParent(y);
			}
		}
	}
}
