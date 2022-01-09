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

public class GameType extends GameNode {
	
	public GameType(Mod parent, String id, String type, String iconFile) {
		super(TYPE_NODE, parent, id, type, iconFile);
	}

	public GameType(Mod parent, String id, String type, String iconFile, ServerList list) {
		super(TYPE_NODE, parent, id, type, iconFile, list);
	}

	public boolean addServer(Server server) {
		return serverList.add(server);
	}
	
	public int countGameTypePlayers() {
		playerCount = 0;
		for (int i = 0; i < serverList.getCount(); i++) {
			playerCount += serverList.get(i).players.getCount();
		}
		return playerCount;
	}
}
