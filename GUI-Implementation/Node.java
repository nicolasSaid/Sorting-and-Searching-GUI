
public class Node implements NodeFunctions {
	private final int key;
	private Node parent;
	private Node left;
	private Node right;
	
	public Node(int key) {
		this.key = key;
		parent = null;
		left = null;
		right = null;
	}
	
	public int getKey() {
		return key;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public Node getLeft() {
		return left;
	}
	
	public Node getRight() {
		return right;
	}
	
	public void setLeft(Node n) {
		left = n;
	}
	
	public void setRight(Node n) {
		right = n;
	}
	
	public void setParent(Node n) {
		parent = n;
	}
	
	public String toString() {
		String ans ="(" + key + ",";
		if(parent != null) {
			ans += parent.getKey();
		}
		ans += ",";
		if (left != null){
			ans += left.getKey();
		}
		ans += ",";
		if (right != null){
			ans += right.getKey();
		}
		return ans + ")";
	}
	
	public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() == o.getClass()){
            Node other = (Node) o;
            if (this.getKey() == other.getKey()){
                return true;
            }
        }
        return false;
	}
}