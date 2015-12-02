import java.nio.ByteBuffer;

public class Code {

	private int index;
	private Character caractere;

	public Code(int index) {
		this.index = index;
	}

	public Code(int index, char caractere) {
		this.index = index;
		this.caractere = caractere;
	}

	public int getIndex() {
		return index;
	}

	public char getChar() {
		return caractere;
	}

	/** Renvoie le code sous forme d'un tableau d'octet */
	public byte[] getCode(int nbBits) {
		// selon le nombre de bits attendu, la taille du tableau varie selon la
		// formule suivante
		int arrayLength = ((nbBits - 1) / 8) + 1;
		byte[] indexB = new byte[arrayLength];

		// recuperation du tableau d'octet correspondant au nombre a coder
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putInt(index);
		byte[] thenumber = buf.array();
		// remplissage du tableau a renvoyer, le nombre sera donc coder sur un
		// nombre minimal d'octets necessaire
		for (int i = 0; i < arrayLength; i++) {
			indexB[arrayLength - i - 1] = thenumber[4 - i - 1];
		}
		
		//on renvoie le code sous forme d'un tableau d'octets (dependant du fait que le caractere soit null)
		if(caractere == null){
			return indexB;
		} else {
			byte[] toReturn = new byte[indexB.length+1];
			for(int i=0;i<indexB.length;i++){
				toReturn[i]=indexB[i];
			}
			toReturn[toReturn.length-1] = (byte)caractere.charValue();
			return toReturn;
		}
		
	}

	public String toString() {
		if (caractere != null) {
			return index + "" + caractere;
		} else {
			return index + "";
		}
	}

}
