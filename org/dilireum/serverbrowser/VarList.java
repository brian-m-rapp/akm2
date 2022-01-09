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

public class VarList {
	private TreeMap<String,VarEntry> varList;
	
	public VarList() {
		varList = new TreeMap<String,VarEntry>();
	}
	
	public boolean containsKey(String key) {
		return varList.containsKey(key);
	}
	
	public VarEntry put(String key, VarEntry var) {
		if (containsKey(key)) {
			remove(key);
		}
		return varList.put(key, var);
	}
	
	public VarEntry get(String key) {
		return varList.get(key);
	}
	
	public VarEntry remove(String key) {
		return varList.remove(key);
	}
	
	public VarEntry getFirst() {
		if (varList.firstEntry() != null) {
			return varList.firstEntry().getValue();
		} else {
			return null;
		}
	}
	
	public VarEntry getNext(String key) {
		if (varList.higherEntry(key) != null) {
			return varList.higherEntry(key).getValue();
		} else {
			return null;
		}
	}

	public VarEntry getLast() {
		if (varList.lastEntry() != null) {
			return varList.lastEntry().getValue();
		} else {
			return null;
		}
	}

	public VarEntry getPrevious(String key) {
		if (varList.lowerEntry(key) != null) {
			return varList.lowerEntry(key).getValue();
		} else {
			return null;
		}
	}
}
