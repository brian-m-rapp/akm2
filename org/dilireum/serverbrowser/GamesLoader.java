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
public class GamesLoader extends DefaultHandler {
	private final String			inputFile;
	private final GameMap			games;

	private Game					game;
	private String					gameID;
	private String					gameName;
	private String					gameIcon;
	private String					serverQueryType;
	private String					authenticate;
	private Mod						mod;
	private String					modName;
	private String					modID;
	private String					modIcon;
	private String					modDiscrim;
	private boolean					hasCustomNames;
	private String					team1Name;
	private String					team2Name;
	private String					gameTypeID;
	private String					gameType;
	private String					ignoreAttrib;
	
	private String					tempVal;
	private String					varName;
	private String					akmXmlVersion;
	
	public GamesLoader(GameMap gameList, String gamesXmlFile, String xmlVersion) {
		inputFile		= gamesXmlFile;
		games			= gameList;
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
		for (Game game = games.getFirst(); game != null; game = games.getNext(game.nodeID)) {
			SysLogger.logMsg(logLevel, game.nodeName + " ID: " + game.nodeID + ", IconFile: " + game.iconFileName + ", Node Type: " + game.nodeType + ", ServerType: " + game.gameNetProtocol);
			for (int i = 0; i < game.masterList.getCount(); i++) {
				(game.masterList.get(i)).printInfo(logLevel);
			}
			for (int i = 0; i < game.childList.getCount(); i++) {
				mod = (Mod) game.childList.get(i);
				SysLogger.logMsg(logLevel, "\t\t" + mod.nodeName + ", ID: " + mod.nodeID + ", IconFile: " + mod.iconFileName + ", Node Type: " + mod.nodeType + ", Discriminator: " + mod.discriminator);
				for (int j = 0; j < mod.childList.getCount(); j++) {
					GameType gt = (GameType) mod.childList.get(j);
					SysLogger.logMsg(logLevel, "\t\t\t" + gt.nodeName + ", ID: " + gt.nodeID + ", Node Type: " + gt.nodeType);
				}
			}
		}
	}
	
	public void initVars() {
		game			= null;
		gameID			= null;
		gameName		= null;
		gameIcon		= null;
		serverQueryType	= null;
		authenticate	= null;
		mod				= null;
		modName			= null;
		modID			= null;
		modIcon			= null;
		modDiscrim		= "gametype";
		hasCustomNames	= false;
		team1Name		= null;
		team2Name		= null;
		gameTypeID		= null;
		gameType		= null;
		ignoreAttrib	= null;
	}
	
	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		SysLogger.logMsg(9, "Found starting element " + qName);
		if (qName.equalsIgnoreCase("games")) {
			String xmlVersion = attributes.getValue("version");
			String msg;
			if (xmlVersion == null) {
				msg = "Error - XML version not found in " + inputFile;
				SysLogger.logMsg(0, msg);
				System.exit(1);
			}
			if (!xmlVersion.equals(akmXmlVersion)) {
				msg = "Error - XML version mismatch in " + inputFile + " - expecting version " + akmXmlVersion;
				SysLogger.logMsg(0, msg);
				System.exit(1);
			}
		} else if (qName.equalsIgnoreCase("game")) {
			// initialize all game variables
			initVars();
			gameID = attributes.getValue("id");
		} else if (qName.equalsIgnoreCase("mod")) {
			modID			= attributes.getValue("id");
			modIcon			= gameIcon;
			mod				= null;
			modDiscrim		= "gametype";
			hasCustomNames	= false;
			team1Name		= null;
			team2Name		= null;
			gameTypeID		= null;
			gameType		= null;
		} else if (qName.equalsIgnoreCase("gametype")) {
			gameTypeID = attributes.getValue("id");
		} else if (qName.equalsIgnoreCase("attribute")) {
			varName = attributes.getValue("var");
		} else if (qName.equalsIgnoreCase("servertype")) {
			authenticate = attributes.getValue("authenticate");
		} else if (qName.equalsIgnoreCase("ignore")) {
			ignoreAttrib = attributes.getValue("attrib").toLowerCase();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		SysLogger.logMsg(9, "Found ending element: " + qName + " with value: " + tempVal);
		
		if (qName.equalsIgnoreCase("gamename")) {
			gameName = tempVal;
		} else if (qName.equalsIgnoreCase("gameicon")) {
			gameIcon = tempVal;
		} else if (qName.equalsIgnoreCase("servertype")) {
			serverQueryType = tempVal;
			game = new Game(gameID, gameName, gameIcon, serverQueryType);
			game.requiresLogin = ((authenticate != null) && authenticate.equalsIgnoreCase("true"));
			games.put(gameID, game);
		} else if (qName.equalsIgnoreCase("defaultport")) {
			game.defaultPort = Integer.parseInt(tempVal);
		} else if (qName.equalsIgnoreCase("attribute")) {
			if (varName.equalsIgnoreCase(Game.varEngineVersion)) {
				game.putVar(Game.varEngineVersion, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varGameType)) {
				game.putVar(Game.varGameType, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varGameName)) {
				game.putVar(Game.varGameName, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varGameVersion)) {
				game.putVar(Game.varGameVersion, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varModInfo)) {
				game.putVar(Game.varModInfo, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varServerProtocol)) {
				game.putVar(Game.varServerProtocol, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varMapName)) {
				game.putVar(Game.varMapName, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varAntiCheat)) {
				game.putVar(Game.varAntiCheat, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varNeedPassword)) {
				game.putVar(Game.varNeedPassword, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varServerName)) {
				game.putVar(Game.varServerName, tempVal);
			} else if (varName.equalsIgnoreCase(Game.varTimeLimit)) {
				game.putVar(Game.varTimeLimit, tempVal);
			}
		} else if (qName.equalsIgnoreCase("cheatprotection")) {
			game.hasCheatProtection = true;
			game.cheatProtection = tempVal;
		} else if (qName.equalsIgnoreCase("modname")) {
			modName = tempVal;
		} else if (qName.equalsIgnoreCase("modicon")) {
			modIcon = tempVal;
		} else if (qName.equalsIgnoreCase("moddiscrim")) {
			if (tempVal.equalsIgnoreCase("gametype")) {
				modDiscrim = "GAME_ID";
			} else if (tempVal.equalsIgnoreCase("gameversion")) {
				modDiscrim = "MOD_VERSION";
			}
		} else if (qName.equalsIgnoreCase("team1Name")) {
			team1Name = tempVal;
		} else if (qName.equalsIgnoreCase("team2Name")) {
			team2Name = tempVal;
		} else if (qName.equalsIgnoreCase("customnames")) {
			hasCustomNames = tempVal.equalsIgnoreCase("true") ? true : false;
		} else if (qName.equalsIgnoreCase("gametype")) {
			if (mod == null) {
				if (team1Name == null) {
					mod = game.addMod(modID, modName, modIcon, modDiscrim, hasCustomNames);
				} else {
					mod = game.addMod(modID, modName, modIcon, modDiscrim, team1Name, team2Name);
				}
			}
			gameType = tempVal;
			mod.addGameType(gameTypeID, gameType);
		} else if (qName.equalsIgnoreCase("mod")) {
			if (mod == null) {
				if (team1Name == null) {
					mod = game.addMod(modID, modName, modIcon, modDiscrim, hasCustomNames);
				} else {
					mod = game.addMod(modID, modName, modIcon, modDiscrim, team1Name, team2Name);
				}
			} else {
				mod.addGameType(Game.OTHER_INDEX, "Other");
			}
		} else if (qName.equalsIgnoreCase("game")) {
			if (!game.ignoreOthers) {
				mod = game.addMod(Mod.OTHER, "Other", game.iconFileName, Mod.GAMEID, false);
				mod.isDisplayed = true;
			}
			mod = game.addMod(Mod.UNKNOWN, "UNKNOWN", AllKnowingMind.unknownModIcon, Mod.GAMEID, false);
			mod.isDisplayed = true;
			games.put(game.nodeID, game);
		}  else if (qName.equalsIgnoreCase("ignoreothers")) {
			game.ignoreOthers = true;
		} else if (qName.equalsIgnoreCase("ignore")) {
			game.ignoreList.add(new IgnoreItem(ignoreAttrib, tempVal.toLowerCase()));
		} else {
			SysLogger.logMsg(6, "Unknown Entity: " + qName);
		}
	}
}
