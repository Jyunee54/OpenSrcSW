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

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class makeCollection {
	int listNum=0;
	String fileRoute;

	public makeCollection(String route) {
		this.fileRoute=route;
	}
	
	public File[] makeFileList(String path) {
		File dir = new File(path);
		return dir.listFiles();
	}

	void makeXml() {

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			File files[] = null;
			files = makeFileList(fileRoute);

			Document document = docBuilder.newDocument();
			Element docs = document.createElement("docs");
			document.appendChild(docs);
			listNum=files.length;

			for (int i = 0; i < files.length; i++) {

				org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");

				Element doc = document.createElement("doc");
				docs.appendChild(doc);
				doc.setAttribute("id", String.valueOf(i));

				String t = html.title();
				Element title = document.createElement("title");
				title.appendChild(document.createTextNode(t));
				doc.appendChild(title);

				String b = html.body().text();
				Element body = document.createElement("body");
				body.appendChild(document.createTextNode(b));
				doc.appendChild(body);

			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			DOMSource source = new DOMSource(docs);
			StreamResult result = new StreamResult(new FileOutputStream(new File("./file/collection.xml")));

			transformer.transform(source, result);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
