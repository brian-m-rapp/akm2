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

import java.io.IOException;
import java.util.ArrayList;

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
public class GameDefaultsLoader extends DefaultHandler {
	private final String			inputFile;
	private final GameMap			games;

	private Game					game;
	private String					gameID;
	private String					arch;
	private Mod					mod;
	private String					modID;
	private String					engine;
	private String					tempVal;
	private String					akmXmlVersion;
	private String					osArch;
	
	private ArrayList<Integer>		pList;		
	private Integer				protocol;
	private MasterServer			master;
	private String					masterAddress;
	private int					masterPort;
	private String					masterNameQuery;
	private String					masterType;
	private boolean				masterEnabled;

	public GameDefaultsLoader(GameMap gameList, String defaultsXmlFile, String xmlVersion, String arch) {
		inputFile		= defaultsXmlFile;
		games			= gameList;
		akmXmlVersion	= xmlVersion;
		osArch			= arch;
	}

	public void parseGames() {
		
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
	
	public void printGames(int logLevel) {
		// This is straight from GamesLoader.java and needs to be modified to make sense for usergames.xml
		for (Game game = games.getFirst(); game != null; game = games.getNext(game.nodeID)) {
			SysLogger.logMsg(logLevel, game.nodeName + "ID: " + game.nodeID + ", Node Type: " + game.nodeType + ", ServerType: " + game.gameNetProtocol + ", Arch: " + osArch);
			for (int i = 0; i < game.masterList.getCount(); i++) {
				(game.masterList.get(i)).printInfo(logLevel);
			}
			VarEntry	wd = game.workingDir.getFirst();
			VarEntry	sc = game.startCommand.getFirst();
			VarEntry	sp = game.startParms.getFirst();
			while (wd != null) {
				SysLogger.logMsg(logLevel, "\tEngine: " + wd.getKey());
				SysLogger.logMsg(logLevel, "\t\tDefault dir: " + wd.getValue());
				SysLogger.logMsg(logLevel, "\t\tCommand: " + sc.getValue());
				SysLogger.logMsg(logLevel, "\t\tParms: " + sp.getValue());
				wd = game.workingDir.getNext(wd.getKey());
				sc = game.startCommand.getNext(sc.getKey());
				sp = game.startParms.getNext(sp.getKey());
			}
			
			SysLogger.logMsg(logLevel, "\tMods:");
			for (int i = 0; i < game.childList.getCount(); i++) {
				mod = (Mod) game.childList.get(i);
				SysLogger.logMsg(logLevel, "\t\t" + mod.nodeName + ", ID: " + mod.nodeID + ", Node Type: " + mod.nodeType + ", Discriminator: " + mod.discriminator);
			}
		}
	}
	
	public void initVars() {
		game			= null;
		gameID			= null;
		mod				= null;
		modID			= null;
		engine			= null;
		arch			= null;
		protocol		= null;
		pList			= new ArrayList<Integer>();		
		master			= null;
		masterAddress	= null;
		masterPort		= 0;
		masterNameQuery	= null;
		masterType		= null;
		masterEnabled	= false;
	}

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		SysLogger.logMsg(9, "Found starting element " + qName);
		if (qName.equalsIgnoreCase("gamedefaults")) {
			String versionID = attributes.getValue("version");
			if ((versionID == null) || !versionID.equals(akmXmlVersion)) {
				SysLogger.logMsg(0, inputFile + " version is " + versionID + " when " + akmXmlVersion + " was expected");
				System.exit(1);
			}
		} else if (qName.equalsIgnoreCase("game")) {
			// initialize all game variables
			initVars();
			gameID = attributes.getValue("gid");
			if ((game = games.get(gameID)) == null) {
				SysLogger.logMsg(0, "Could not find game ID = " + gameID + " from " + inputFile + " in list of games in games.xml");
			}
		} else if (qName.equalsIgnoreCase("arch")) {
			if (game == null) return;
			arch = attributes.getValue("os");
		} else if (qName.equalsIgnoreCase("master")) {
			masterEnabled = attributes.getValue("enabled").equals("yes");
			protocol	= null;
			pList		= new ArrayList<Integer>();		
		} else if (qName.equalsIgnoreCase("mod")) {
			if (game == null) return;
			modID = attributes.getValue("mid");
			if ((mod = (Mod) game.childList.findGameNode(modID)) == null) {
				SysLogger.logMsg(0, "Could not find mod ID = " + modID + " from " + inputFile + " in list of mods for game " + game.nodeName + " in games.xml");
			}
		} else if (qName.equalsIgnoreCase("start")) {
			if (osArch.equals(arch)) {
				engine = attributes.getValue("engine").toLowerCase();
				game.multiEngine = engine.equalsIgnoreCase("default") ? false : true;	// default engine value is "default"
			}
		} else {
			SysLogger.logMsg(0, "Unknown Element: " + qName);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (game == null) {
			SysLogger.logMsg(0, "game could not be found for game ID " + gameID + ", ignoring all elements");
			return;
		}
		SysLogger.logMsg(9, "Found ending element: " + qName + " with value: " + tempVal);

		if (qName.equalsIgnoreCase("defaultdir")) {
			if (osArch.equals(arch)) {
				game.workingDir.put(engine.toLowerCase(), new VarEntry(engine.toLowerCase(), tempVal));
			}
		} else if (qName.equalsIgnoreCase("defaultcommand")) {
			if (osArch.equals(arch)) {
				game.startCommand.put(engine.toLowerCase(), new VarEntry(engine.toLowerCase(), tempVal));
			}
		} else if (qName.equalsIgnoreCase("defaultparms")) {
			if (osArch.equals(arch)) {
				game.startParms.put(engine.toLowerCase(), new VarEntry(engine.toLowerCase(), tempVal));
			}
		} else if (qName.equalsIgnoreCase("defaultmodparms")) {
			game.defaultModParms = tempVal;
		} else if (qName.equalsIgnoreCase("protid")) {
			protocol = Integer.parseInt(tempVal);
			pList.add(protocol);
		} else if (qName.equalsIgnoreCase("url")) {
			masterAddress = tempVal;
		} else if (qName.equalsIgnoreCase("masterport")) {
			masterPort = Integer.parseInt(tempVal);
		} else if (qName.equalsIgnoreCase("queryname")) {
			masterNameQuery = tempVal;
		} else if (qName.equalsIgnoreCase("mastertype")) {
			masterType = tempVal;
		} else if (qName.equalsIgnoreCase("master")) {
			if (masterType.equalsIgnoreCase("q3m")) {
				if (masterNameQuery != null) {
					master = new Q3MasterServer(game, masterAddress, masterPort, pList, masterNameQuery, masterEnabled);
				} else {
					master = new Q3MasterServer(game, masterAddress, masterPort, pList, masterEnabled);
				}
			} else if (masterType.equalsIgnoreCase("http")) {
				master = new ETQWMasterServer(game, masterAddress, masterEnabled);
			}
			game.masterList.add(master);
		} else if (qName.equalsIgnoreCase("modparms")) {
			mod.startParms = tempVal;
		}
	}
}
