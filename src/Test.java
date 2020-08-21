import java.io.IOException;

//14057 Savev David
public class Test 
{

	public static void main(String[] args) throws IOException 
	{
		HammingCode.encode("Hi. This is a text to test the functionality of a Hamming Code!", "Hamming.txt");
		System.out.println(HammingCode.decode("Hamming.txt"));
	}
}
