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
import java.util.TreeMap;

public class GameMap {
	private TreeMap<String,Game> gameList;
	
	public GameMap() {
		gameList = new TreeMap<String,Game>();
	}
	
	public int size() {
		return gameList.size();
	}

	public boolean containsKey(String key) {
		return gameList.containsKey(key);
	}
	
	public Game put(String key, Game game) {
		return gameList.put(key, game);
	}
	
	public Game get(String key) {
		return gameList.get(key);
	}
	
	public Game remove(String key) {
		return gameList.remove(key);
	}
	
	public Game getFirst() {
		if (gameList.firstEntry() != null) {
			return gameList.firstEntry().getValue();
		} else {
			return null;
		}
	}
	
	public Game getNext(String key) {
		if (gameList.higherEntry(key) != null) {
			return gameList.higherEntry(key).getValue();
		} else {
			return null;
		}
	}

	public Game getLast() {
		if (gameList.lastEntry() != null) {
			return gameList.lastEntry().getValue();
		} else {
			return null;
		}
	}

	public Game getPrevious(String key) {
		if (gameList.lowerEntry(key) != null) {
			return gameList.lowerEntry(key).getValue();
		} else {
			return null;
		}
	}

	public void load(int logging, String supportedGamesFile, String XMLversion) {
		GamesLoader			gl = new GamesLoader(this, supportedGamesFile, XMLversion);
		gl.parseGames();									// Load all supported games
		gl.printGames(logging);
	}

	public void loadDefaults(int logging, String gameDefaultsFile, String XMLversion, String osArch) {
		GameDefaultsLoader	gdf = new GameDefaultsLoader(this, gameDefaultsFile, XMLversion, osArch);
		gdf.parseGames();									// Load the default values for this architecture/OS
		gdf.printGames(logging);
	}
	
	public void loadInstalledGames(int logging, String userGamesFile, String folderImageFile, String XMLversion) {
		UserGamesLoader ugl = new UserGamesLoader(this, userGamesFile, folderImageFile, XMLversion);
		ugl.parseGames();
		ugl.printGames(logging);
	}

	public void writeUserGames(String userGamesFile, String XMLversion, String osArch) throws IOException {
		UserGamesWriter		ugw = new UserGamesWriter(this, userGamesFile);
		try {
			ugw.writeUserGamesFile(XMLversion, osArch);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	public void setInstalledGames() {
    	for (Game game = getFirst(); game != null; game = getNext(game.nodeID)) {
    		for (VarEntry start = game.workingDir.getFirst(); start != null; start = game.workingDir.getNext(start.getKey())) {
    			if ((new File(start.getValue())).exists()) {
    				game.isInstalled = true;
    				game.isDisplayed = true;
    			}
    		}
    		if (game.isInstalled) {
    			for (int i = 0; i < game.childList.getCount(); i++) {
    				Mod mod =  (Mod) game.childList.get(i);
    				mod.isInstalled = true;
    				mod.isDisplayed = true;
    			}
    		}
    	}
	}
}