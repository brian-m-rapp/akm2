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

public class ETQWPlayer extends Player {
	public int			playerNumber;
	public String		clanName;
	public boolean		isBot;
	public boolean		tagPosition;
	public int			kills;
	public int			deaths;
	
	public ETQWPlayer(String name, String clan, int number, int pingTime, boolean isBot, boolean tagPosition) {
		super(name);
		readableName = parseRawName(playerName);
		playerNumber = number;
		this .pingTime = pingTime;
		clanName = clan;
		this.isBot = isBot;
		this.tagPosition = tagPosition;
	}
	

	private String parseRawName (String rawName) {
		
		String rn = new String();
		
		for (int i = 0; i < rawName.length(); i++) {
			if (rawName.substring(i, i+1).equals("^")) {
				if ((i+2) < rawName.length()) {
					if (rawName.substring(i+1, i+2).equals("x")) {
						if ((i+7) < rawName.length()) {
							i += 7;
						} else {
							i++;
						}
					} else {
						i++;
					}
				} else {
					i++;
				}
			} else {
				rn += rawName.substring(i, i+1);
			}
		}
		return rn;
	}
}
