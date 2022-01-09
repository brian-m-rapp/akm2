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

import org.eclipse.swt.graphics.*;
import java.util.TreeMap;

public class ImageList {
	private TreeMap<String,Image> imageList;
	
	public ImageList() {
		imageList = new TreeMap<String,Image>();
	}
	
	public boolean containsKey(String key) {
		return imageList.containsKey(key);
	}
	
	public Image put(String key, Image flagIcon) {
		return imageList.put(key, flagIcon);
	}
	
	public Image get(String key) {
		return imageList.get(key);
	}
	
	public Image remove(String key) {
		return imageList.remove(key);
	}
	
	public Image getFirst() {
		if (imageList.firstEntry() != null) {
			return imageList.firstEntry().getValue();
		} else {
			return null;
		}
	}
	
	public Image getNext(String key) {
		if (imageList.higherEntry(key) != null) {
			return imageList.higherEntry(key).getValue();
		} else {
			return null;
		}
	}

	public Image getLast() {
		if (imageList.lastEntry() != null) {
			return imageList.lastEntry().getValue();
		} else {
			return null;
		}
	}

	public Image getPrevious(String key) {
		if (imageList.lowerEntry(key) != null) {
			return imageList.lowerEntry(key).getValue();
		} else {
			return null;
		}
	}
}