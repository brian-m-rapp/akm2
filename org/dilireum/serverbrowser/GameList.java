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

public class GameList {
	private ArrayList<GameNode> 	nodeList;
	
	public GameList() {
		nodeList = new ArrayList<GameNode>();
	}
	
	public synchronized boolean add(GameNode node) {
		if (nodeList.contains(node)) {
			return false;
		} else {
			return nodeList.add(node);
		}
	}
	
	public synchronized boolean remove(GameNode node) {
		return nodeList.remove(node);
	}
	
	public synchronized GameNode remove(int i) {
		return nodeList.remove(i);
	}
	
	public synchronized GameNode get(int i) {
		return nodeList.get(i);
	}
	
	public synchronized int getCount() {
		return nodeList.size();
	}
	
	public synchronized GameNode findGameNode(String nodeID) {
		GameNode node = null;
		if (nodeID == null) return null;
		for (int i = 0; i < getCount(); i++) {
			node = nodeList.get(i);
			if (node.nodeID.equals(nodeID)) {
				return node;
			}
		}
		return null;
	}
}
