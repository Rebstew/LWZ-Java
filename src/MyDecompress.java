import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Vector;

public class MyDecompress {

	private static boolean outputFile = false;
	private static String outputFilePath;
	private static String compressedFilePath;

	private static List<String> dictionary;

	private static void reconstructDictionary() {
		try {

			BufferedReader reader = new BufferedReader(new FileReader(
					compressedFilePath));
			dictionary = new Vector<String>();
			dictionary.add(""); // ajout du caractere null
			int tmp = reader.read(); // lecture du premier caractere
			dictionary.add((char) tmp + ""); // ajout du caractere dans le
												// dictionnaire
			int codeRead = 1; // nombre de code déja lu
			int nbBits = 1; // nombre de bits correspondant a un indice lors de
							// la compression
			int sizeIndex = 1; // le nombre d'octet pour lire un indice

			while ((tmp = reader.read()) != -1) {
				// Recreation de l'index
				byte[] indexB = new byte[sizeIndex];
				indexB[0] = (byte)tmp;
				for (int i = 1; i < sizeIndex; i++) {
					tmp = reader.read();
					indexB[i] = (byte) tmp;
				}
				int index = constructIntFromIndexByteArray(indexB);
				System.out.println("size:"+dictionary.size());
				System.out.println(index);
				// recreation du mot
				if ((tmp = reader.read()) != -1) {
					String toAdd = dictionary.get(index) + "" + ((char) tmp);
					dictionary.add(toAdd);
				} else {
					dictionary.add(dictionary.get(index));
				}
				
				System.out.println(dictionary.get(dictionary.size()-1));

				// calcul des nouveaux sizeIndex
				codeRead++;
				nbBits = (int) Math.ceil(Math.log(codeRead) / Math.log(2));
				sizeIndex = ((nbBits - 1) / 8) + 1;
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erreur lors de la lecture du fichier "
					+ compressedFilePath);
		}
	}

	private static int constructIntFromIndexByteArray(byte[] indexB) {
		/*int index = 0;
		for (int i = (indexB.length - 1); i >= 0; i--) {
			index += (indexB[i] << (i*8));
		}

		return index;
		*/
		
		ByteBuffer b = ByteBuffer.allocate(4);
		for(int i=(4-indexB.length);i>0;i--){
			b.put((byte)0);
		}
		b.put(indexB);
		b.clear();
		return b.getInt();
		
	}

	private static void writeDecompressedText() {
		try {
			BufferedWriter writer;
			if (outputFile) {
				writer = new BufferedWriter(new FileWriter(outputFilePath));
				for (String s : dictionary) {
					writer.write(s);
				}
				writer.flush();
				writer.close();
			} else {
				writer = new BufferedWriter(new OutputStreamWriter(System.out));
				for (String s : dictionary) {
					writer.write(s);
				}
				writer.flush();
				writer.close();
			}
		} catch (Exception e) {
			System.out.println("Erreur lors de l'ecriture");
			e.printStackTrace();
		}
	}

	private static void testArguments(String[] args) {
		if (args.length == 2) {
			compressedFilePath = args[1];
		} else if (args.length == 4) {
			if (args[2].equalsIgnoreCase("-c")) {
				compressedFilePath = args[1];
				outputFile = true;
				outputFilePath = args[3];
			} else {
				System.out
						.println("Decompress : MyLzw -d PathCompressedFile [-o PathDecompressedFile]");
				System.exit(0);
			}
		} else {
			System.out
					.println("Decompress : MyLzw -d PathCompressedFile [-o PathDecompressedFile]");
			System.exit(0);
		}
	}

	public static void main(String[] args) {

		// testArguments(args);
		
		compressedFilePath = "C:/Compressed.txt";

		reconstructDictionary();

		writeDecompressedText();
		
		
		
	}

}
