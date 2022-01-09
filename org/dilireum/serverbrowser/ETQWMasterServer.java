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

import java.io.*;
import java.net.*;
import org.dilireum.logging.*;

public class ETQWMasterServer implements MasterServer, Cloneable {

	public String			masterURL;
	public Game				game;
	public boolean			isEnabled;
	private String			masterType;

	public ETQWMasterServer(Game game, String url, boolean masterEnabled) {
		masterURL = url;
		setGame(game);
		isEnabled = masterEnabled;
		masterType = "http";
	}
	
    public MasterServer clone() {
        try {
            return (ETQWMasterServer) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen
            throw new InternalError(e.toString());
        }
    }

	public String getMasterType() {
		return masterType;
	}

	public String getMasterUrl() {
		return masterURL;
	}

	public void printInfo(int logLevel) {
		SysLogger.logMsg(logLevel, "\tMaster: " + game.nodeName + ", URL: " + masterURL);
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void queryMasterServer(ServerList serverList) {
		URL					url;
		HttpURLConnection	conn;
		InputStreamReader	in;
		BufferedReader		buff;
		String				line;
		ETQWServer			server;
		String				serverIP;
		int					port;
		int					colonPosition;
		
		if (!isEnabled) {
			return;
		}

		try {
			// Create an URL instance
			url = new URL(masterURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();

			// Get an input stream for reading
			in = new InputStreamReader((InputStream) conn.getContent());

			// Create a buffered input stream for efficiency
			buff = new BufferedReader(in);
			for (line = buff.readLine(); line != null; line = buff.readLine()) {
				if ((colonPosition = line.indexOf(':')) != -1) {
					serverIP = line.substring(0, colonPosition);
					port = Integer.parseInt(line.substring(colonPosition+1));
					server = new ETQWServer(game, serverIP, port);
					serverList.add(server);
				}
			}

			SysLogger.logMsg(4, "Found grand total of " + serverList.getCount() + " servers");
			Server myServer;
			SysLogger.logMsg(7, "Server List\n=======================================");
			if (SysLogger.getLogLevel() >= 7) {
				for (int i = 0; i < serverList.getCount(); i++) {
					myServer = serverList.get(i);
					SysLogger.logMsg(7, "Server: " + myServer.address.getHostAddress() + ":" + myServer.port);
				}
			}
			SysLogger.logMsg(7, "=== End List ===");
		} catch (MalformedURLException mue) {
			SysLogger.logMsg(0, "Invalid URL");
		} catch (IOException ioe) {
			SysLogger.logMsg(0, "I/O Error - " + ioe);
		}
	}
}
