package assign1;

/**
 * Assignment 1
 * Submitted by: 
 * Michael Chen. 	ID# 315922666
 * Gal Partuk. 	ID# 208375691
 */

// Uncomment if you wish to use FileOutputStream and FileInputStream for file access.
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Stack;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import base.Compressor;

public class HuffmanEncoderDecoder implements Compressor {

	public HuffmanEncoderDecoder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Compress(String[] input_names, String[] output_names) {
		FileInputStream input;
		BitOutputStream output;
		try {
			input = new FileInputStream(input_names[0]);
			output = new BitOutputStream(new FileOutputStream(output_names[0]));
			HashMap<Short, Integer[]> huffmanDictionary = new HashMap<>();
			HashMap<Short, Integer> huffmanFrequencyCount = new HashMap<>();
			PriorityQueue<HuffmanNode> huffmanMinHeap = new PriorityQueue<>();
			int numberOfBytes = 0;
			int numberOfNodes = 0;

			while (true) {
				// try reading first two bytes
				int firstByte = input.read();
				if (firstByte == -1)
					break;
				numberOfBytes++;
				int secondByte = input.read();

				// take two bytes of data and merge them into a short(2-byte-data type)
				short twoBytes = (short) firstByte;
				twoBytes <<= 8;
				if (secondByte != -1) {
					numberOfBytes++;
					twoBytes = (short) (twoBytes | secondByte);
				}
				// twoBytes should now hold 2 bytes of data

				// if the number of signs in the file is odd, last byte should be 0xff
				// insert the two-byte data into a HashMap in order to determine their frequency
				huffmanFrequencyCount.put(twoBytes, huffmanFrequencyCount.getOrDefault(twoBytes, 0) + 1);
				if (secondByte == -1)
					break;
			}

			// insert all huffman-nodes into a min-heap ordered by their frequency value
			for (Entry<Short, Integer> e : huffmanFrequencyCount.entrySet()) {
				numberOfNodes++;
				huffmanMinHeap.add(new HuffmanNode(e.getValue(), e.getKey()));
			}

			// from this line we should start building the huffman-tree
			// after this code block we will have a Heap of size one with the Root node
			// min node should be at left side and the one after should be on the right
			while (huffmanMinHeap.size() > 1) {
				HuffmanNode leftNode = huffmanMinHeap.poll();
				HuffmanNode rightNode = huffmanMinHeap.poll();
				HuffmanNode newRoot = new HuffmanNode(leftNode.getFrequency() + rightNode.getFrequency());
				newRoot.setLeftChild(leftNode);
				newRoot.setRightChild(rightNode);
				huffmanMinHeap.offer(newRoot);
			}
			// This is the root of the huffman tree, |left = 0|right = 1|
			HuffmanNode root = huffmanMinHeap.poll();
			// huffmanDictionary should be all set after this function
			// also header data should be written in the file at this point
			output.writeBits(32, numberOfBytes);
			output.writeBits(32, numberOfNodes);
			CreateHuffmanDictAndHeader(root, 0, 0, huffmanDictionary, output);

			// from this point the second pass should start
			input.close();
			input = new FileInputStream(input_names[0]);
			while (true) {
				// try reading first two bytes
				int firstByte = input.read();
				if (firstByte == -1)
					break;
				int secondByte = input.read();
				// take two bytes of data and merge them into a short(2-byte-data type)
				short twoBytes = (short) firstByte;
				twoBytes <<= 8;
				if (secondByte != -1)
					twoBytes = (short) (twoBytes | secondByte);
				// twoBytes should now hold 2 bytes of data
				// if the number of signs in the file is odd, last byte should be 0xff
				Integer[] data = huffmanDictionary.get(twoBytes);
				int dataToWrite = data[0];
				int bitsToWrite = data[1];
				output.writeBits(bitsToWrite, dataToWrite);
				if (secondByte == -1)
					break;
			}
			output.flush();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// this function take a huffman tree and initializes a dictionary based on it
	// this function also takes a list and inserts the header data into it
	public void CreateHuffmanDictAndHeader(HuffmanNode root, int code, int count, HashMap<Short, Integer[]> dict,
			BitOutputStream output) {
		if (root.getLeftChild() == null && root.getRightChild() == null) {
			short symbols = root.getTwoBytesData();
			Integer[] data = new Integer[] { code, count };
			dict.put(symbols, data);
			output.writeBits(1, 1);
			output.writeBits(8, symbols >> 8);
			output.writeBits(8, symbols & 0xff);
			return;
		}
		CreateHuffmanDictAndHeader(root.getLeftChild(), code << 1, count + 1, dict, output);
		CreateHuffmanDictAndHeader(root.getRightChild(), (code << 1) + 1, count + 1, dict, output);
		output.writeBits(1, 0);
	}

	public void CreateHuffmanDictionary(HuffmanNode root, String code, short count, HashMap<String, Short[]> dict) {
		if (root.getLeftChild() == null && root.getRightChild() == null) {
			short symbols = root.getTwoBytesData();
			Short[] data = new Short[] { symbols, count };
			dict.put(code, data);
			return;
		}
		CreateHuffmanDictionary(root.getLeftChild(), code + "0", (short) (count + 1), dict);
		CreateHuffmanDictionary(root.getRightChild(), code + "1", (short) (count + 1), dict);
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names) {
		BitInputStream input;
		BitOutputStream output;
		Stack<HuffmanNode> stack = new Stack<>();
		HashMap<String, Short[]> huffmanDictionary = new HashMap<>();

		try {
			input = new BitInputStream(new FileInputStream(input_names[0]));
			output = new BitOutputStream(new FileOutputStream(output_names[1]));
			// read header data
			int numberOfBytes = input.readBits(32);
			int numberOfLeaf = input.readBits(32);
			int numberOfInternalNodes = numberOfLeaf - 1;

			// start building huffman tree from header data
			int x = input.readBits(1);
			while (numberOfLeaf != 0 || numberOfInternalNodes != 0) {
				if (x == 1) {
					stack.push(new HuffmanNode((short) input.readBits(16)));
					x = input.readBits(1);
					numberOfLeaf--;
				} else if (x == 0) {
					HuffmanNode rightNode = stack.pop();
					HuffmanNode leftNode = stack.pop();
					HuffmanNode father = new HuffmanNode();
					father.setLeftChild(leftNode);
					father.setRightChild(rightNode);
					stack.push(father);
					x = input.readBits(1);
					numberOfInternalNodes--;
				}
			}

			// stack should contain the root of the tree
			// this function initialized the dictionary base on the tree
			CreateHuffmanDictionary(stack.pop(), "", (short) 0, huffmanDictionary);
			int length = 1;
			String bit = Integer.toBinaryString(x);
			while (numberOfBytes > 0) {
				if (huffmanDictionary.containsKey(bit)) {
					Short[] value = huffmanDictionary.get(bit);
					int len = value[1];
					if (len == length) {
						output.writeBits(16, value[0]);
						length = 0;
						numberOfBytes = numberOfBytes - 2;
						bit = "";
					}
				}
				bit = bit + Integer.toBinaryString(input.readBits(1));
				length++;
				//System.out.println(bit);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

}
