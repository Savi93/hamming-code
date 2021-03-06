import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//14057 Savev David
public class HammingCode 
{
	public static void encode(String message, String filename)
	{
		FileWriter filewrite;
		
		if(message.length() > 0)
		{
			try 
			{
				filewrite = new FileWriter(filename, true);
				
				//Diesen For Zyklus manipuliert jedes Zeichen des Textes.
				for(int j = 0; j < message.length(); j++)
				{		
					//Jedes Zeichen des Textes wird in ASCII konvertiert.
					 String ascii = Integer.toBinaryString(message.charAt(j)); 
					 
					 //Hier, werden "0" am Anfang des ACII String verkettet um l�nge 8 zu erhalten.
					 if(ascii.length() == 6)
						 ascii = "00" + ascii;
					 else if(ascii.length() == 7)
						 ascii = "0" + ascii;
					 
					 //Die ASCII String wird in zwei Teile mit Gr��e 4 zerbrochen.
					 String lower = ascii.substring(4);
					 String higher = ascii.substring(0, 4);
					 
					 //F�r beide substrings, werden die Parit�t bits berechnet; zwei neue Stringen (mit Gr��e 7) die auch deren enthalten werden kreiert.
					 Boolean[] paritylow = computeParityBits(lower.charAt(0), lower.charAt(1), lower.charAt(2), lower.charAt(3));		
					 String resultlow = boolToString(paritylow[0]) + boolToString(paritylow[1]) + lower.charAt(0) + boolToString(paritylow[2]) + lower.substring(1);
					 
					 Boolean[] parityhigh = computeParityBits(higher.charAt(0), higher.charAt(1), higher.charAt(2), higher.charAt(3));		
					 String resulthigh = boolToString(parityhigh[0]) + boolToString(parityhigh[1]) + higher.charAt(0) + boolToString(parityhigh[2]) + higher.substring(1);
					 
					 //Hier werden beide Hamming-Kodes verkettet und ins File hinzugef�gt.
					 filewrite.write(resulthigh + resultlow + " ");
				}
				
				filewrite.write("\r\n");
				filewrite.close();
			} 
			
			catch (IOException e) 
			{
				System.out.println(" FEHLER: Probleme beim Erstellen / �ffnen der Datei! ");
			}
		}
	}
	
	public static String decode(String filename)
	{
		BufferedReader fileread;
		String decoded = "";
		
		try 
		{
			fileread = new BufferedReader(new FileReader(filename));
			
			String line = fileread.readLine();
			
			//Hier, lese ich jede Zeile des Textes und f�ge jede bin�r Sequenz die durch ein " " geteilt ist, in eine String-Array.
			while(line != null)
			{
				String[] arrayascii = line.split(" ");
				
				//F�r jede bin�r Sequenz ins Array (die aus 14 Zeichen besteht), mache ich die folgende Manipulationen:
				for(String ascii : arrayascii)
				{
					 try
					 {
						 Integer.parseInt(ascii, 2);
						 
						 //Die Sequenz wird in zwei Teile zerbrochen.
						 String lower = ascii.substring(7);
						 String higher = ascii.substring(0, 7);
						 
						 //F�r beide substrings, wird durch die Methode verifyParity das Wert von jede Parit�t bit verifiziert.
						 boolean lowp1 = verifyParity(lower.charAt(0), lower.charAt(2), lower.charAt(4), lower.charAt(6));
						 boolean lowp2 = verifyParity(lower.charAt(1), lower.charAt(2), lower.charAt(5), lower.charAt(6));
						 boolean lowp3 = verifyParity(lower.charAt(3), lower.charAt(4), lower.charAt(5), lower.charAt(6));
						 
						 boolean highp1 = verifyParity(higher.charAt(0), higher.charAt(2), higher.charAt(4), higher.charAt(6));
						 boolean highp2 = verifyParity(higher.charAt(1), higher.charAt(2), higher.charAt(5), higher.charAt(6));
						 boolean highp3 = verifyParity(higher.charAt(3), higher.charAt(4), higher.charAt(5), higher.charAt(6));
						 
						 //Wenn zu mindestens eine der verifizierten Werte "1" ist, haben wir einen Fehler; 
						 //so soll man die 3 bit verifizierte Summe in Dezimal konvertieren und den Wert in dessen Position umkehren.
						 if(highp1 || highp2 || highp3)
						 {
							 int error = Integer.parseInt((boolToString(highp3) + boolToString(highp2) + boolToString(highp1)), 2) - 1;
							 
							 if(higher.charAt(error) == '0')
								 higher = setCharAt(higher, error, '1');
							 else
								 higher = setCharAt(higher, error, '0');
						}
						
						 //Hier, eliminiere ich aus die MS Bin�r Sequenz die Parit�t bits und f�ge es in eine neue String.
						 String resulthigh = higher.substring(2, 3) + higher.substring(4);
						 
						 if(lowp1 || lowp2 || lowp3)
						 {
							 int error = Integer.parseInt((boolToString(lowp3) + boolToString(lowp2) + boolToString(lowp1)), 2) - 1;
							 
							 if(lower.charAt(error) == '0')
								 lower = setCharAt(lower, error, '1');
							 else
								 lower = setCharAt(lower, error, '0');
						 }
						 
						//Hier, eliminiere ich aus die LS Bin�r Sequenz die Parit�t bits und f�ge es in eine neue String.
						 String resultlow = lower.substring(2, 3) + lower.substring(4);
						 
						 //Hier werden die 4 MSB mit die 4 LSB zusammen verkettet, in die entsprechende Zeichen konvertiert ins decoded String gef�gt.
						 String resultstring = resulthigh + resultlow;
						 int resultint = Integer.parseInt(resultstring, 2);
						 char resultchar = (char)resultint;
						 
						 decoded += resultchar;
					 }
					 
					 catch(NumberFormatException e)
					 {
						 throw new IOException();
					 }
				}
				
				 decoded += "\n";
				 line = fileread.readLine();
			}
			
			fileread.close();
		} 
		
		catch (FileNotFoundException e) 
		{
			
			System.out.println(" FEHLER: Nicht bestehender Datei! ");
		} 
		
		catch (IOException e) 
		
		{
			System.out.println(" FEHLER: Probleme beim Lesen der Datei! ");
			decoded = "";
		}
		
		return decoded;
	}
	
	private static Boolean[] computeParityBits(char first, char second, char third, char fourth)
	{
		 Boolean[] array = new Boolean[3];

		 array[0] = charToBoolean(first) ^ charToBoolean(second) ^ charToBoolean(fourth);
		 array[1] = charToBoolean(first) ^ charToBoolean(third) ^ charToBoolean(fourth);
		 array[2] = charToBoolean(second) ^ charToBoolean(third) ^ charToBoolean(fourth);
		 
		 return array;
	}
	
	private static Boolean verifyParity(char p, char first, char second, char third)
	{ 
		 return charToBoolean(p) ^ charToBoolean(first) ^ charToBoolean(second) ^ charToBoolean(third);
	}
	
	private static boolean charToBoolean(char bit)
	{
		if(bit == '0')
			return false;
		
		return true;
	}
	
	private static String boolToString(boolean bit)
	{
		if(bit)
			return "1";
		return "0";
	}
	
	private static String setCharAt(String text, int position, char ch)
	{
		String result = text.substring(0, position);
		result = result + ch;
		result = result + text.substring(position + 1);
		
		return result;
	}
}
