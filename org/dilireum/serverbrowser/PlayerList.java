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

import java.util.*;

public class PlayerList {
	public	String				name;
	private ArrayList<Player> 	playerList;
	
	public PlayerList(String teamName) {
		playerList = new ArrayList<Player>();
		name = teamName;
	}
	
	public boolean add(Player player) {
		return playerList.add(player);
	}
	
	public int findPlayer(Player player) {
		for (int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i) == player) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean remove(Player player) {
		return playerList.remove(player);
	}
	
	public Player remove(int i) {
		return playerList.remove(i);
	}
	
	public Player get(int i) {
		return playerList.get(i);
	}
	
	public int getCount() {
		return playerList.size();
	}
}
