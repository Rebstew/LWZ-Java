import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MyCompress {

	/** Le dictionnaire */
	private static List<String> dictionary;
	/** Liste des terme a coder */
	private static List<String> toCode;
	/** Liste des code a ecrire dans le fichier de fin */
	private static List<Code> coded;
	
	private static int nbByteBefore;
	private static int nbByteAfter;

	/**
	 * remplie le dictionnaire a l'aide de la chaine de caractere passe en
	 * parametre Remplie aussi une liste des terme du dictionnaire a coder
	 * utiliser a l'etape 1
	 */
	private static void createDictionary(String toCompress) {
		dictionary = new ArrayList<String>();
		toCode = new Vector<String>();
		dictionary.add("");
		String mot = "";
		for (int i = 0; i < toCompress.length(); i++) {
			mot += toCompress.charAt(i);
			if (!dictionary.contains(mot)) {
				dictionary.add(mot);
				toCode.add(mot);
				mot = "";
			}
		}
		if (!mot.equals(""))
			toCode.add(mot);
	}

	/** creer la liste des code a ecrire dans le fichier (ou sur le terminal) */
	private static void createCodedStrings() {
		coded = new Vector<Code>();
		for (int i = 0; i < toCode.size(); i++) {

			String mot = toCode.get(i);
			// si il ne s'agit pas du dernier mot a coder
			if (i != toCode.size() - 1) {
				Code code = getCodedWord(mot);
				coded.add(code);
			} else { // sinon on procede a un traitement special selon son
						// existence dans le dictionnaire

				// le traitement est un peu de la bidouille, mais reste
				// parfaitement logique
				// en effet, le dernier mot a coder fera toujours partie du
				// dictionnaire
				// Si il apparait en revanche deux fois dans la liste de mot a
				// coder il sera coder seulement par l'indice du mot dans le
				// dictionnaire
				// qui lui est déja coder

				// si le mot apparait une seul fois dans la liste de mot a coder
				int last = toCode.lastIndexOf(mot);
				int first = toCode.indexOf(mot);
				if (first == last) {
					// on le code normalement
					Code code = getCodedWord(mot);
					coded.add(code);
				} else {
					// sinon on ne renvoie que l'indice dans le dictionnaire
					Code code = new Code(dictionary.indexOf(mot));
					coded.add(code);
				}
			}
		}

		toCode.clear(); // plus besoin de la liste de mot
		dictionary.clear(); // plus besoin du dictionaire.
		toCode = null;
		dictionary = null;
	}

	/** Renvoie le code du mot passer en parametre */
	private static Code getCodedWord(String mot) {
		if (mot.length() == 1) {
			Code code = new Code(0, mot.charAt(0));
			return code;
		} else {
			String sousMot = mot.substring(0, mot.length() - 1);
			char end = mot.charAt(mot.length() - 1);
			int index = dictionary.indexOf(sousMot);
			Code code = new Code(index, end);
			return code;
		}
	}

	/** Ecrit le restultat de la compression dans le fichier pointer par path */
	private static void writeLZW() {
		try {
			File toWrite = new File(pathFileCompressed);
			FileOutputStream fstream = new FileOutputStream(toWrite);
			int nbByte = 0;

			// On ecrit tous les code
			for (int i = 0; i < coded.size(); i++) {
				Code code = coded.get(i);
				// recupere le code sous forme d'un tableau d'octets
				byte[] buff = getBinaryCode(code, i);
				nbByte += buff.length;
				// on l'ecrit
				fstream.write(buff);
				
			}

			nbByteAfter = nbByte;
			fstream.flush();
			fstream.close();
			System.out.println("Taille fichier "+pathFileCompressed+" "+ nbByte + " octets");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERREUR LORS DE L'ECRITURE DU FICHIER");
		}
	}
	
	/** Ecrit le resultat de la compression dans le terminal */
	private static void writeTerminal(){
		try{
			int nbByte = 0;
			//on ecrit tous les codes
			for (int i = 0; i < coded.size(); i++) {
				Code code = coded.get(i);
				// recupere le code sous forme d'un tableau d'octets
				byte[] buff = getBinaryCode(code, i);
				nbByte += buff.length;
				// on l'ecrit
				System.out.write(buff);
			}
			
			nbByteAfter = nbByte;
			System.out.println("Taille compression "+nbByte+" octets");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * renvoie le code sous forme d'un tableau d'octets de taille variable selon
	 * son indice
	 */
	private static byte[] getBinaryCode(Code code, int index) {
		if (index == 0) {
			byte[] toReturn = new byte[1];
			toReturn[0] = (byte) code.getChar();
			return toReturn;
		} else {
			int nbBits = (int) Math.ceil(Math.log(index + 1) / Math.log(2));
			byte[] toReturn = code.getCode(nbBits);
			return toReturn;
		}
	}
	
	private static boolean fromFile = false;
	private static String pathFile;
	private static String toCompress ="";
	private static boolean outputFile = false;
	private static String pathFileCompressed;
	
	private static void testArguments(String[] args){
		if(args.length == 2){ //MyLzw -c String
			toCompress = args[1];
			nbByteBefore = toCompress.length();
		} else if(args.length == 3){ //MyLzw -c -f PathFile
			if(args[1].equalsIgnoreCase("-f")){
				fromFile = true;
				pathFile = args[2];
			} else {
				System.out.println("Compress : MyLzw -c [-f] PathFile/String [-o PathCompressedFile]");
				System.exit(0);
			}
		} else if(args.length == 4){ //MyLzw -c String -o PathCompressedFile
			if(args[2].equalsIgnoreCase("-o")){
				outputFile = true;
				pathFileCompressed = args[3];
				toCompress = args[1];
				nbByteBefore = toCompress.length();
			}else {
				System.out.println("Compress : MyLzw -c [-f] PathFile/String [-o PathCompressedFile]");
				System.exit(0);
			}
		} else if(args.length == 5){ //MyLzw -c -f PathFile -o PathCompressedFile
			if(args[1].equalsIgnoreCase("-f")){
				fromFile = true;
				pathFile = args[2];
			}else {
				System.out.println("Compress : MyLzw -c [-f] PathFile/String [-o PathCompressedFile]");
				System.exit(0);
			}
			if(args[3].equalsIgnoreCase("-o")){
				outputFile = true;
				pathFileCompressed = args[4];
			} else {
				System.out.println("Compress : MyLzw -c [-f] PathFile/String [-o PathCompressedFile]");
				System.exit(0);
			}
			
		} else {
			System.out.println("Compress : MyLzw -c [-f] PathFile/String [-o PathCompressedFile]");
			System.exit(0);
		}
	}
	
	private static void readFileToCompress(){
		try{
			toCompress = "";
			File f = new File(pathFile);
			int nbByte =0;
			BufferedReader r = new BufferedReader(new FileReader(f));
			toCompress += r.readLine();
			nbByte += toCompress.length();
			String line;
			while((line = r.readLine()) != null){
				nbByte += line.length()+1;
				toCompress +="\n"+line;
			}
			
			nbByteBefore = nbByte;
			System.out.println("Taille du fichier"+pathFile+" avant compression "+nbByte+" octets");
			r.close();
		}catch(Exception e){
			System.out.println("Erreur lors de la lecture du fichier "+pathFile);
		}
	}

	public static void main(String[] args) {
		
		//Chaine de test
		//String toCompress = "";
		//String toCompress = "a";
		//String toCompress = "turlututuutuutututu";
		//String toCompress = "turlututuutuututut";
		//String toCompress = "turlututuutuutututtut";
		
		
		//Traitement des arguments
		testArguments(args);
		
		if(fromFile){
			readFileToCompress();
		}
		
		
		// ##Etape 1 & 2##
		// creation du dictionnaire (et recuperation de la fin de la chaine au
		// cas ou le mot retrouver se trouve déja dans le dictionnaire
		// Les indices sont déja présent
		createDictionary(toCompress);

		// ##Etape 3##
		createCodedStrings();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("test"));
			for(Code c : coded){
				writer.write(c.toString());
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//## Etape 4##
		if(outputFile){
			writeLZW();
		} else {
			writeTerminal();
		}
		
		int taux =(int) (((nbByteAfter*1.0)/nbByteBefore)*100);
		System.out.println("Taux de compression "+taux+"%");
	}
}
