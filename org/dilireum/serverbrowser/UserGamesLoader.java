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
public class UserGamesLoader extends DefaultHandler {
	private final String	inputFile;
	private final GameMap	games;

	private Game			game;
	private String			gameID;
	private Mod				mod;
	private String			modID;
	private Folder			folder;
	private String			folderName;
	private String			folderIcon;
	private Server			server;
	private String			ipAddress;
	private int				port;
	private String			hostName;
	private String			engine;
	private String			tempVal;
	private String			defaultParms;
	private String			akmXmlVersion;
	
	private ArrayList<Integer>		pList;		
	private Integer				protocol;
	private MasterServer			master;
	private String					masterAddress;
	private int					masterPort;
	private String					masterNameQuery;
	private String					masterType;
	private boolean				masterEnabled;

	public UserGamesLoader(GameMap gameList, String gamesXmlFile, String ficon, String xmlVersion) {
		inputFile		= gamesXmlFile;
		games			= gameList;
		folderIcon		= ficon;
		akmXmlVersion	= xmlVersion;
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
			SysLogger.logMsg(logLevel, game.nodeName + "ID: " + game.nodeID + ", Node Type: " + game.nodeType + 
					", Installed " + game.isInstalled + ", Displayed " + game.isDisplayed);
			for (int i = 0; i < game.folders.getCount(); i++) {
				Folder f = (Folder) game.folders.get(i);
				SysLogger.logMsg(logLevel, "\tFolder: " + f.nodeName);
				for (int j = 0; j < f.serverList.getCount(); j++) {
					Server s = f.serverList.get(j);
					SysLogger.logMsg(logLevel, "\t\tServer: " + s.readableHostName + " Address: " + 
							s.address.getHostAddress() + ":" + s.port);
				}
			}
			SysLogger.logMsg(logLevel, "\tMods");
			for (int i = 0; i < game.childList.getCount(); i++) {
				mod = (Mod) game.childList.get(i);
				SysLogger.logMsg(logLevel, "\t\t" + mod.nodeName + ", ID: " + mod.nodeID + ", Node Type: " + mod.nodeType + 
						", Discriminator: " + mod.discriminator + ", Installed " + mod.isInstalled + 
						", Displayed " + mod.isDisplayed);
			}
		}
	}
	
	public void initVars() {
		game			= null;
		gameID			= null;
		mod				= null;
		modID			= null;
		folder			= null;
		folderName		= null;
		ipAddress		= null;
		port			= 0;
		hostName		= null;
		engine			= null;
		defaultParms	= null;
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
		if (qName.equalsIgnoreCase("usergames")) {
			String versionID = attributes.getValue("version");
			if ((versionID == null) || !versionID.equals(akmXmlVersion)) {
				SysLogger.logMsg(0, "usergames.xml version is " + versionID + " when " + akmXmlVersion + " was expected");
				System.exit(1);
			}
		} else if (qName.equalsIgnoreCase("game")) {
			// initialize all game variables
			initVars();
			gameID = attributes.getValue("id");
			if ((game = games.get(gameID)) == null) {
				SysLogger.logMsg(0, "Could not find game ID = " + gameID + " from usergames.xml in list of games in games.xml");
			}
		} else if (qName.equalsIgnoreCase("master")) {
			masterEnabled = attributes.getValue("enabled").equals("yes");
			protocol	= null;
			pList		= new ArrayList<Integer>();
		} else if (qName.equalsIgnoreCase("mod")) {
			if (game == null) return;
			modID = attributes.getValue("id");
			if ((mod = (Mod) game.childList.findGameNode(modID)) == null) {
				SysLogger.logMsg(0, "Could not find mod ID = " + modID + " from usergames.xml in list of mods for game " + game.nodeName + " in games.xml");
			} else {
				mod.startParms = defaultParms;
			}
		} else if (qName.equalsIgnoreCase("folder")) {
			folderName = attributes.getValue("name");
			folder = game.addFolder(folderName, folderIcon);
		} else if (qName.equalsIgnoreCase("start")) {
			engine = attributes.getValue("engine").toLowerCase();
			game.multiEngine = engine.equalsIgnoreCase("default") ? false : true;	// default engine value is "default"
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

		if (qName.equalsIgnoreCase("displaygame")) {
			game.isDisplayed = tempVal.equalsIgnoreCase("true") ? true : false;
		} else if (qName.equalsIgnoreCase("gameinstalled")) {
			game.isInstalled = tempVal.equalsIgnoreCase("true") ? true : false;
		} else if (qName.equalsIgnoreCase("workingdir")) {
	    	game.workingDir.put(engine.toLowerCase(), new VarEntry(engine.toLowerCase(), tempVal));
		} else if (qName.equalsIgnoreCase("startcommand")) {
	    	game.startCommand.put(engine.toLowerCase(), new VarEntry(engine.toLowerCase(), tempVal));
		} else if (qName.equalsIgnoreCase("startparms")) {
	    	game.startParms.put(engine.toLowerCase(), new VarEntry(engine.toLowerCase(), tempVal));
		} else if (qName.equalsIgnoreCase("userid")) {
			game.loginID = tempVal;
		} else if (qName.equalsIgnoreCase("password")) {
			game.loginPassword = tempVal;
		} else if (qName.equalsIgnoreCase("defaultmodparms")) {
			game.defaultModParms = tempVal;
			defaultParms = tempVal;
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
		} else if (qName.equalsIgnoreCase("address")) {
			ipAddress = tempVal;
		} else if (qName.equalsIgnoreCase("port")) {
			port = Integer.parseInt(tempVal);
		} else if (qName.equalsIgnoreCase("hostname")) {
			hostName = tempVal;
		} else if (qName.equalsIgnoreCase("server")) {
			if (game.gameNetProtocol.equals("q3s") || game.gameNetProtocol.equals("q3i")) {
				server = new Q3Server(game, ipAddress, port);
			} else if (game.gameNetProtocol.equals("etqw")) {
				server = new ETQWServer(game, ipAddress, port);
			} else {
				SysLogger.logMsg(0, "Unknown game server protocol \"" + game.gameNetProtocol + "\".  Ignoring.");
			}
			server.hostName = hostName;
			server.readableHostName = hostName;
			server.gameIconFile = game.iconFileName;
			folder.serverList.add(server);
		} else if (qName.equalsIgnoreCase("displaymod")) {
			mod.isDisplayed = tempVal.equalsIgnoreCase("true") ? true : false;
		} else if (qName.equalsIgnoreCase("modinstalled")) {
			mod.isInstalled = tempVal.equalsIgnoreCase("true") ? true : false;
		}  else if (qName.equalsIgnoreCase("moddir")) {
			mod.workingDir = tempVal;
		} else if (qName.equalsIgnoreCase("modparm")) {
			mod.startParms = tempVal;
		} else if (qName.equalsIgnoreCase("modcommand")) {
			mod.startCommand = tempVal;
		} else {
			SysLogger.logMsg(6, "Unknown Entity: " + qName);
		}
	}
}
