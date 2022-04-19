package scripts;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class makeKeyword {
	private int num;
	private String[][] tagData;
	String fileRoute;
	
	public makeKeyword(String route) {
		this.num = 5;
		tagData = new String[num][2];
		fileRoute = route;
	}
	
	String[][] parsingXML(){
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
							tagData[id][0]=element.getTextContent();	
						}
						else if(name.equals("body")) {		
							tagData[id][1]=element.getTextContent();	
							id++;
						}
						else 
							continue;
					}
				}
			}
		}catch(Exception e) {
			System.out.println(e);
		}
		return tagData;
	}
	
	void indexXml(String[][] array) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
		
			Document document = builder.newDocument();
			Element docs = document.createElement("docs");
			document.appendChild(docs);
			
			for(int i=0;i<array.length;i++) {
				Element doc = document.createElement("doc");	
				docs.appendChild(doc);
				doc.setAttribute("id", String.valueOf(i));
				
				Element title = document.createElement("title");	
				title.appendChild(document.createTextNode(array[i][0]));
				doc.appendChild(title);
				
				Element body = document.createElement("body");		
				doc.appendChild(body);
				
				String testString = array[i][1];		
				String keywordString = "";
				KeywordExtractor ke = new KeywordExtractor();
				KeywordList kl = ke.extractKeyword(testString, true);
				for(int j=0;j<kl.size();j++) {
					Keyword kwrd = kl.get(j);
					keywordString = keywordString + kwrd.getString() + ":" + kwrd.getCnt() + "#";
				}
				body.appendChild(document.createTextNode(keywordString));
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			
			DOMSource source = new DOMSource(docs);
			StreamResult result = new StreamResult(new FileOutputStream(new File("./file/index.xml")));
			
			transformer.transform(source, result);
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}

}
