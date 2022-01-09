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

public class Q3Player extends Player {

	public int			playerNumber;
	public int			teamNumber;
	
	public Q3Player(String name, int number, int score, int pingTime, int team) {
		super(name);
		readableName = parseRawName(playerName);
		playerNumber = number;
		this.score = score;
		this .pingTime = pingTime;
		teamNumber = team;
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
