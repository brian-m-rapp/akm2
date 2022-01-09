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

import java.io.File;
import java.io.FileFilter;

import org.dilireum.logging.SysLogger;
import org.eclipse.swt.widgets.Shell;

public class Game extends GameNode implements Cloneable {

	public static final String 		UNKNOWN		= "Unknown";
	public static final String		TYPE_OTHER	= "Other";
	public static final String		DISCRIM_MOD	= "mod";
	public static final String		DISCRIM_ENG	= "engine";

	public static final String		varEngineVersion	= "engineversion";
	public static final String		varGameType			= "gametype";
	public static final String		varGameName			= "gamename";
	public static final String		varGameVersion		= "gameversion";
	public static final String		varModInfo			= "modversion";
	public static final String		varServerProtocol	= "protocol";
	public static final String		varMapName			= "mapname";
	public static final String		varAntiCheat		= "cheatprotection";
	public static final String		varNeedPassword		= "needpassword";
	public static final String		varServerName		= "servername";
	public static final String		varTimeLimit		= "timelimit";
	public static final String		varMaxClients		= "maxclients";

//	private static final int		MAX_SERVER_QUERY_THREADS	= 100;
	public static final String		OTHER_INDEX					= "other";

	public String					gameNetProtocol;	// e.g. - q3s, q3i, gs, gs2
	public int						defaultPort;
	public GameList					folders;
	public boolean					cancelAction;
	public boolean					multiEngine;
	public VarList					startCommand;
	public VarList					workingDir;
	public VarList					startParms;
	public String					defaultModParms;
	public MasterServerList			masterList;
	public boolean					hasCheatProtection;
	public String					cheatProtection;
	public VarList					gameVars;
	public boolean					requiresLogin;
	public String					loginID;
	public String					loginPassword;
	public boolean					ignoreOthers;		// Ignore servers for unspecified mods
	public IgnoreList				ignoreList;
	
	public Game(String shortName, String longName, String iconFile, String queryType) {
		super(GAME_NODE, (GameNode) null, shortName, longName, iconFile);
		masterList			= new MasterServerList();
		gameNetProtocol		= queryType;
		cancelAction		= false;
		isDisplayed			= false;
		hasCheatProtection	= false;
		cheatProtection		= null;
		folders				= new GameList();
		gameVars			= new VarList();
		requiresLogin		= false;
		loginID				= null;
		loginPassword		= null;
		setVarDefaults();
		ignoreOthers		= false;
		ignoreList			= new IgnoreList();
		multiEngine			= false;
		startCommand		= new VarList();
		workingDir			= new VarList();
		startParms			= new VarList();
	}

	public Game copyGame() {
		Game newGame 			= clone();
		newGame.childList 		= new GameList();
		newGame.ignoreList		= new IgnoreList();
		newGame.startCommand	= new VarList();
		newGame.workingDir		= new VarList();
		newGame.startParms		= new VarList();
		newGame.masterList		= new MasterServerList();
		
		for (int i = 0; i < masterList.getCount(); i++) {
			MasterServer ms = masterList.get(i);
			MasterServer nms = ms.clone();
			nms.setGame(newGame);
			newGame.masterList.add(nms);
		}
		for (VarEntry var = startCommand.getFirst(); var != null; var = startCommand.getNext(var.getKey())) {
			newGame.startCommand.put(var.getKey(), new VarEntry(var.getKey(),var.getValue()));
		}
		for (VarEntry var = workingDir.getFirst(); var != null; var = workingDir.getNext(var.getKey())) {
			newGame.workingDir.put(var.getKey(), new VarEntry(var.getKey(),var.getValue()));
		}
		for (VarEntry var = startParms.getFirst(); var != null; var = startParms.getNext(var.getKey())) {
			newGame.startParms.put(var.getKey(), new VarEntry(var.getKey(),var.getValue()));
		}
		for (int i = 0; i < ignoreList.getCount(); i++) {
			newGame.ignoreList.add(new IgnoreItem(ignoreList.get(i).getType(), ignoreList.get(i).getValue()));
		}
		for (int i = 0; i < childList.getCount(); i++) {
			Mod mod = (Mod) childList.get(i);
			Mod newMod = mod.copyMod();
			newMod.parent = newGame;
			newGame.childList.add(newMod);
		}

		return newGame;	
	}
	
    protected Game clone() {
        try {
            return (Game) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen
            throw new InternalError(e.toString());
        }
    }

	public void putVar(String key, String value) {
    	gameVars.put(key.toLowerCase(), new VarEntry(key.toLowerCase(), value));
	}
	
	public String getVar(String key) {
		VarEntry val = gameVars.get(key.toLowerCase());
		if (val != null) {
			return val.getValue();
		} else {
			return null;
		}
	}
	
	public void deleteCvar(String key) {
		gameVars.remove(key.toLowerCase());
	}

	private void setVarDefaults() {
		if (gameNetProtocol.equalsIgnoreCase("q3s") || gameNetProtocol.equalsIgnoreCase("q3i")) {
			putVar(Game.varEngineVersion,	"version");
			putVar(Game.varGameType,		"g_gametype");
			putVar(Game.varGameName,		"gamename");
			putVar(Game.varGameVersion,		"gameversion");		// For OSP, it's like OSP v1.03a
			putVar(Game.varModInfo,			"g_version");		// For mods that discriminate on the game version like RA3
			putVar(Game.varServerProtocol,	"protocol");
			putVar(Game.varMapName,			"mapname");
			putVar(Game.varAntiCheat,		"sv_punkbuster");
			putVar(Game.varNeedPassword,	"g_needpass");
			putVar(Game.varServerName,		"sv_hostname");
			putVar(Game.varMaxClients,		"sv_maxclients");
			putVar(Game.varTimeLimit,		"timelimit");
		} else if (gameNetProtocol.equalsIgnoreCase("etqw")) {
			putVar(Game.varEngineVersion,	"si_version");
			putVar(Game.varGameType,		"si_rules");
			putVar(Game.varGameName,		"fs_game");
			putVar(Game.varGameVersion,		"gameversion");
			putVar(Game.varModInfo,			"g_version");
			putVar(Game.varServerProtocol,	"protocol");
			putVar(Game.varMapName,			"si_map");
			putVar(Game.varAntiCheat,		"net_serverPunkbusterEnabled");
			putVar(Game.varNeedPassword,	"si_needpass");
			putVar(Game.varServerName,		"si_name");
			putVar(Game.varMaxClients,		"si_maxplayers");
			putVar(Game.varTimeLimit,		"timelimit");
		} else {
			SysLogger.logMsg(0, "Unknown server type " + gameNetProtocol);
			System.exit(1);
		}
	}

	public int getServerCount() {
		return serverList.getCount();
	}

	public void setPlayerCount(int count) {
		playerCount = count;
	}
	
	public int getPlayerCount() {
		return playerCount;
	}
	
	public int countGamePlayers() {
		playerCount = 0;
		for (int i = 0; i < serverList.getCount(); i++) {
			playerCount += serverList.get(i).players.getCount();
		}
		return playerCount;
	}
	
	public void queryMasterServer(ServerList serverList) {
		for (int i = 0; i < masterList.getCount(); i++) {
			(masterList.get(i)).queryMasterServer(serverList);
		}
   		SysLogger.logMsg(2, "Found " + serverList.getCount() + " servers");
	}

	public MapImageList loadMapImages(Shell shell, MapImageList list, String path) {
		File[] files;

	    FileFilter filter = new FileFilter() {
	        public boolean accept(File file) {
	            return file.getName().endsWith(".jpg") || file.getName().endsWith(".gif");
	        }
	    };

	    path = path + File.separator + nodeID;		// eg - for Quake 3, this would be images/maps/q3a 

	    File dir = new File(path);
	    if (!dir.exists()) dir.mkdirs();
	    files = (new File(path)).listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			String imageName	= files[i].getName();
			String fullPath;
			SysLogger.logMsg(7, "File: " + imageName);
			String map = imageName.substring(0, imageName.indexOf('.')).toLowerCase();
			fullPath = path + File.separator + imageName;
			SysLogger.logMsg(7,"Map: " + map + " File: " + fullPath);
			if (!list.containsKey(map)) {
				list.put(map, fullPath);
			}
		}
		return list;
	}
	
	public void clearServerLists() {
		serverList = new ServerList();
		playerCount = 0;
		for (int i = 0; i < childList.getCount(); i++) {
			Mod mod = (Mod) childList.get(i);
			mod.serverList = new ServerList();
			mod.playerCount = 0;
			for (int j = 0; j < mod.childList.getCount(); j++) {
				GameType type = (GameType) mod.childList.get(j);
				type.serverList = new ServerList();
				type.playerCount = 0;
			}
		}
	}

	public Mod addMod(String modID, String modName, String icon, String discriminator, boolean customTeamNames) {
		Mod mod = new Mod(this, modID, modName, icon, discriminator, customTeamNames);
		childList.add(mod);
		return mod;
	}

	public Mod addMod(String modID, String modName, String icon, String discriminator, String team1, String team2) {
		Mod mod = new Mod(this, modID, modName, icon, discriminator, team1, team2);
		childList.add(mod);
		return mod;
	}
	
	public Folder addFolder(String folderName, String icon) {
		Folder folder = new Folder(this, folderName, icon);
		folders.add(folder);
		return folder;
	}

	private class QueryServerThread implements Runnable {
		
		private Server		server;

		public QueryServerThread(Server svr) {
			server = svr;
		}
		
		public void run() {
    		server.getServerStatus();
		}
	}

	private boolean isIgnored(Server s) {
		int			i;
		String		attrib;
		String		value;
		
		for (i = 0; i < ignoreList.getCount(); i++) {
			attrib	= ignoreList.get(i).getType();
			value	= ignoreList.get(i).getValue();
			if (attrib.equals("mod")) {
				if (value.equals(s.gameMod)) return true;
			} else if (attrib.equals("engine")) {
				if (value.equalsIgnoreCase(s.gameEngine)) return true;
			}
		}
		return false;
	}

	public void queryServerList(ServerList list) {
		Thread[]	serverThreads = new Thread[AllKnowingMind.maxConcurrency]; 
		boolean		slotFound;
		int			i;
		int			slot = 0;
		
		for (list.counter = 0; ((list.counter < list.getCount()) && !cancelAction); list.counter++) {
    		// find a thread that's not active
    		slotFound = false;
    		while (!slotFound) {
    			for (slot = 0; (slot < AllKnowingMind.maxConcurrency) && !slotFound; slot++) {
    				if ((serverThreads[slot] == null) || !serverThreads[slot].isAlive()) {
    					serverThreads[slot] = new Thread(new QueryServerThread(list.get(list.counter)));
    					serverThreads[slot].start();
    					slotFound = true;
    				}
    			}
    			
    			if (!slotFound) {
    				try {
    					Thread.sleep(100);
    				} catch (InterruptedException e) {
    					SysLogger.logMsg(4, "Query thread interrupted");
    				}
				}
    		}
    	}
    	
		boolean done = false;
		while (!done) {
			done = true;
			for (i = 0; i < slot; i++) {
				if (serverThreads[i].isAlive()) {
					done = false;
					break;
				}
			}

			if (!done) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					SysLogger.logMsg(4, "Thread interrupted");
				}
			}
		}

		for (i = 0; i < list.getCount(); i++) {
			Server		server = list.get(i);
			Mod			mod;
			GameType	type;

			playerCount += server.players.getCount();
			if (server.gameMod == null) {
				mod = (Mod) childList.findGameNode(Mod.UNKNOWN);
			} else {
				mod = (Mod) childList.findGameNode(server.gameMod);
				if (isIgnored(server)) {
					list.remove(i--);
					continue;
				}
				if ((mod == null) || ((mod != null) && !mod.isDisplayed)) {
					// Remove this server if a defined mod hasn't been found and the ignoreOthers flag is set.
					if (ignoreOthers) {
						list.remove(i--);
						continue;
					} else {
						mod = (Mod) childList.findGameNode(Mod.OTHER);
					}
				}
			}

			server.mod = mod;
			// TODO - This is broken.  mod and/or game type can come back as null.  Fix it to prevent crashing on display.  Also need
			// to fix updateServerNode below.  It has the same problem.
			
			if (mod != null) {		// mod will be null here if this game doesn't have an "Other" mod
				mod.addServer(server);
				server.gameIconFile = mod.iconFileName;
				mod.playerCount += server.players.getCount();

				if ((type = (GameType) mod.childList.findGameNode(server.getModNodeID())) == null) {
					type = (GameType) mod.childList.findGameNode(OTHER_INDEX);
					if (type == null) {
						SysLogger.logMsg(5, "Game type is null for " + server.address.getHostAddress() + ":" + server.port + ", Mod: " + mod.nodeName + ", Type: " + server.getModNodeID() + ", OTHER_INDEX = " + OTHER_INDEX);
					}
				}
				
				if (type != null) {	// type will be null here if this mod doesn't have an "Other" game type
					server.setGameType(type.nodeName);
					type.addServer(server);
					type.playerCount += server.players.getCount();
				} else {
					server.setGameType(server.getModNodeID());
					if (server.getGameType() == null) {
						server.setGameType("(none)");
					}
				}
			} else { // mod == null
				server.gameIconFile = iconFileName;

				if ((type = (GameType) childList.findGameNode(server.getModNodeID())) == null) {
					type = (GameType) childList.findGameNode(OTHER_INDEX);
					if (type == null) {
						SysLogger.logMsg(3, "Game type is null for " + server.address.getHostAddress() + ":" + server.port + ", Type: " + server.getModNodeID() + ", OTHER_INDEX = " + OTHER_INDEX);
					}
				}
				
				if (type != null) {	// type will be null here if this game doesn't have an "Other" game type
					server.setGameType(type.nodeName);
					type.addServer(server);
					type.playerCount += server.players.getCount();
				} else {
					//server.gameMod = nodeName;
					server.setGameType(server.getModNodeID());
					if (server.getGameType() == null) {
						server.setGameType("(none)");
					}
				}
			}

//			SysLogger.logMsg(4, String.format("%-15s %-60s % 5d  %2d/%2d  %-24s  %-6s  %-6s  %-8s  %s:%d",
//					newServer.getGameMod(), newServer.getHostName(), newServer.pingTime, newServer.getPlayerCount(), newServer.getMaxClients(), newServer.getMap(), 
//					newServer.getGameType(), newServer.getGameEngine(), newServer.getEngineVersion(), newServer.address.getHostAddress(), newServer.port));

		}
		if (cancelAction) cancelAction = false;
	}
	
	public void updateServerNode(GameNode node) {
		Thread[]	serverThreads = new Thread[AllKnowingMind.maxConcurrency]; 
		boolean		slotFound;
		int			i;
		int			j;
		int			slot = 0;
		boolean done = false;
		ServerList	origList = node.getServerList();
		GameNode	gameNode;

		if (node.nodeType.equals(GameNode.GAME_NODE)) {
			gameNode = node;
		} else if (node.nodeType.equals(GameNode.MOD_NODE) || node.nodeType.equals(GameNode.FOLDER_NODE)) {
			gameNode = node.parent;
		} else {	// This is a type node 
			gameNode = node.parent.parent;
		}

		for (origList.counter = 0; ((origList.counter < origList.getCount()) && !cancelAction); origList.counter++) {
    		// find a thread that's not active
    		slotFound = false;
    		while (!slotFound) {
    			for (slot = 0; (slot < AllKnowingMind.maxConcurrency) && !slotFound; slot++) {
    				if ((serverThreads[slot] == null) || !serverThreads[slot].isAlive()) {
    					serverThreads[slot] = new Thread(new QueryServerThread(origList.get(origList.counter)));
    					serverThreads[slot].start();
    					slotFound = true;
    				}
    			}
    			
    			if (!slotFound) {
    				try {
    					Thread.sleep(50);
    				} catch (InterruptedException e) {
    					SysLogger.logMsg(4, "Query thread interrupted");
    				}
				}
    		}
    	}
    	
		while (!done) {
			done = true;
			for (i = 0; i < slot; i++) {
				if (serverThreads[i].isAlive()) {
					done = false;
					break;
				}
			}

			if (!done) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					SysLogger.logMsg(4, "Thread interrupted");
				}
			}
		}

		if (node.nodeType.equals(GameNode.GAME_NODE)) { // create new mod and gametype serverlists.
			for (i = 0; i < node.childList.getCount(); i++) {
				GameNode mNode = node.childList.get(i);
				mNode.setServerList(new ServerList());
				for (j = 0; j < mNode.childList.getCount(); j++) {
					GameNode tNode = mNode.childList.get(j);
					tNode.setServerList(new ServerList());
				}
			}
		} else if (node.nodeType.equals(GameNode.MOD_NODE)) { // create a new server list for this mod and all gametypes under it.
			node.setServerList(new ServerList());
			for (j = 0; j < node.childList.getCount(); j++) {
				GameNode tNode = node.childList.get(j);
				tNode.setServerList(new ServerList());
			}
		} else if (node.nodeType.equals(GameNode.TYPE_NODE)) { // this is a game type node, create a new server list for this game type.
			node.setServerList(new ServerList());
		}

		for (i = 0; i < origList.getCount(); i++) {
			Server	server = origList.get(i);
			Mod			mod;
			GameType	type;

			if ((server.gameMod == null) || (server.gameMod == Mod.UNKNOWN)) {
				mod = (Mod) childList.findGameNode(Mod.UNKNOWN);
				server.gameMod = server.gameMod = Mod.UNKNOWN;
			} else {
				mod = (Mod) childList.findGameNode(server.gameMod);
				if (((mod == null) || ((mod != null) && !mod.isDisplayed)) && !node.nodeType.equals(GameNode.FOLDER_NODE)) {
					// Remove this server if a defined mod hasn't been found and the ignoreOthers flag is set.
					if (ignoreOthers || isIgnored(server)) {
						origList.remove(i--);
						// if this is a mod or a game type, remove the server from the parent server lists as well
						if (node.nodeType.equals(GameNode.MOD_NODE)) {
							node.parent.serverList.remove(server);
						} else if (node.nodeType.equals(GameNode.TYPE_NODE)) {
							node.parent.serverList.remove(server);
							node.parent.parent.serverList.remove(server);
						}
						continue;
					} else {
						mod = (Mod) childList.findGameNode(Mod.OTHER);
					} 
				} else {
					server.mod = mod;
					server.gameIconFile = mod.iconFileName;
				}
			}

			if (!node.nodeType.equals(GameNode.FOLDER_NODE)) {
				// Should only do this for refreshes, not for updates.  Updates need to move servers between serverlists as needed.
				mod.addServer(server);
				server.gameIconFile = mod.iconFileName;

				type = (GameType) mod.childList.findGameNode(server.getModNodeID());
				if (type == null) {
					type = (GameType) mod.childList.findGameNode(OTHER_INDEX);
				}

				if (type != null) {
					server.setGameType(type.nodeName);
					type.addServer(server);
				}

				//	SysLogger.logMsg(4, String.format("%-15s %-60s % 5d  %2d/%2d  %-24s  %-6s  %-6s  %-8s  %s:%d",
				//		newServer.getGameMod(), newServer.getHostName(), newServer.pingTime, newServer.getPlayerCount(), newServer.getMaxClients(), newServer.getMap(), 
				//		newServer.getGameType(), newServer.getGameEngine(), newServer.getEngineVersion(), newServer.address.getHostAddress(), newServer.port));
			}
		}
		
		gameNode.countGameNodePlayers();
		for (i = 0; i < gameNode.childList.getCount(); i++) {
			GameNode mNode = gameNode.childList.get(i);
			mNode.countGameNodePlayers();
			for (j = 0; j < mNode.childList.getCount(); j++) {
				GameNode tNode = mNode.childList.get(j);
				tNode.countGameNodePlayers();
			}
		}
		if (cancelAction) cancelAction = false;
	}

	public void insertExistingServer(Server server) {
		Mod			mod;
		GameType	type;

		if (server.gameMod == null) {
			mod = (Mod) childList.findGameNode(Mod.UNKNOWN);
		} else {
			mod = (Mod) childList.findGameNode(server.gameMod);
			if ((mod == null) || (!ignoreOthers && !isIgnored(server))) {
				mod = (Mod) childList.findGameNode(Mod.OTHER);
			}
		}
		mod.addServer(server);
		type = (GameType) mod.childList.findGameNode(server.gameID);
		if (type == null) {
			type = (GameType) mod.childList.findGameNode(OTHER_INDEX);
		}

		type	= (GameType) mod.childList.findGameNode(server.gameID);
		type.addServer(server);
	}

	private class PingServerThread implements Runnable {
		
		private Server		server;

		public PingServerThread(Server svr) {
			server = svr;
		}
		
		public void run() {
    		server.findPingTime();
		}
	}

	public void pingServerNode(GameNode node) {
		Thread[]	serverThreads = new Thread[AllKnowingMind.maxConcurrency]; 
		boolean		slotFound;
		int			i;
		int			slot = 0;
		boolean done = false;
		ServerList	origList = node.getServerList();

		for (origList.counter = 0; ((origList.counter < origList.getCount()) && !cancelAction); origList.counter++) {
    		// find a thread that's not active
    		slotFound = false;
    		while (!slotFound) {
    			for (slot = 0; (slot < AllKnowingMind.maxConcurrency) && !slotFound; slot++) {
    				if ((serverThreads[slot] == null) || !serverThreads[slot].isAlive()) {
    					serverThreads[slot] = new Thread(new PingServerThread(origList.get(origList.counter)));
    					serverThreads[slot].start();
    					slotFound = true;
    				}
    			}
    			
    			if (!slotFound) {
    				try {
    					Thread.sleep(50);
    				} catch (InterruptedException e) {
    					SysLogger.logMsg(4, "Ping thread interrupted");
    				}
				}
    		}
    	}
    	
		while (!done) {
			done = true;
			for (i = 0; i < slot; i++) {
				if (serverThreads[i].isAlive()) {
					done = false;
					break;
				}
			}

			if (!done) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					SysLogger.logMsg(4, "Thread interrupted");
				}
			}
		}
		if (cancelAction) cancelAction = false;
	}

	private class UpdateServerThread implements Runnable {
		
		private Server		server;

		public UpdateServerThread(Server svr) {
			server = svr;
		}
		
		public void run() {
    		server.getQuickStatus();		// Quick because it doesn't ping.  Need to write my own ping class.
		}
	}

	public void updateServerList(ServerList origList) {
		Thread[]	serverThreads = new Thread[AllKnowingMind.maxConcurrency]; 
		boolean		slotFound;
		int			i;
		int			slot = 0;
		boolean done = false;

		for (origList.counter = 0; ((origList.counter < origList.getCount()) && !cancelAction); origList.counter++) {
    		// find a thread that's not active
    		slotFound = false;
    		while (!slotFound) {
    			for (slot = 0; (slot < AllKnowingMind.maxConcurrency) && !slotFound; slot++) {
    				if ((serverThreads[slot] == null) || !serverThreads[slot].isAlive()) {
    					serverThreads[slot] = new Thread(new UpdateServerThread(origList.get(origList.counter)));
    					serverThreads[slot].start();
    					slotFound = true;
    				}
    			}
    			
    			if (!slotFound) {
    				try {
    					Thread.sleep(50);
    				} catch (InterruptedException e) {
    					SysLogger.logMsg(4, "Query thread interrupted");
    				}
				}
    		}
    	}
    	
		while (!done) {
			done = true;
			for (i = 0; i < slot; i++) {
				if (serverThreads[i].isAlive()) {
					done = false;
					break;
				}
			}

			if (!done) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					SysLogger.logMsg(4, "Thread interrupted");
				}
			}
		}
		
		// Try to assign a mod and mod-specific icon to each server if a mod isn't currently assigned
		for (i = 0; i < origList.getCount(); i++) {
			Server	s = origList.get(i);
			if (s.mod == null) {
				if ((s.mod = (Mod) childList.findGameNode(s.gameMod)) != null) {
					s.gameIconFile = s.mod.iconFileName;
				}
			}
		}
		
		if (cancelAction) cancelAction = false;
	}
	
	public void pingServerList(ServerList origList) {
		Thread[]	serverThreads = new Thread[AllKnowingMind.maxConcurrency]; 
		boolean		slotFound;
		int			i;
		int			slot = 0;
		boolean done = false;

		for (origList.counter = 0; ((origList.counter < origList.getCount()) && !cancelAction); origList.counter++) {
    		// find a thread that's not active
    		slotFound = false;
    		while (!slotFound) {
    			for (slot = 0; (slot < AllKnowingMind.maxConcurrency) && !slotFound; slot++) {
    				if ((serverThreads[slot] == null) || !serverThreads[slot].isAlive()) {
    					serverThreads[slot] = new Thread(new PingServerThread(origList.get(origList.counter)));
    					serverThreads[slot].start();
    					slotFound = true;
    				}
    			}
    			
    			if (!slotFound) {
    				try {
    					Thread.sleep(50);
    				} catch (InterruptedException e) {
    					SysLogger.logMsg(4, "Ping thread interrupted");
    				}
				}
    		}
    	}
    	
		while (!done) {
			done = true;
			for (i = 0; i < slot; i++) {
				if (serverThreads[i].isAlive()) {
					done = false;
					break;
				}
			}

			if (!done) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					SysLogger.logMsg(4, "Thread interrupted");
				}
			}
		}
		if (cancelAction) cancelAction = false;
	}
}
