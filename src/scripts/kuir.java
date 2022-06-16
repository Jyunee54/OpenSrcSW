package scripts;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class kuir {

	public static void main(String[] args) throws ClassNotFoundException, IOException, SAXException, ParserConfigurationException {
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
		else if(command.equals("-s")) {
			String path = "./file/"+args[1];
			if(args[2].equals("-q")) {
				String query = args[3];
				searcher searcher = new searcher(path, query);
				searcher.printSim();
			}
			else
				System.out.println("Query doesn't exist");
		}
		else if(command.equals("-m")) {
			String path = "./file/"+args[1];
			if(args[2].equals("-q")) {
				String query = args[3];
				MidTerm midterm = new MidTerm(path, query);
				
			}
			else
				System.out.println("Query doesn't exist");
		}
	}
}
