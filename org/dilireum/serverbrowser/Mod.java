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

public class Mod extends GameNode implements Cloneable {
	public static final String	DEF_TEAM1	= "Blue";
	public static final String	DEF_TEAM2	= "Red";
	public static final String	OTHER		= "other";
	public static final String	UNKNOWN		= "unknown";

	public static final String	GAMEID		= "GAME_ID";
	public static final String	MODVERSION	= "MOD_VERSION";
	public String 					discriminator;
	public boolean					customNames;
	public String					team1Name;
	public String					team2Name;

	public String					startParms;
	public boolean					isStandalone;
	public String					startCommand;
	public String					workingDir;

	public Mod(Game parent, String modID, String modName, String iconFile, String crit, boolean customTeamNames) {
		super(MOD_NODE, parent, modID, modName, iconFile);
		isStandalone = false;
		discriminator = crit;
		customNames = customTeamNames;
		if (!customNames) {
			team1Name = DEF_TEAM1;
			team2Name = DEF_TEAM2;
		}
	}

	public Mod(Game parent, String modID, String modName, String iconFile, String crit, String team1, String team2) {
		super(MOD_NODE, parent, modID, modName, iconFile);
		isStandalone = false;
		discriminator = crit;
		team1Name = team1;
		team2Name = team2;
		customNames = false;
	}

    protected Mod clone() {
        try {
            return (Mod) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen
            throw new InternalError(e.toString());
        }
    }

    public Mod copyMod() {
    	Mod newMod = clone();
    	
		for (int i = 0; i < childList.getCount(); i++) {
			GameType gt = (GameType) childList.get(i);
			gt.parent = newMod;
		}
    	return newMod;
    }

	public boolean addServer(Server server) {
		server.typeDiscriminator = discriminator;
		return serverList.add(server);
	}

	public ServerList getServerList() {
		return serverList;
	}
	
	public int getServerCount() {
		return serverList.getCount();
	}
	
	public GameType getGameType(String gameType) {
		return findGameType(gameType);
	}

	public GameType addGameType(String gameID, String gameType) {
		GameType gt = new GameType(this, gameID, gameType, null);
		childList.add(gt);
		return gt;
	}

	public String getNodeIdField() {
		return discriminator;
	}
	
	public int countModPlayers() {
		playerCount = 0;
		for (int i = 0; i < serverList.getCount(); i++) {
			playerCount += serverList.get(i).players.getCount();
		}
		return playerCount;
	}
	
	public GameType findGameTypeID(String id) {
		for (int i = 0; i < childList.getCount(); i++) {
			if (childList.get(i).nodeID == id) {
				return (GameType) childList.get(i);
			}
		}
		return null;
	}

	public GameType findGameType(String typeString) {
		for (int i = 0; i < childList.getCount(); i++) {
			if (childList.get(i).nodeID.equals(typeString)) {
				return (GameType) childList.get(i);
			}
		}
		return null;
	}
}
