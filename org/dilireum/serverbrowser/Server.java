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
import org.dilireum.logging.SysLogger;

public abstract class Server {

	public static final String	UNKNOWN_MAP		= "???";
	public static final String	UNKNOWN_HOST	= "(Unknown)";
	public static final String	UNKNOWN_ENGINE	= "???";
	public static final String	UNKNOWN_GAMETYPE = "???";
	public static final int		SIZE_TEAMS	= 4;
	public static final int		TEAM1		= 0;
	public static final int		TEAM2		= 1;
	public static final int		SPECTATORS	= 2;
	public static final int		REFEREES	= 3;

	public String[]					teamNames;
	public int[]					teamScore;
	public Game						game;
	public Mod						mod;
	public final InetAddress		address;
	public final int				port;
	public VarList					attributes;
	public PlayerList				players;
	public PlayerList[]				teams;
	public boolean					isValid;
	public boolean					isTeamGame;
	public boolean					hasExtendedInfo;
	public boolean					hasTeamScore;
	public boolean					isTV;
	public String					gameIconFile;
	protected String				gameID;
	public String					gameName;
	public String					gameMod;
	public String					gameEngine;
	public String					engineVersion;	// 1.32c, 1.27g, etc
	public String					gameType;		// CTF, FFA, TDM, 1on1
	public String					country;
	public int						pingTime;
	public int						maxClients;
	public String					hostName;
	public String					readableHostName;
	public boolean					pbEnabled;
	public boolean					needPassword;
	public String					mapName;
	public String					typeDiscriminator;
	public String					gameClock;

	protected Server(Game game, String server, int port) {
		address = strToInetAddress(server);
		this.port = port;
		this.game			= game;
		mod					= null;
		isValid				= false;
		mapName				= UNKNOWN_MAP;
		needPassword		= false;
		pbEnabled			= false;
		isTV				= false;
		hostName			= UNKNOWN_HOST;
		readableHostName	= UNKNOWN_HOST;
		hasExtendedInfo		= false;
		hasTeamScore		= false;
		players				= new PlayerList("All Players");
		attributes			= new VarList();
		teamScore			= new int[2];
		teamNames			= new String[SIZE_TEAMS];
		teams				= new PlayerList[SIZE_TEAMS];
	}

	protected Server(Game game, InetAddress server, int port) {
		this(game, server.getHostAddress(), port);
	}
	
	private InetAddress strToInetAddress(String server) {
		InetAddress tmpAddr;
		
    	try {
    		tmpAddr = InetAddress.getByName(server);
    	} catch (UnknownHostException e) {
    		SysLogger.logMsg(0, "Host \"" + server + "\" could not be resolved");
    		tmpAddr = null;
    	}
		
    	return tmpAddr;
	}
	
	public static float arr2float (byte[] arr, int start) {
		int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);
	}

	public void setGameName(String name) {
		gameName = name;
		SysLogger.logMsg(5, "\tgameName set to: " + gameName);
	}
	
	public boolean isAddressValid() {
		return (address != null);
	}

	public int pingServer() {
		String	os			= System.getProperty("os.name");
		
		if (os.toLowerCase().contains("windows")) {
			return pingWindowsServer();
		} else if (os.toLowerCase().contains("linux") || os.toLowerCase().contains("mac")) {
			return pingUnixServer();
		} else {
			return 9999;
		}
	}
	
	public int pingUnixServer() {
		String	ip			= address.getHostAddress();
		String	pingCmd		= "ping -c3 -W1 " + ip;
		String	inputLine	= null;
		int		timeVal		= 9999;
		boolean	found		= false;
		String	timeStr;
		int		end;

		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(pingCmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while (!found && (inputLine = in.readLine()) != null) {
				if (inputLine.indexOf("rtt") != -1) {
					found = true;
				}
			}

			if (found) {
				// "rtt min/avg/max/mdev = 1.110/1.487/2.202/0.506 ms"
				timeStr = inputLine.substring(inputLine.indexOf('='));
				timeStr = timeStr.substring(timeStr.indexOf('/') + 1);
				end = timeStr.indexOf('/');
				timeStr = timeStr.substring(0, end);
				timeVal = (int) (Float.parseFloat(timeStr) + 0.5);
			} else {
				timeVal = 9999;
			}
			in.close();
		} catch (IOException e) {
			System.err.println(e);
		}

		return timeVal;
	}
	
	public int pingWindowsServer() {
		String	ip			= address.getHostAddress();
		String  pingCmd		= "ping -n 3 -w 1000 " + ip;
		String	inputLine	= null;
		int		timeVal		= 9999;
		boolean	found		= false;
		String	timeStr;
		int		end;

		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(pingCmd);

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
			while (!found && (inputLine = in.readLine()) != null) {
				if (inputLine.indexOf("Average") != -1) {
					found = true;
				}
			}

			if (found) {
				// "Minimum = 1ms, Maximum = 9ms, Average = 3ms"
				timeStr = inputLine.substring(inputLine.indexOf("Average"));
				timeStr = timeStr.substring(timeStr.indexOf('=') + 2);
				end = timeStr.indexOf("ms");
				timeStr = timeStr.substring(0, end);
				timeVal = (int) (Float.parseFloat(timeStr) + 0.5);
			} else {
				timeVal = 9999;
			}
			in.close();
		} catch (IOException e) {
			System.err.println(e);
		}

		return timeVal;
	}
	
	public void findPingTime() {
		pingTime = pingServer();
	}
	
	public void setPBEnabled(boolean pbStat) {
		pbEnabled = pbStat;
		SysLogger.logMsg(5, "\tPunkbuster enabled is: " + isPBEnabled());
	}

	public boolean isPBEnabled() {
		return pbEnabled;
	}
	
	public void setNeedPassword(boolean pwb) {
		needPassword = pwb;
		SysLogger.logMsg(5, "\tPassword needed is: " + isPasswordNeeded());
	}

	public boolean isPasswordNeeded() {
		return needPassword;
	}

	public void setGameType(String type) {
		gameType = type;
	}
	
	public String getGameType() {
		return gameType;
	}

	public void addCvar(String key, String value) {
    	attributes.put(key.toLowerCase(), new VarEntry(key.toLowerCase(), value));
	}
	
	public String getCvarValue(String key) {
		VarEntry val = attributes.get(key.toLowerCase());
		if (val != null) {
			SysLogger.logMsg(7, "\t\t" + key + " = " + val.getValue());
			return val.getValue();
		} else {
			return null;
		}
	}
	
	public void deleteCvar(String key) {
		SysLogger.logMsg(7, "\t\tRemoving Cvar " + key);
		attributes.remove(key.toLowerCase());
	}

	public int stringToInt(String numStr) {
		try {
			return (Integer.parseInt(numStr));
		} catch (NumberFormatException e) {
			try {
				return (int) (Float.parseFloat(numStr) + 0.5);
			} catch (NumberFormatException f) {
				return 0;
			}
		}
	}
	
	public void setMaxClients(int num) {
		maxClients = num;
		SysLogger.logMsg(5, "\tmaxClients set to: " + getMaxClients());
	}
	
	public int getMaxClients() {
		return maxClients;
	}

	public int saveMaxClients(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			setMaxClients(stringToInt(str));
			deleteCvar(cvar);
		} else {
			setMaxClients(0);
		}
		return getMaxClients();
	}

	public String saveMap(String cvar) {
		String str = getCvarValue(cvar);
		if (str != null) {
			mapName = str;
			deleteCvar(cvar);
		} else {
			mapName = "(none)";
		}
		return mapName;
	}

	public void setHostName(String name) {
		hostName = name;
		SysLogger.logMsg(5, "\thostName set to: " + hostName);
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public String saveHostName(String cvar) {
		String str = getCvarValue(cvar);

		if (str != null) {
			setHostName(str);
			deleteCvar(cvar);
		} else {
			setHostName("(no name)");
		}
		return getHostName();
	}

	public abstract boolean getServerStatus();
	
	public abstract boolean getQuickStatus();

	public abstract String getModNodeID();
	
}
