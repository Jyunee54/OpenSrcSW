package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MidTerm {
	
	String query;
	String route;
	String idxTag[][];

	public MidTerm(String path, String query) {
		this.query = query;
		this.route = path;
	}
	
	//스니펫 계싼
	void showSnippet() throws SAXException, IOException, ParserConfigurationException {
		ArrayList<String> kw = new ArrayList<>();
		kw = kkma(query);
		getTagData();
		
		//스니펫 계산
		for(int i=0;i<idxTag.length;i++) {
			char str[] = idxTag[i][1].toCharArray();
			for(int k=0;k<idxTag[i][1].length()-30;k++) {
				String st = "";
				for(int j=k;j<k+30;j++) {
					st += str[j];
				}
				kkma(st);
			}
		}
		
		
		
		
	}
	
	//keyword 분석
	ArrayList<String> kkma(String qu) {
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(qu, true);
		ArrayList<String> list = new ArrayList<>();
		
		for(int i=0;i<kl.size();i++) {
			Keyword kwrd = kl.get(i);
			list.add(kwrd.getString());
		}
		return list;
	}
	
	//파일
	String[][] getTagData(){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(route);
			
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
	
	//파일 title
//	String[] getName(int id) throws SAXException, IOException, ParserConfigurationException {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
//		Document document = documentBuilder.parse("./file/index.xml");
//		String data[] = new String[2];
//		
//		Element doc = document.getDocumentElement();
//		NodeList children = doc.getChildNodes();
//		
//		Node docNode = children.item(id);
//		Node titleNode = docNode.getFirstChild();
//		Element titleElement = (Element)titleNode;
//		String titleName = titleElement.getNodeName();
//		if(titleName.equals("title")) {
//			data[0]=titleElement.getTextContent();
//		}
//		Node bodyNode = docNode.getLastChild();
//		Element bodyElement = (Element)bodyNode;
//		String bodyName = bodyElement.getNodeName();
//		if(bodyName.equals("body")) {
//			data[1]=bodyElement.getTextContent();
//		}
//		
//		return data;
//	}
}
