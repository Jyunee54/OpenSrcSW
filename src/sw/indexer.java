package sw;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class indexer {
	private int num;
	private String[][] idxTag;
	String fileRoute;
	ArrayList<ArrayList<ArrayList<String>>> allData = new ArrayList<>();
	ArrayList<ArrayList<String>> keyAndTFIDF = new ArrayList<>();
	
	public indexer(String route) {
		this.num=5;
		idxTag = new String[num][2];
		fileRoute = route;
		splitBody();
	}
	
	String[][] getTagData(){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(fileRoute);
			
			Element doc = document.getDocumentElement();
			NodeList children = doc.getChildNodes();
			int id=0;
			for(int i=0;i<children.getLength();i++) {
				Node node = children.item(i);
				NodeList nlist = node.getChildNodes();
				
				for(int j=0;j<nlist.getLength();j++) {
					Node childNode = nlist.item(j);
					if(childNode.getNodeType()==Node.ELEMENT_NODE) {
						Element element = (Element)childNode;
						String name = element.getNodeName();
						if(name.equals("title")) {
							idxTag[id][0]=element.getTextContent();
						}
						else if(name.equals("body")) {
							idxTag[id][1]=element.getTextContent();
							id++;
						}else
							continue;
					}
				}
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
		return idxTag;
	}
	
	void splitBody() {
		String[][] tagData = getTagData();
		String[][] data1 = new String[num][];
		
		for(int i=0;i<data1.length;i++) {
			data1[i] = tagData[i][1].split("#");
		}
		
		String[] temp = new String[2];
		for(int i=0;i<data1.length;i++) {
			ArrayList<ArrayList<String>> keywordAndFre = new ArrayList<>();
			for(int j=0;j<data1[i].length;j++) {
				ArrayList<String> dataSave = new ArrayList<>();
				temp = data1[i][j].split(":");
				dataSave.add(temp[0]);
				dataSave.add(temp[1]);
				keywordAndFre.add(dataSave);
			}
			allData.add(keywordAndFre);
		}
		for(int i=0;i<allData.size();i++) {
			for(int j=0;j<allData.get(i).size();j++) {
				this.keyAndTFIDF.add(calTFIDF(allData.get(i).get(j).get(0)));
			}
		}
		
	}
	
	ArrayList<String> calTFIDF(String keyword) {
		int tf[] = new int[5];
		int df=0;
		double result;
		String res = "";
		ArrayList<String> keyAndTfidf = new ArrayList<String>();
		keyAndTfidf.add(keyword);
		
		for(int i=0;i<allData.size();i++) {	
			for(int j=0;j<allData.get(i).size();j++) {
				if(allData.get(i).get(j).get(0).equals(keyword)) {
					tf[i]=Integer.parseInt(allData.get(i).get(j).get(1));
					df++;
					break;
				}
				
			}
		}
		
		for(int i=0;i<tf.length;i++) {
			if(df!=0) {
				result = Math.round(tf[i] * Math.log((double)num/(double)df) * 100)/100.0;
				res = res + i + " " + result + " ";
			}
		}
		keyAndTfidf.add(res);
		return keyAndTfidf;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void makeHashmap() throws IOException {
		FileOutputStream fileStream = new FileOutputStream("./file/index.post");
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
		
		HashSet<ArrayList<String>> keywordList = new HashSet<>(keyAndTFIDF);
		ArrayList<ArrayList<String>> keyAndTFIDF_fn = new ArrayList<>(keywordList);
		
		HashMap TFIDF = new HashMap();
		
		for(int i=0;i<keyAndTFIDF_fn.size();i++) {
			TFIDF.put(keyAndTFIDF_fn.get(i).get(0), keyAndTFIDF_fn.get(i).get(1));
		}
		
		objectOutputStream.writeObject(TFIDF);
		objectOutputStream.close();
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void readMap() throws IOException, ClassNotFoundException {
		FileInputStream fileStream = new FileInputStream("./file/index.post");
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		HashMap hashMap = (HashMap)object;
		Iterator<String> it = hashMap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			String value = (String) hashMap.get(key);
			System.out.println(key+" → "+ value);
		}
		
	}
	
}
