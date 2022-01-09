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

public abstract class GameNode {

	public static final String	GAME_NODE			= "GAME";
	public static final String	MOD_NODE			= "MOD";
	public static final String	TYPE_NODE			= "GAMETYPE";
	public static final String	FOLDER_NODE			= "FOLDER";

	public final String			nodeType;			// GAME, MOD, GAMETYPE, FOLDER
	public final String 		nodeID;				// id for this node for matching
	public final String 		nodeName;			// display name for this node
	public final String			iconFileName;
	public GameNode				parent;				// Points to the parent of this node.  For a game, this will be null
	public GameList				childList;			// Points to a list of child nodes (For games, this can be MOD or FOLDER nodes)
	protected ServerList		serverList;			// Points to list of servers associated with this node
	public int					playerCount;		// Number of people currently playing this node and all its children
	public boolean				isInstalled;		// Set to true if this game/mod is installed on the user's computer
	public boolean				isDisplayed;		// Set to true if the user wants this game/mod displayed

	public GameNode(String type, GameNode parentNode, String id, String name, String icon) {
		nodeType		= type;
		parent			= parentNode;
		nodeID			= id;
		nodeName		= name;
		iconFileName	= icon;
		childList		= new GameList();
		serverList		= new ServerList();
		playerCount		= 0;
		isInstalled		= false;
		isDisplayed		= false;
	}
	
	public GameNode(String type, GameNode parentNode, String id, String name, String icon, ServerList list) {
		nodeType		= type;
		parent			= parentNode;
		nodeID			= id;
		nodeName		= name;
		iconFileName	= icon;
		childList		= new GameList();
		serverList		= list;
		playerCount		= 0;
		isInstalled		= false;
		isDisplayed		= false;
	}
	
	public void setServerList(ServerList list) {
		serverList = list;
	}
	
	public ServerList getServerList() {
		return serverList;
	}
	
	public GameNode getParent() {
		return parent;
	}
	
	public String getID() {
		return nodeID;
	}
	
	public String getName() {
		return nodeName;
	}
	
	public String getNodeType() {
		return nodeType;
	}
	
	public GameList getChildList() {
		return childList;
	}
	
	public int countGameNodePlayers() {
		playerCount = 0;
		for (int i = 0; i < serverList.getCount(); i++) {
			playerCount += serverList.get(i).players.getCount();
		}
		return playerCount;
	}
}
