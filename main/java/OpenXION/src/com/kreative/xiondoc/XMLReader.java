/*
 * Copyright &copy; 2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.kreative.xiondoc.xdom.DocumentationSet;

/**
 * This is the XIONDocReader that reads XIONDoc 1.3's pure XML format.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XMLReader implements XIONDocReader {
	private XMLXDOMParser p = new XMLXDOMParser(true);
	
	public String derive(File f) {
		String fn = f.getAbsolutePath();
		while (fn.endsWith(".xnd") || fn.endsWith(".xml")) {
			fn = fn.substring(0, fn.length()-4);
		}
		return fn;
	}
	
	public void read(String xnd, DocumentationSet d) throws IOException {
		try {
			System.out.println("Parsing XML document...");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xnd)));
			System.out.println("Traversing XML document...");
			p.parseDocument(document, d);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXParseException saxpe) {
			throw new IOException(saxpe.getMessage() + " at " + saxpe.getLineNumber()+":"+saxpe.getColumnNumber());
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
}
