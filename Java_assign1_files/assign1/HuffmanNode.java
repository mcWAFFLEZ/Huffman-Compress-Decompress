package assign1;

public class HuffmanNode implements Comparable<HuffmanNode> {
	private HuffmanNode leftChild;
	private HuffmanNode rightChild;
	private int frequency;
	private short twoBytesData;
	private boolean visited;

	public HuffmanNode(int frequency, short twoBytesData) {
		this.leftChild = null;
		this.rightChild = null;
		this.frequency = frequency;
		this.twoBytesData = twoBytesData;
		this.visited = false;
	}
	
	public HuffmanNode(short twoBytesData) {
		this(0,twoBytesData);
	}
	
	public HuffmanNode(int frequency) {
		this(frequency,(short)0);
	}
	
	public HuffmanNode() {
		this(0,(short)0);
	}

	public HuffmanNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(HuffmanNode leftChild) {
		this.leftChild = leftChild;
	}

	public HuffmanNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(HuffmanNode rightChild) {
		this.rightChild = rightChild;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public short getTwoBytesData() {
		return twoBytesData;
	}

	public void setTwoBytesData(short twoBytesData) {
		this.twoBytesData = twoBytesData;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	@Override
	public String toString() {
		char one = (char)((twoBytesData >> 8) & 0xff);
		char two = (char)(twoBytesData & 0xff);
		return one+""+two+" = "+Integer.toBinaryString(twoBytesData)+" = "+ frequency;
	}

	@Override
	public int compareTo(HuffmanNode hNode) {
		if (this.frequency > hNode.frequency)
			return 1;
		else if (this.frequency < hNode.frequency)
			return -1;
		return 0;
	}
}
