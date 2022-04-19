package scripts;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class searcher {
	String route;
	String query;
	ArrayList<Integer> tf = new ArrayList<>();
	ArrayList<String> kw = new ArrayList<>();
	int idNum = 5;
	
	public searcher(String path, String query) {
		this.route = path;
		this.query = query;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	double CalcSim(int id) throws ClassNotFoundException, IOException {
		double result=0.0;
		
		double[] wgt = new double[kw.size()];
		HashMap hashMap = getFile();
		
		for(int i=0;i<kw.size();i++) {
			Iterator<String> iter = hashMap.keySet().iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				if(key.equals(kw.get(i))) {
					String value = (String) hashMap.get(key);
					double w = splitValue(id, value);
					if(w!=-1) {
						wgt[i] = w * (double)tf.get(i);
						iter.remove();
						break;
					}
				}
			}
		}
		
		for(int i=0;i<kw.size();i++) {
			result += wgt[i];
		}
		
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	double CalcSim_master(int id) throws ClassNotFoundException, IOException {
		double result=0.0;
		
		double[] wgt = new double[kw.size()];
		double[] wq2 = new double[kw.size()];
		double[] wid = new double[kw.size()];
		HashMap hashMap = getFile();
		
		for(int i=0;i<kw.size();i++) {
			Iterator<String> iter = hashMap.keySet().iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				if(key.equals(kw.get(i))) {
					String value = (String) hashMap.get(key);
					double w = splitValue(id, value);
					if(w!=-1) {
						wgt[i] = w * (double)tf.get(i);
						wq2[i] = (double)tf.get(i)*tf.get(i);
						wid[i] = w*w;
						iter.remove();
						break;
					}
				}
			}
		}
		
		for(int i=0;i<kw.size();i++) {
			result += wgt[i];
		}
		
		double sumwq = 0.0;
		double sumwid = 0.0;
		for(int i=0;i<wq2.length;i++) {
			sumwq += wq2[i];
			sumwid += wid[i];
		}
		
		double sim = 0.0;
		if((sumwq!=0.0)&&(sumwid!=0.0)) {
			sim = result / Math.sqrt(sumwq) / Math.sqrt(sumwid);
		}
		
		return sim;
	}
	
	double splitValue(int id, String v) {
		String[] weight = v.split(" ");
		for(int i=0;i<(weight.length)/2;i+=2) {
			if(id==Double.parseDouble(weight[i])) {
				return Double.parseDouble(weight[i+1]);
			}
		}
		return 0;
	}
	
void printSim() throws ClassNotFoundException, IOException, SAXException, ParserConfigurationException {
		
		kkma();
		
		double[] sim = new double[idNum];
		int[] index = {0,1,2,3,4};
		for(int i=0;i<sim.length;i++) {
			sim[i] = CalcSim(i);
		}
		
		for(int i=0;i<sim.length-1;i++) {
			for(int k=0;k<sim.length-1;k++) {
				if(sim[k]<sim[k+1]) {
					double temp1=sim[k];
					sim[k]=sim[k+1];
					sim[k+1]=temp1;
					int temp2=index[k];
					index[k]=index[k+1];
					index[k+1]=temp2;
				}
			}
		}
		
		if(sim[0]==0.0) {
			System.out.println("검색된 문서가 없습니다.");
		}
		else {
			for(int i=0;i<3;i++) {
				if(sim[i]==0.0)
					break;
				else {
					System.out.print("title : "+ getName(index[i]));
					System.out.printf(" (doc id=%d), 유사도 : %.2f\n",index[i], sim[i]);
				}
			}
		}
		
	}

	void printSim_master() throws ClassNotFoundException, IOException, SAXException, ParserConfigurationException {
	
	kkma();
	
	double[] sim = new double[idNum];
	int[] index = new int[idNum];
	for(int i=0;i<idNum;i++) {
		index[i]=i;
	}
	
	for(int i=0;i<sim.length;i++) {
		sim[i] = CalcSim(i);
	}
	
	for(int i=0;i<sim.length-1;i++) {
		for(int k=0;k<sim.length-1;k++) {
			if(sim[k]<sim[k+1]) {
				double temp1=sim[k];
				sim[k]=sim[k+1];
				sim[k+1]=temp1;
				int temp2=index[k];
				index[k]=index[k+1];
				index[k+1]=temp2;
			}
		}
	}
	
	if(sim[0]==0.0) {
		System.out.println("검색된 문서가 없습니다.");
	}
	else {
		for(int i=0;i<3;i++) {
			if(sim[i]==0.0)
				break;
			else {
				System.out.printf("%d등 ", i+1);
				System.out.print(getName(index[i]));
				System.out.printf(" (doc id=%d), 유사도 : %.2f\n",index[i], sim[i]);
			}
		}
	}
	
}

	String getName(int id) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse("./file/index.xml");
		
		Element doc = document.getDocumentElement();
		NodeList children = doc.getChildNodes();
		
		Node docNode = children.item(id);
		Node titleNode = docNode.getFirstChild();
		Element element = (Element)titleNode;
		String nodeName = element.getNodeName();
		if(nodeName.equals("title")) {
			return element.getTextContent();
		}
		return "errer";
	}
	
	@SuppressWarnings("rawtypes")
	HashMap getFile() throws IOException, ClassNotFoundException {
		
		FileInputStream fileStream = new FileInputStream(route);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		HashMap hashMap = (HashMap)object;
		
		return hashMap;
	}
	
	void kkma() {		//query keyword 추출
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		
		for(int i=0;i<kl.size();i++) {
			Keyword kwrd = kl.get(i);
			kw.add(kwrd.getString());
			tf.add(kwrd.getCnt());
		}
	}
	
}
