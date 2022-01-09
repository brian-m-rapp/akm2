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
import java.net.*;
import java.nio.*;

import org.dilireum.logging.SysLogger;

public class Q3Server extends Server {

	private int		protocol;
	private int		privateClients;
	private String		serverArch;			// linux-i386, win32-i386, etc.
	private String		modVersion;			// optional 2nd token from gamename
	private String		modEngine;			// Only for g_version games
	private String		modEngineVersion;	// For g_version
	private int		captureLimit;
	private int		fragLimit;
	private int		timeLimit;
	private boolean	downloadAllowed;
	private boolean	botsEnabled;
	private String	queryCommand;
	private int		dmFlags;					/*	1     No Health.
													2     No Powerups.
													4     Weapons Stay.
													8     No Falling Damage.
													16    Instant Powerups.
													32    Same Map.
													64    Teams by Skin.
													128   Teams by Model.
													256   No Friendly Fire.
													512   Spawn Farthest.
													1024  Force Respawn.
													2048  No Armor.
													4096  Allow Exit.
													8192  Infinite Ammo.
													16384 Quad Drop.
													32768 Fixed FOV. */

	
//	Add common attributes to attributes, such as host name, game type, map, maxClients, pb status, etc, as well as access methods.  Get from Q3ServerBaseQ3.
//	Don't assign to a specific mod until the user queries that server.  Then create the specific game type and display all info.
	
	public Q3Server(Game game, String server, int port) {
		super(game, server, port);
		if (game.gameNetProtocol.toLowerCase().equals("q3s")) {
			queryCommand = "getstatus";
		} else { // gameNetProtocol == q3i
			queryCommand = "getinfo";
		}
		setGameMod(Mod.UNKNOWN);
	}
	
	public Q3Server(Game game, InetAddress server, int port) {
		super(game, server, port);
		if (game.gameNetProtocol.toLowerCase().equals("q3s")) {
			queryCommand = "getstatus";
		} else { // gameNetProtocol == q3i
			queryCommand = "getinfo";
		}
	}
	
	public synchronized boolean getServerStatus() {
		boolean isOK;

		if (gameMod == null) {
			setGameMod(Mod.UNKNOWN);
		}
		isOK = queryServer(queryCommand);
		
		isValid = isOK;
		//findPingTime();
		return isOK;
	}

	public synchronized boolean getQuickStatus() {
		boolean isOK;
		
		if (gameMod == null) {
			setGameMod(Mod.UNKNOWN);
		}
		isOK = queryServer(queryCommand);
		
		isValid = isOK;
		return isOK;
	}

/*
	private double stringToDouble(String numStr) {
		try {
			return Float.parseFloat(numStr);
		} catch (NumberFormatException e) {
			return (double) 0.0;
		}
	}
*/

	protected String parseRawName (String rawName) {
		
		String rn = new String();
		
		for (int i = 0; i < rawName.length(); i++) {
			if (rawName.substring(i, i+1).equals("^")) {
				if ((i+2) < rawName.length()) {
					if (rawName.substring(i+1, i+2).equals("x")) {
						if ((i+7) < rawName.length()) {
							i += 7;
						} else {
							i++;
						}
					} else {
						i++;
					}
				} else {
					i++;
				}
			} else {
				rn += rawName.substring(i, i+1);
			}
		}
		return rn;
	}

	public String saveGameMod(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			int		end;
			String	modName = str;
			String	modVersion;
			
			if ((end = str.indexOf(' ')) != -1) {
				modName = str.substring(0, end);
				modVersion = str.substring(end+1);
				setModVersion(modVersion);
			}
			setGameMod(modName);
			deleteCvar(cvar);
		} else {
			setGameMod(null);
		}
		return gameMod;
	}

/*	public String saveGameType(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setGameType(findGameType(stringToInt(str)));
			deleteCvar(cvar);
		} else {
			setGameType(findGameType(-1));
		}
		SysLogger.logMsg(5, "\tgameType set to: " + getGameType());
		return getGameType();
	}
*/
	public String saveGameID(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			gameID = str;
			deleteCvar(cvar);
		} else {
			gameID = Game.TYPE_OTHER;
		}
		SysLogger.logMsg(5, "\tgameType set to: " + gameID);
		return gameID;
	}

	public String saveEngineInfo(String cvar) {
		String	str = getCvarValue(cvar);
		int		start;
		int		end;
		
		if (str != null) {
			start = 0;
			if ((end = str.indexOf(' ', start)) != -1) {
				setGameEngine(str.substring(start, end));
				start = end + 1;
				if ((end = str.indexOf(' ', start)) != -1) {
					setEngineVersion(str.substring(start, end));
					start = end + 1;
					if ((end = str.indexOf(' ', start)) != -1) {
						setArch(str.substring(start, end));
					}
				}
			}
		} else {
			setGameEngine("N/A");
			SysLogger.logMsg(0, "OriginalGame version not specified");
		}
		return getGameEngine();
	}

	public String saveModInfo(String cvar) {
		String	str = getCvarValue(cvar);
		int		start;
		int		end;
		
		if (str != null) {
			start = 0;
			if ((end = str.indexOf(' ', start)) != -1) {
				setModEngine(str.substring(start, end));
				start = end + 1;
				if ((end = str.indexOf(' ', start)) != -1) {
					setModEngineVersion(str.substring(start, end));
				}
			}
		} else {
			setModEngine("N/A");
			setModEngineVersion("0.0");
		}
		return getModEngine();
	}

	public int saveProtocol(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setProtocol(stringToInt(str));
			deleteCvar(cvar);
		} else {
			setProtocol(0);
		}
		return getProtocol();
	}

	public int saveCaptureLimit(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setCaptureLimit(stringToInt(str));
			deleteCvar(cvar);
		} else {
			setCaptureLimit(0);
		}
		return getCaptureLimit();
	}

	public int saveFragLimit(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setFragLimit(stringToInt(str));
			deleteCvar(cvar);
		} else {
			setFragLimit(0);
		}
		return getFragLimit();
	}

	public int saveTimeLimit(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setTimeLimit(stringToInt(str));
			deleteCvar(cvar);
		} else {
			setTimeLimit(0);
		}
		return getTimeLimit();
	}

	public int saveDMFlags(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			int eos = str.indexOf(' ');
			if (eos != -1) {
				str = str.substring(0, eos);
			}
			
			setDMFlags(stringToInt(str));
			deleteCvar(cvar);
		} else {
			setDMFlags(0);
		}
		return getDMFlags();
	}

	public int savePrivateClients(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setPrivateClients(stringToInt(str));
			deleteCvar(cvar);
		} else {
			setPrivateClients(0);
		}
		return getPrivateClients();
	}
	
	public boolean savePBEnabled(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setPBEnabled(stringToInt(str) == 1);
			deleteCvar(cvar);
		} else {
			setPBEnabled(false);
		}
		return isPBEnabled();
	}
	
	public boolean saveNeedPassword(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setNeedPassword(stringToInt(str) == 1);
			deleteCvar(cvar);
		} else {
			setNeedPassword(false);
		}
		return isPasswordNeeded();
	}
	
	public boolean saveAllowDownload(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setDownloadAllowed(stringToInt(str) == 1);
			deleteCvar(cvar);
		} else {
			setDownloadAllowed(false);
		}
		return isDownloadAllowed();
	}
	
	public boolean saveBotsEnabled(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setBotsEnabled(stringToInt(str) != 0);
			deleteCvar(cvar);
		} else {
			setBotsEnabled(false);
		}
		return areBotsEnabled();
	}
	
	public String getTeamName(String cvar) {
		String str = getCvarValue(cvar);

		if (str != null) {
			//deleteCvar(cvar);
		} else {
			str = "(no name)";
		}
		return str;
	}

	public void setGameMod(String mod) {
		if (mod != null) {
			gameMod = mod.toLowerCase();
		}
		
		SysLogger.logMsg(5, "\tgameMod set to: " + gameMod);
	}
	
	public void setModVersion(String mv) {
		modVersion = mv;
	}
	
	public String getModVersion() {
		return modVersion;
	}
	
	public void setModEngine(String mEngine) {
		modEngine = mEngine;
	}
	
	public void setModEngineVersion(String mEngVer) {
		modEngineVersion = mEngVer;
	}
	
	public String getModEngine() {
		return modEngine;
	}
	
	public String getModEngineVersion() {
		return modEngineVersion;
	}

/*	public String findGameType(int gameNum) {
		String gameTypes[] = { Q3Game.Q3_FFA, Q3Game.Q3_DUEL, Q3Game.Q3_SINGLE, Q3Game.Q3_TDM, Q3Game.Q3_CTF, Game.TYPE_OTHER }; 

		if ((gameNum < 0) || (gameNum > (gameTypes.length - 2))) {
			gameNum = gameTypes.length - 1;
		}
		return gameTypes[gameNum];
	}
*/
	public void setGameEngine(String str) {
		gameEngine = str;
	}

	public String getGameEngine() {
		return gameEngine;
	}
	
	public void setEngineVersion(String str) {
		engineVersion = str;
	}
	
	public String getEngineVersion() {
		return engineVersion;
	}
	
	public void setArch(String str) {
		serverArch = str;
	}

	public String getArch()	{
		return serverArch;
	}
	
	public void setProtocol(int p) {
		protocol = p;
		SysLogger.logMsg(5, "\tgame protocol set to: " + getProtocol());
	}
	
	public int getProtocol() {
		return protocol;
	}

	public void setCaptureLimit(int cl) {
		captureLimit = cl;
		SysLogger.logMsg(5, "\tcaptureLimit set to: " + getCaptureLimit());
	}
	
	public int getCaptureLimit() {
		return captureLimit;
	}
	
	public void setFragLimit(int fl) {
		fragLimit = fl;
		SysLogger.logMsg(5, "\tfragLimit set to: " + getFragLimit());
	}
	
	public int getFragLimit() {
		return fragLimit;
	}
	
	public void setTimeLimit(int tl) {
		timeLimit = tl;
		SysLogger.logMsg(5, "\ttimeLimit set to: " + getTimeLimit());
	}
	
	public int getTimeLimit() {
		return timeLimit;
	}
	
	public void setDMFlags(int dmf) {
		dmFlags = dmf;
		SysLogger.logMsg(5, "\tdmFlags set to: " + getDMFlags());
	}
	
	public int getDMFlags() {
		return dmFlags;
	}
	
	public void setPrivateClients(int num) {
		privateClients = num;
		SysLogger.logMsg(5, "\tprivateClients set to: " + getPrivateClients());
	}
	
	public int getPrivateClients() {
		return privateClients;
	}
	
	public void setBotsEnabled(boolean bots) {
		botsEnabled = true;
		SysLogger.logMsg(5, "\tBots enabled is: " + areBotsEnabled());
	}
	
	public boolean areBotsEnabled() {
		return botsEnabled;
	}
	
	public void setDownloadAllowed(boolean dla) {
		downloadAllowed = dla;
		SysLogger.logMsg(5, "\tDownload allowed is: " + isDownloadAllowed());
	}

	public boolean isDownloadAllowed() {
		return downloadAllowed;
	}
	
	public String getModNodeID() {
		if (typeDiscriminator.equals(Mod.GAMEID)) {
			return gameID;
		} else if (typeDiscriminator.equals(Mod.MODVERSION)) {
			return getModEngineVersion();
		} else {
			return gameID;
		}
	}

	public boolean getExtendedInfo() {
		switch (protocol) {
		//TODO This won't work.  We can't guarantee that a particular protocol # will only be used for a single game.  
		// Another mechanism is needed to specify which variables will be provided and what format they will be in.
		case 9:		// Warsow
		case 10:
			getWarsowExtendedInfo();
			break;
		case 84:	// Wolf: ET
			getWetExtendedInfo();
			break;
		default:
			getQ3ExtendedInfo("Blue", "Red");
			break;	// All other Q3-based protocols
		}

		if (hasExtendedInfo && (SysLogger.getLogLevel() >= 4)) {
			int		i;
			SysLogger.logMsg(4, "=================\n" + teams[TEAM1].name + " members\n---------------");
			for (i = 0; i < teams[TEAM1].getCount(); i++) {
				SysLogger.logMsg(4, teams[TEAM1].get(i).readableName);
			}
			if (isTeamGame) {
				SysLogger.logMsg(4, teams[TEAM2].name + " members\n---------------");
				for (i = 0; i < teams[TEAM2].getCount(); i++) {
					SysLogger.logMsg(4, teams[TEAM2].get(i).readableName);
				}
			}
			SysLogger.logMsg(4, teams[SPECTATORS].name + " members\n---------------");
			for (i = 0; i < teams[SPECTATORS].getCount(); i++) {
				SysLogger.logMsg(4, teams[SPECTATORS].get(i).readableName);
			}
		}
		return isTeamGame;
	}
	
	public boolean getWetExtendedInfo() {
		
		hasExtendedInfo = false;
		isTeamGame = false;
		if (getCvarValue("P") != null) {		// W:ET Teams variable
			assignWETteams("P");
			isTeamGame = true;
		} else {
			getQ3ExtendedInfo("Axis", "Allies");	// Maybe still using Players_Axis/Players_Allies instead of P cvar.
		}
		return isTeamGame;
	}
	
	public boolean getWarsowExtendedInfo() {
		int		i;

		hasExtendedInfo = true;
		for (i = 0; i < 4; i++) {
			teams[i] = null;
		}
		
		for (i = 0; i < players.getCount(); i++) {
			Q3Player p = (Q3Player) players.get(i);
			if (p.teamNumber == 0) {
				if (teams[2] == null) teams[2] = new PlayerList("Spectators");
				teams[2].add(p);
			}
			if (p.teamNumber == 1) {
				isTeamGame = false;
				if (teams[0] == null) teams[0] = new PlayerList("Combatants");
				teams[0].add(p);
			}
			if (p.teamNumber == 2) {
				isTeamGame = true;
				if (teams[1] == null) teams[1] = new PlayerList("Red");
				teams[1].add(p);
			}
			if (p.teamNumber == 3) {
				isTeamGame = true;
				if (teams[0] == null) teams[0] = new PlayerList("Blue");
				teams[0].add(p);
			}
			if (p.teamNumber == 4) {
				isTeamGame = true;
				if (teams[0] == null) teams[0] = new PlayerList("Green");
				teams[0].add(p);
			}
			if (p.teamNumber == 5) {
				isTeamGame = true;
				if (teams[1] == null) teams[1] = new PlayerList("Yellow");
				teams[1].add(p);
			}
		}
		if (teams[0] == null) teams[0] = new PlayerList("Combatants");
		if (teams[1] == null) teams[1] = new PlayerList("");
		if (teams[2] == null) teams[2] = new PlayerList("Spectators");

		return isTeamGame;
	}

	public boolean getQ3ExtendedInfo(String team1Name, String team2Name) {
		boolean tryTeams = true;

		if ((getCvarValue("Players_" + team1Name) != null) || (getCvarValue("Players_Active") != null)) {
			saveGameClock("Score_Time");

			if (getCvarValue("Players_Active") != null) {
				isTeamGame = false;
				hasExtendedInfo = true;
				tryTeams = (assignTeam(TEAM1, "Players_Active", "Combatants") == 0);
			}

			if (tryTeams) {
				String name = getCvarValue("g_teamNameBlue"); 
				if (name != null) {
					teamNames[TEAM1] = name;
				} else {
					teamNames[TEAM1] = team1Name;
				}

				name = getCvarValue("g_teamNameRed"); 
				if (name != null) {
					teamNames[TEAM2] = name;
				} else {
					teamNames[TEAM2] = team2Name;
				}

				hasExtendedInfo = true;
				if (getCvarValue("Players_" + team1Name) != null) {
					isTeamGame = true;

					assignTeam(TEAM1, "Players_" + team1Name, teamNames[TEAM1]);
					assignTeam(TEAM2, "Players_" + team2Name, teamNames[TEAM2]);

					if ((gameClock != null) && !gameClock.equals("Waiting for Players")) {
						if (getCvarValue("Score_" + team1Name) != null) {
							hasTeamScore = true;
						}
						saveScore(TEAM1, "Score_" + team1Name);
						saveScore(TEAM2, "Score_" + team2Name);
					}
				}
			}
			assignSpecs();
		}
		return isTeamGame;
	}
	
	public String saveGameClock(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setGameClock(str);
			deleteCvar(cvar);
		}
		return gameClock;
	}
	
	public void setGameClock(String time) {
		gameClock = time;
		SysLogger.logMsg(5, "\tGame clock set to: " + gameClock);
	}

	public int saveScore(int index, String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setScore(index, Integer.parseInt(str));
			deleteCvar(cvar);
		} else {
			SysLogger.logMsg(0, "Team score not specified");
		}
		return getScore(index);
	}
	
	public void setScore(int index, int score) {
		teamScore[index] = score;
	}
	
	public int getScore(int index) {
		return teamScore[index];
	}
	
	public int assignTeam(int team, String key, String name) {
		String		members;
		int			pos;
		int			nextpos;
		boolean	done = false;
		int			index;
		int			count = 0;
		
		teams[team] = new PlayerList(name);
		members = getCvarValue(key);
		SysLogger.logMsg(6, name + " members: " + members + "\n--------------------");
		if (members != null) {
			deleteCvar(key);
			if (!members.toLowerCase().contains("none")) {
				for (pos = 0; !done; pos = nextpos + 1) {
					nextpos = members.indexOf(' ', pos);
					if (nextpos != -1) {
						index = Integer.parseInt(members.substring(pos, nextpos));
					} else {
						index = Integer.parseInt(members.substring(pos));
						done = true;
					}

					if (index <= 0) {
						SysLogger.logMsg(0, "Skipping member element with value of " + index);
					} else {
						if (index > players.getCount()) {
							SysLogger.logMsg(6, name + " members: " + members + "\n--------------------");
							logPlayerList(6);
						} else {
							count++;
							teams[team].add(players.get(index-1));
						}
					}
				}
			}
		}
		return count;
	}
	
	public void assignSpecs() {
		teams[SPECTATORS] = new PlayerList("Spectators");
		if (isTeamGame) {
			for (int i = 0; i < players.getCount(); i++) {
				Player	p = players.get(i);
				if ((teams[TEAM1].findPlayer(p) == -1) && (teams[TEAM2].findPlayer(p) == -1)) {
					teams[SPECTATORS].add(p);
				}
			}
		} else { // Not a team game
			for (int i = 0; i < players.getCount(); i++) {
				Player	p = players.get(i);
				if (teams[TEAM1].findPlayer(p) == -1) {
					teams[SPECTATORS].add(p);
				}
			}
		}
	}
	
	private void assignWETteams(String key) {
		String	slots = getCvarValue(key);

		teams[TEAM1]		= new PlayerList("Axis");
		teams[TEAM2]		= new PlayerList("Allies");
		teams[SPECTATORS]	= new PlayerList("Spectators");

		if (slots != null) {
			int		pos		= 0;
			int		pidx	= 0;

			hasExtendedInfo = true;
			isTeamGame = true;
			Player	p;
			deleteCvar(key);
			for (pos = 0; pos < slots.length(); pos++) {
				String slot = slots.substring(pos, pos+1);
				if (("0123").contains(slot)) {
					if (pidx >= players.getCount()) {
						break;
					}
					int slotNum = Integer.parseInt(slot); 
					p = players.get(pidx);
					pidx++;
					switch (slotNum) {
					case 1:		// Axis
					case 2:		// Allies
						teams[slotNum - 1].add(p);
						break;
					case 0:		// Player at slot is connecting
					case 3:		// Spectator
					default:
						teams[SPECTATORS].add(p);
					break;
					}
				}
			}
		}
	}

	public boolean parseServerInfo (String statStr) {
        int								pos;
        int								nextPos = 0;
        String							curVar;
        String							curVal;
        String							cVarStr;
        String							playerStr;
        boolean							done = false;
        String							playerLine;
        
        players = new PlayerList("All Players");
        attributes = new VarList();
        
    	SysLogger.logMsg(9, "In parseServerStatus");

        if ((pos = statStr.indexOf('\\', 0)) == -1) {
        	return false;
        }
        if ((nextPos = statStr.indexOf('\n', pos + 1)) == -1) {
        	return false;
        }

        cVarStr = statStr.substring(pos, nextPos);
        playerStr = statStr.substring(nextPos);
        
        for (pos = 0; !done; pos = nextPos) {
        	nextPos = cVarStr.indexOf('\\', pos + 1);
        	curVar = cVarStr.substring(pos + 1, nextPos);
        	pos = nextPos;
        	nextPos = cVarStr.indexOf('\\', pos + 1);
        	if (nextPos == -1) {
        		nextPos = cVarStr.length();
        		done = true;
        	}
        	curVal = cVarStr.substring(pos + 1, nextPos).trim();
        	addCvar(curVar, curVal);
        }

        for (VarEntry cv = attributes.getFirst(); cv != null; cv = attributes.getNext(cv.getKey())) {
        	SysLogger.logMsg(6, cv.getKey() + " = " + cv.getValue());
        }

		saveEngineInfo(game.getVar(Game.varEngineVersion));
		saveGameID(game.getVar(Game.varGameType));
		saveGameMod(game.getVar(Game.varGameName));
		curVal = getCvarValue(game.getVar(Game.varGameVersion)); 
		if ((curVal != null) && (curVal.toLowerCase().contains("osp"))) setGameMod("osp");	// How should an OSP server reporting as baseq3 be classified? 
		saveModInfo(game.getVar(Game.varModInfo));		// For RA3, which discriminates on mod version.
		saveProtocol(game.getVar(Game.varServerProtocol));
		saveMap(game.getVar(Game.varMapName));
		savePBEnabled(game.getVar(Game.varAntiCheat));
		saveNeedPassword(game.getVar(Game.varNeedPassword));
		saveHostName(game.getVar(Game.varServerName));
		saveMaxClients(game.getVar(Game.varMaxClients));
		saveTimeLimit(game.getVar(Game.varTimeLimit));
		//saveCaptureLimit("capturelimit");
		//saveFragLimit("fraglimit");
		//saveAllowDownload("sv_allowDownload");
		//saveBotsEnabled("bot_minplayers");
		//saveDMFlags("dmflags");
		//savePrivateClients("sv_privateClients");
		readableHostName = parseRawName(hostName);
		
        SysLogger.logMsg(5, "\nPlayers\nRaw Name\t\t\t      Name\t\t  Score\t  Ping\n--------------------------------      ----------------    -----\t  ----");

    	int playerNumber;
        switch (protocol) {
        case 9:		// Warsow protocols
        case 10:
        	int		numClients;
        	String	clientStr = getCvarValue("clients");
        	
        	if (clientStr != null) {
        		numClients = Integer.parseInt(clientStr);
        	} else {
        		SysLogger.logMsg(0, "Clients not defined for protocol 10 server " + address.getHostAddress() + ":" + port);
        		return false;
        	}

        	for (playerNumber = 0, pos = 0; playerNumber < numClients; playerNumber++, pos = nextPos) {
	        	Q3Player player;
	        	
	        	nextPos = playerStr.indexOf('\n', pos + 1);
	        	if (nextPos != -1) {
	        		playerLine = playerStr.substring(pos+1, nextPos);
	        	} else {
	        		playerLine = playerStr.substring(pos+1);
	        	}
	        	// Put these into a collection as well
	
	        	if (SysLogger.getLogLevel() >= 9) {
	        		String tmpStr = "(" + playerLine.length() + ")";
	        		int x;
	        		byte[]	lineBuf = playerLine.getBytes();
	        		for (x = 0; x < lineBuf.length; x++) {
	        			tmpStr += Integer.toHexString(lineBuf[x]) + " ";
	        		}
	
	        		SysLogger.logMsg(9, tmpStr);
	        	}
	
	        	if (playerLine.substring(0, 1).getBytes()[0] != 0) {
	        		int		start	= 0;
	        		int		end		= playerLine.indexOf(' ', start);
	        		int		score	= Integer.parseInt(playerLine.substring(start, end));
	        		int		ping	= 0;
	        		String	playerName;
	        		int		teamNumber = -1;
	
	        		start = end + 1;
	        		end = playerLine.indexOf(' ', start);
	        		ping = Integer.parseInt(playerLine.substring(start, end));
	        		start = end + 2;
	        		end = playerLine.indexOf('\"', start);
	        		playerName = playerLine.substring(start, end).trim();
	        		start = end + 2;
	        		teamNumber = Integer.parseInt(playerLine.substring(start, playerLine.length()));  // Warsow gives teamNumber, q3 and others don't
	        		//SysLogger.logMsg(8, String.format("%-20s % 4d\t% 4d\t%d", playerName, score, ping, teamNumber));
	        		player = new Q3Player(playerName, playerNumber+1, score, ping, teamNumber);
	        		players.add(player);
	        		//SysLogger.logMsg(5, String.format("%-38s%-20s% 5d\t  % 4d\t %d", player.playerName, player.readableName, player.score, player.pingTime, player.teamNumber));
	        	}
        	}
	
        	break;
        default:	// All remaining Q3 protocols        
	        for (playerNumber = 1, pos = 0; nextPos != -1; pos = nextPos, playerNumber++) {
	        	Q3Player player;
	
	        	nextPos = playerStr.indexOf('\n', pos + 1);
	        	if (nextPos != -1) {
	        		playerLine = playerStr.substring(pos+1, nextPos);
	        	} else {
	        		playerLine = playerStr.substring(pos+1);
	        	}
	
	        	if (SysLogger.getLogLevel() >= 9) {
	        		String tmpStr = "(" + playerLine.length() + ")";
	        		int x;
	        		byte[]	lineBuf = playerLine.getBytes();
	        		for (x = 0; x < lineBuf.length; x++) {
	        			tmpStr += Integer.toHexString(lineBuf[x]) + " ";
	        		}
	
	        		SysLogger.logMsg(9, tmpStr);
	        	}
	
	        	if (playerLine.substring(0, 1).getBytes()[0] != 0) {
	        		int		start	= 0;
	        		int		end		= playerLine.indexOf(' ', start);
	        		int		score;
	        		int		ping	= 0;
	        		String	playerName;
	
	        		try {
						score = Integer.parseInt(playerLine.substring(start, end));
					} catch (NumberFormatException e) {
						score = -1;
					}
	        		start = end + 1;
	        		end = playerLine.indexOf(' ', start);
	        		ping = Integer.parseInt(playerLine.substring(start, end));
	        		start = end + 2;
	        		end = playerLine.indexOf('\"', start);
	        		playerName = playerLine.substring(start, end).trim();
	        		//SysLogger.logMsg(8, String.format("%-20s % 4d\t% 4d", playerName, score, ping));
	        		player = new Q3Player(playerName, playerNumber, score, ping, -1);
	        		players.add(player);
	        		//SysLogger.logMsg(5, String.format("%-38s%-20s % 4d\t  % 4d", player.playerName, player.readableName, player.score, player.pingTime));
	        	}
	        }
        break;
        }
        logPlayerList(6);
        getExtendedInfo();

        return true;
	}

	public void logPlayerList(int level) {
		if (SysLogger.getLogLevel() >= level) {
			for (int i = 0; i < players.getCount(); i++) {
				Q3Player p = (Q3Player) players.get(i);
				if ((protocol == 9) || (protocol == 10)) {
					SysLogger.logMsg(level, String.format("%-38s%-20s % 4d\t  % 4d", p.playerName, p.readableName, p.score, p.pingTime, p.teamNumber));
				} else {
					SysLogger.logMsg(level, String.format("%-38s%-20s % 4d\t  % 4d", p.playerName, p.readableName, p.score, p.pingTime));
				}
			}
		}
	}
	
	public boolean queryServer(String command) {
		final int		SOCKET_TIMEOUT_MSECS = 2000;
        final int		header = 0xFFFFFFFF;
    	ByteBuffer		outBuf = ByteBuffer.allocate(256);
    	byte[]			buf = new byte[10000];
    	DatagramSocket	socket;
    	DatagramPacket	inPacket;
    	DatagramPacket	outPacket;
    	boolean			packetReceived = false;
    	int				retryCount = 0;
    	long			startTime;
    	long			returnTime;
    	
    	SysLogger.logMsg(9, "In queryServer");
    	
    	// Construct getstatus command
        outBuf.clear();
        outBuf.putInt(header).put(command.getBytes());
        outBuf.flip();
        
        try {
        	socket = new DatagramSocket();
        } catch (SocketException e) {
        	SysLogger.logMsg(0, "Error creating socket: " + e.getMessage());
        	return (false);
        }
        
        try {
        	socket.setSoTimeout(SOCKET_TIMEOUT_MSECS);
        } catch (SocketException se) {
        	socket.close();
        	SysLogger.logMsg(0, "Unable to set socket option SO_TIMEOUT");
        	return (false);
        }

        // Construct datagram and send to server
    	outPacket = new DatagramPacket(outBuf.array(), outBuf.array().length, address, port);
    	inPacket = new DatagramPacket(buf, buf.length);
    	SysLogger.logMsg(7, "Querying " + address.getHostAddress() + ":" + port);
    	
        while (!packetReceived && (retryCount < AllKnowingMind.maxRetries)) {
        	try {
        		socket.send(outPacket);
        		startTime = System.currentTimeMillis();

        	} catch (IOException e) {
        		SysLogger.logMsg(0, "Error writing to socket: " + e.getMessage());
        		socket.close();
        		return (false);
        	}

        	try {
       			socket.receive(inPacket);
       			returnTime = System.currentTimeMillis();
       			pingTime = (int) (returnTime - startTime);
        		packetReceived = true;
/*        		if (SysLogger.getLogLevel() >= 9) {
        			String tStr = "";

        			for (int i = 0; i < buf.length; i++) {
        				tStr += String.format("%d:%x ", i, buf[i]);
        			}

        			SysLogger.logMsg(9, tStr);
        		}
*/
        	} catch (SocketTimeoutException e) {
        		retryCount++;
        	} catch (IOException e) {
        		SysLogger.logMsg(0, "Error reading from socket " + e.getMessage());
    			retryCount++;
        	}
        }
        
        if (retryCount >= AllKnowingMind.maxRetries) {
        	SysLogger.logMsg(6, "No response from " + address.getHostAddress() + ":" + port);
        	socket.close();
        	return (false);
        }

        String received = new String(inPacket.getData(), 4, inPacket.getLength());
       	SysLogger.logMsg(9, "Received buffer: (" + received + ")");
        socket.close();
       	return parseServerInfo(received);
	}
}
