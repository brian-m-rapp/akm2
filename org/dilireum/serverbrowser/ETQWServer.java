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

import java.net.*;
import java.io.*;
import java.nio.*;

import org.dilireum.logging.SysLogger;

public class ETQWServer extends Server {
	public int			osMask;
	public boolean		isRanked;
	public int			timeLeft;		// in milliseconds
	public int			timeLimit;
	public byte			gameState;
	private int			privateClients;
	public String		serverArch;			// linux-i386, win32-i386, etc.
	public String		modVersion;			// optional 2nd token from gamename
	public String		modEngine;			// Only for g_version games
	private String		modEngineVersion;	// For g_version


	public ETQWServer(Game game, String server, int port) {
		super(game, server, port);
		gameMod = Mod.UNKNOWN;
	}

	public ETQWServer(Game game, InetAddress server, int port) {
		super(game, server, port);
	}
	
	public String getModNodeID() {
		if (typeDiscriminator.equals(Mod.GAMEID)) {
			return gameID;
		} else if (typeDiscriminator.equals(Mod.MODVERSION)) {
			return modEngineVersion;
		} else {
			return gameID;
		}
	}

	public void setGameMod(String mod) {
		if (mod != null) {
			gameMod = mod.toLowerCase();
		}
		
		SysLogger.logMsg(5, "\tgameMod set to: " + gameMod);
	}
	
	public synchronized boolean getServerStatus() {
		boolean isOK;
		
		if (gameMod == null) {
			setGameMod(Mod.UNKNOWN);
		}
		isOK = queryServer();
		
		isValid = isOK;
		//findPingTime();
		return isOK;
	}

	public synchronized boolean getQuickStatus() {
		boolean isOK;
		
		if (gameMod == null) {
			setGameMod(Mod.UNKNOWN);
		}
		isOK = queryServer();
		
		isValid = isOK;
		return isOK;
	}
	
	private int findNextNull(byte[] buf, int start, int length) {
		for (int i = start; i < length; i++) {
			if (buf[i] == 0) return i;
		}
		return -1;
	}
	
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

	//Move this and all other standard entities to the Server Super class.  Override where necessary per the game type
	public String saveHostName(String cvar) {
		String str = getCvarValue(cvar);

		if (str != null) {
			hostName = str;
			deleteCvar(cvar);
		} else {
			hostName = "(no name)";
		}
		return hostName;
	}

	public String saveEngineInfo(String cvar) {
		String	str = getCvarValue(cvar);
		int		start;
		int		end;
		
		if (str != null) {
			start = 0;
			if ((end = str.indexOf(' ', start)) != -1) {
				gameEngine = str.substring(start, end);
				start = end + 1;
				if ((end = str.indexOf(' ', start)) != -1) {
					engineVersion = str.substring(start, end);
					start = end + 1;
					if ((end = str.indexOf(' ', start)) != -1) {
						serverArch = str.substring(start, end);
					}
				}
			}
		} else {
			gameEngine = "N/A";
			SysLogger.logMsg(0, "OriginalGame version not specified");
		}
		return gameEngine;
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
				this.modVersion = modVersion;
			}
			gameMod = modName;
			deleteCvar(cvar);
		} else {
			gameMod = "base";
		}
		return gameMod;
	}

	public String saveGameID(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			gameID = str.toLowerCase();
			deleteCvar(cvar);
		} else {
			gameID = Game.TYPE_OTHER;
		}
		SysLogger.logMsg(5, "\tgameType set to: " + gameID);
		return gameID;
	}

	public String saveMap(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			if (str.startsWith("maps/")) str = str.substring(5);
			if (str.endsWith(".entities")) str = str.substring(0, str.length() - 9);
			mapName = str;
			deleteCvar(cvar);
		} else {
			mapName = "(none)";
		}
		return mapName;
	}

	public int saveTimeLimit(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			timeLimit = stringToInt(str);
			deleteCvar(cvar);
		} else {
			timeLimit = 0;
		}
		return timeLimit;
	}

	public int savePrivateClients(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			privateClients = stringToInt(str);
			deleteCvar(cvar);
		} else {
			privateClients = 0;
		}
		return privateClients;
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
	
	
	public ETQWPlayer findPlayerNumber(PlayerList pl, int index) {
		ETQWPlayer		p;
		for (int i = 0; i < pl.getCount(); i++) {
			p = (ETQWPlayer) pl.get(i);
			if (p.playerNumber == index) return p;
		}
		return null;
	}

	public boolean parseServerInfo(byte[] serverBytes, int bufLength) {
		int			pos;
		int			nextPos = 0;
		String		curVar;
		String		curVal;
		boolean		done = false;
		ETQWPlayer	player;
		int			playerNumber;
		int			ping;
		String		playerName;
		String		clanName;
		boolean		tagPosition;
		boolean		isBot;

		players = new PlayerList("All Players");
		attributes = new VarList();

		SysLogger.logMsg(9, "In ETQW parseServerStatus");

		pos = 33;
		if ((nextPos = findNextNull(serverBytes, pos, bufLength)) == -1) {
			SysLogger.logMsg(0, "Server " + address.getHostAddress() + ":" + port + " has no cVars");
			return false;
		}

		for (pos = 33; !done; pos = nextPos + 1) {
			nextPos = findNextNull(serverBytes, pos, bufLength);
			if (pos == nextPos) {
				done = true;
			} else {
				curVar = new String(serverBytes, pos, nextPos - pos);
				pos = nextPos + 1;
				nextPos = findNextNull(serverBytes, pos, bufLength);
				curVal = new String(serverBytes, pos, nextPos - pos);
				SysLogger.logMsg(4, "Var: " + curVar + "  Value: " + curVal);
				addCvar(curVar, curVal);
			}
		}
		pos++;
		for (VarEntry cv = attributes.getFirst(); cv != null; cv = attributes
				.getNext(cv.getKey())) {
			SysLogger.logMsg(6, cv.getKey() + " = " + cv.getValue());
		}


		// Player list comes next
		//		byte		playerNumber
		//		short		ping
		//		String		playerName (null-terminated)
		//		byte		clanTagPosition (0 = prefix, 1 = postfix)
		//		String		clanTag (null-terminated)
		//		byte		isBot

		done = false;

		String tmpBuf = "";
		for (int num = pos; num < bufLength; num++) {
			tmpBuf += String.format("(%x) ", serverBytes[num]);
		}
		SysLogger.logMsg(9, "Raw Player Data");
		SysLogger.logMsg(9, tmpBuf);

		for (playerNumber = (int) serverBytes[pos++]; playerNumber < 32; playerNumber	= (int) serverBytes[pos++]) {
			ping			= ((int) serverBytes[pos++] & 0xFF) + (((int) serverBytes[pos++] & 0xFF) << 8);
			//rate			= (((int) serverBytes[pos++] & 0xFF) << 24) + (((int) serverBytes[pos++] & 0xFF) << 16) + 
			//				  (((int) serverBytes[pos++] & 0xFF) << 8) + ((int) serverBytes[pos++] & 0xFF);
			nextPos = findNextNull(serverBytes, pos, bufLength);
			playerName		= new String(serverBytes, pos, nextPos - pos);
			pos = nextPos + 1;
			tagPosition		= (serverBytes[pos++] == 1);
			nextPos = findNextNull(serverBytes, pos, bufLength);
			clanName = (pos == nextPos) ? "" : new String(serverBytes, pos, nextPos - pos);
			pos = nextPos + 1;
			isBot			= (serverBytes[pos++] == 1);
			player = new ETQWPlayer(playerName, clanName, playerNumber, ping, isBot, tagPosition);
			SysLogger.logMsg(4, "Adding player " + playerName + " player number: " + playerNumber);
			players.add(player);
		}

		osMask			= ((int) serverBytes[pos++] & 0xFF) + (((int) serverBytes[pos++] & 0xFF) << 8) + 
		  				  (((int) serverBytes[pos++] & 0xFF) << 16) + (((int) serverBytes[pos++] & 0xFF) << 24);
		isRanked		= (serverBytes[pos++] == 1);
		timeLeft		= ((int) serverBytes[pos++] & 0xFF) + (((int) serverBytes[pos++] & 0xFF) << 8) + 
						  (((int) serverBytes[pos++] & 0xFF) << 16) + (((int) serverBytes[pos++] & 0xFF) << 24);
		gameState		= serverBytes[pos];
		
		SysLogger.logMsg(7, "OS Mask: " + osMask + " isRanked: " + isRanked + " timeLeft: " + ((int) timeLeft / 1000) + " gameState: " + gameState);

		pos += 3;

		String si_tv = getCvarValue("si_tv");
		isTV = ((si_tv != null) && (si_tv.equals("1"))) ? true : false;
		if (isTV) {
			isTeamGame = false;
			hasExtendedInfo = false;
		} else if (players.getCount() > 0) {
			String		teamName;		// empty for spectator
			float		xp;

			teams[0] = new PlayerList("GDF");
			teams[1] = new PlayerList("Strogg");
			teams[2] = new PlayerList("Spectators");

			isTeamGame = true;
			hasExtendedInfo = true;
			SysLogger.logMsg(9, "Pos: " + pos + " total size: " + bufLength);
			for (playerNumber = (int) serverBytes[pos++]; playerNumber < 32; playerNumber	= (int) serverBytes[pos++]) {
				player = findPlayerNumber(players, playerNumber);
				if (player == null) {
					SysLogger.logMsg(0, "Player number " + playerNumber + " not found in players");
					break;
				}
				xp = arr2float (serverBytes, pos);
				player.score = (int) xp;
				pos += 4;
				if ((nextPos = findNextNull(serverBytes, pos, bufLength)) == -1) {
					tmpBuf = "";
					for (int num = pos; num < bufLength; num++) {
						tmpBuf += String.format("(%x) ", serverBytes[num]);
					}
					SysLogger.logMsg(0, "Could not parse players list");
					SysLogger.logMsg(0, tmpBuf);
					break;
				}

				if (nextPos == pos) {
					teams[2].add(player);
					pos++;
				} else {
					teamName = new String(serverBytes, pos, nextPos - pos);
					pos = nextPos + 1;
					if (teamName.toLowerCase().equals("gdf")) {
						teams[0].add(player);
					} else {
						teams[1].add(player);
					}
				}

				player.kills  = ((int) serverBytes[pos++] & 0xFF) + (((int) serverBytes[pos++] & 0xFF) << 8) + 
								(((int) serverBytes[pos++] & 0xFF) << 16) + (((int) serverBytes[pos++] & 0xFF) << 24);
				player.deaths = ((int) serverBytes[pos++] & 0xFF) + (((int) serverBytes[pos++] & 0xFF) << 8) + 
								(((int) serverBytes[pos++] & 0xFF) << 16) + (((int) serverBytes[pos++] & 0xFF) << 24);
			}
		}

		saveHostName(game.getVar(Game.varServerName));
		saveEngineInfo(game.getVar(Game.varEngineVersion));
		saveGameMod(game.getVar(Game.varGameName));
		saveGameID(game.getVar(Game.varGameType));
		saveMap(game.getVar(Game.varMapName));
		saveTimeLimit(game.getVar(Game.varTimeLimit));
		saveMaxClients(game.getVar(Game.varMaxClients));
		savePBEnabled(game.getVar(Game.varAntiCheat));
		saveNeedPassword(game.getVar(Game.varNeedPassword));
		//savePrivateClients("si_privateClients");
		readableHostName = parseRawName(hostName);

		logPlayerList(6);

		return true;
	}

	public void logPlayerList(int level) {
		if (SysLogger.getLogLevel() >= level) {
			for (int i = 0; i < players.getCount(); i++) {
				ETQWPlayer p = (ETQWPlayer) players.get(i);
				SysLogger.logMsg(level, String.format("%-38s%-20s % 4d\t  % 4d", p.playerName, p.readableName, p.score, p.pingTime));
			}
		}
	}
	
	public boolean queryServer() {
		final String	command					= "getInfoEx";
		final int		SOCKET_TIMEOUT_MSECS	= 2000;
        final short		header					= (short) -1;
    	ByteBuffer		outBuf					= ByteBuffer.allocate(256);
    	byte[]			buf						= new byte[10000];
    	DatagramSocket	socket;
    	DatagramPacket	inPacket;
    	DatagramPacket	outPacket;
    	boolean			packetReceived			= false;
    	int				retryCount				= 0;
    	long			startTime;
    	long			returnTime;
    	
    	SysLogger.logMsg(9, "In ETQW queryServer");
    	
    	// Construct getstatus command
        outBuf.clear();
        outBuf.putShort(header).put(command.getBytes());
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
        	SysLogger.logMsg(0, "Unable to set socket option SO_TIMEOUT");
        	socket.close();
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

        socket.close();
       	return parseServerInfo(inPacket.getData(), inPacket.getLength());
	}
}
