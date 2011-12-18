package test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class xmldump {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		for (String arg : args) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(arg));
			dump("", document);
		}
	}
	
	private static void dump(String indent, Node document) {
		NodeList nl = document.getChildNodes();
		if (nl.getLength() == 0) {
			System.out.println(indent + "<" + document.getNodeName() + "/>");
		} else {
			System.out.println(indent + "<" + document.getNodeName() + ">");
			for (int i = 0; i < nl.getLength(); i++) {
				dump(indent + "\t", nl.item(i));
			}
			System.out.println(indent + "</" + document.getNodeName() + ">");
		}
		
	}
}
