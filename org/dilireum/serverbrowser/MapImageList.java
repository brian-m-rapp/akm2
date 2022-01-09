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

import java.util.TreeMap;

public class MapImageList {
	private TreeMap<String,String> mapList;
	
	public MapImageList() {
		mapList = new TreeMap<String,String>();
	}
	
	public boolean containsKey(String key) {
		return mapList.containsKey(key);
	}
	
	public String put(String key, String mapFileName) {
		return mapList.put(key, mapFileName);
	}
	
	public String get(String key) {
		return mapList.get(key);
	}
	
	public String remove(String key) {
		return mapList.remove(key);
	}
	
	public String getFirst() {
		if (mapList.firstEntry() != null) {
			return mapList.firstEntry().getValue();
		} else {
			return null;
		}
	}
	
	public String getNext(String key) {
		if (mapList.higherEntry(key) != null) {
			return mapList.higherEntry(key).getValue();
		} else {
			return null;
		}
	}

	public String getLast() {
		if (mapList.lastEntry() != null) {
			return mapList.lastEntry().getValue();
		} else {
			return null;
		}
	}

	public String getPrevious(String key) {
		if (mapList.lowerEntry(key) != null) {
			return mapList.lowerEntry(key).getValue();
		} else {
			return null;
		}
	}
	
}