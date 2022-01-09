/*
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */
package org.dilireum.serverbrowser;

import java.io.*;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.dilireum.logging.SysLogger;

/**
 * @author brapp
 *
 */
public class LanguageLoader extends DefaultHandler {
	private final String						inputFile;
	private final ArrayList<LanguageEntry>		langList;
	private String								key;
	private String								value;
	
	public LanguageLoader(ArrayList<LanguageEntry> langs, String xmlFile) {
		inputFile	= xmlFile;
		langList	= langs;
	}

	public void parseLanguages() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			sp.parse(inputFile, this);
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void printLanguages(int logLevel) {
		for (int i = 0; i < langList.size(); i++) {
			SysLogger.logMsg(logLevel, "ISO 639 Value: " + langList.get(i).getIsoKey() + ", Name: " + langList.get(i).getName());
		}
	}
	
	public void initVars() {
	}
	
	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		value = "";
		SysLogger.logMsg(9, "Found starting element " + qName);
		if (qName.equalsIgnoreCase("language")) {
			key = attributes.getValue("id");
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		value = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		LanguageEntry	le;
		SysLogger.logMsg(9, "Found ending element: " + qName + " with value: " + value);
		
		if (qName.equalsIgnoreCase("language")) {
			le = new LanguageEntry(key, value);
			langList.add(le);
		}
	}
}
