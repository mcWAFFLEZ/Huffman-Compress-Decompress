package assign1;

public class Runner {
	public static void main(String[] args) {
		// "C:\Users\pioni\Desktop\ass1_315922666_208375691\ass1_315922666_208375691\ExampleInputs\Romeo and Juliet  Entire Play.txt"
		// "C:\\Users\\pioni\\Desktop\\ass1_315922666_208375691\\ExampleInputs\\whu.txt"
		String[] in = new String[] { "C:\\Users\\pioni\\Desktop\\ass1_315922666_208375691\\ExampleInputs\\Romeo and Juliet  Entire Play.txt",
				"C:\\Users\\pioni\\Desktop\\kaka.txt" };
		String[] out = new String[] { "C:\\Users\\pioni\\Desktop\\egg.txt" };
		HuffmanEncoderDecoder x = new HuffmanEncoderDecoder();
		x.Compress(in, out);
		x.Decompress(out, in);
	}
}
