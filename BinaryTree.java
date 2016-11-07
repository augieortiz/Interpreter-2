public class BinaryTree {
	
		BinaryTree left, right;
		boolean isList;
		String value;
		String type;
		
		public BinaryTree()
		{
			this.left = null;
			this.right = null;
			this.isList = true;
			this.value = null;
			this.type = null;
		}
		
		public BinaryTree(String types, String values)
		{
			this.left = null;
			this.right = null;
			this.isList = false;
			this.value = values;
			this.type = types;
		}
}


