package assign1;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class IORunner
{

	static String IN_FILE_PATH = "C:\\Users\\pioni\\Desktop\\ass1_315922666_208375691\\ExampleInputs\\whu.txt";

	static String OUT_FILE_PATH = "C:\\Users\\pioni\\Desktop\\egg.txt";

	public static void main(String[] args)
	{
		FileInputStream input;
		FileOutputStream output;
		try
		{
			input = new FileInputStream(IN_FILE_PATH);
			output = new FileOutputStream(OUT_FILE_PATH);

			while (true)					// Keep going until forced out.
//			for (int i = 0; i < 100; i++)	// Check only 100 first bytes.
			{
				int x = input.read();
				if (x != -1) // -1 is EOF
				{
					System.out.print(x);
					System.out.print((char)x);
//					output.write(x);
				}
				else
				{
					System.out.println(x);
					break;
				}
			}

			input.close();
			output.close();

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
