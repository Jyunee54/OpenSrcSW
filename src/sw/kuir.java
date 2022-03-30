package sw;

public class kuir {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String command = args[0];
		
		if(command.equals("-c")) {
			String path = args[1];
			makeCollection collection = new makeCollection(path);
			collection.makeXml();
		}
		else if(command.equals("-k")) {
			String path = "./file/" + args[1];
			makeKeyword keyword = new makeKeyword(path);
			keyword.indexXml(keyword.parsingXML());
		}
		else if(command.equals("-i")) {
			String path = "./file/" + args[1];
			indexer indexer = new indexer(path);
			try {
				indexer.makeHashmap();
				indexer.readMap();
			}catch(Exception e) {
				System.out.println(e);
			}
		}
		
	}
	

}

