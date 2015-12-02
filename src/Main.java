
public class Main {

	public static void main(String[] args) {
		if(args.length >1){
			if(args[0].equalsIgnoreCase("-c")){
				MyCompress.main(args);
				System.exit(0);
			} else if(args[0].equalsIgnoreCase("-d")){
				MyDecompress.main(args);
				System.exit(0);
			} 
		}
		System.out.println("Compress : MyLzw -c [-f] PathFile/String [-o PathCompressedFile]");
		System.out.println("Decompress : MyLzw -d PathCompressedFile [-o PathDecompressedFile]");
		
		

	}

}
