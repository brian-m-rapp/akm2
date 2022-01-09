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

public class IgnoreList {

	private ArrayList<IgnoreItem>		ignoreItems;
	
	public IgnoreList() {
		ignoreItems = new ArrayList<IgnoreItem>();
	}
	
	public synchronized boolean add(IgnoreItem item) {
		return ignoreItems.add(item);
	}
	
	public synchronized boolean remove(IgnoreItem item) {
		return ignoreItems.remove(item);
	}
	
	public synchronized IgnoreItem remove(int i) {
		return ignoreItems.remove(i);
	}
	
	public synchronized IgnoreItem get(int i) {
		return ignoreItems.get(i);
	}
	
	public synchronized int getCount() {
		return ignoreItems.size();
	}
}
