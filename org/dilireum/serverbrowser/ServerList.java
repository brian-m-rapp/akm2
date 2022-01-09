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

public class ServerList {
	private ArrayList<Server> 	serverList;
	public int					counter;
	
	public ServerList() {
		serverList = new ArrayList<Server>();
		counter = 0;
	}
	
	public synchronized boolean add(Server server) {
		if (serverList.contains(server)) {
			return false;
		} else {
			return serverList.add(server);
		}
	}
	
	public synchronized boolean replace(int slot, Server server) {
		if (slot < serverList.size()) {
			remove(slot);
			serverList.add(slot, server);
			return true;
		} else {
			return false;
		}
	}

	public synchronized Server findServerByAddress(String address, int port) {
		Server server = null;
		
		if (address == null) return null;
		for (int i = 0; i < getCount(); i++) {
			server = get(i);
			if (server.address.getHostAddress().equals(address) && (server.port == port)) {
				return server;
			}
		}
		return null;
	}
	
	public synchronized int findServerIndex(Server server) {
		int			i;
		Server		s;
		for (i = 0; i < serverList.size(); i++) {
			s = serverList.get(i);
			if (s == server) return i;
		}
		return -1;
	}
	
	public synchronized boolean remove(Server server) {
		return serverList.remove(server);
	}
	
	public synchronized Server remove(int i) {
		return serverList.remove(i);
	}
	
	public synchronized Server get(int i) {
		return serverList.get(i);
	}
	
	public synchronized int getCount() {
		return serverList.size();
	}
}
