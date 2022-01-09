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
import java.nio.*;
import java.util.*;
import org.dilireum.logging.*;

public class Q3MasterServer implements MasterServer, Cloneable {

	static final int			MAX_QUERY_THREADS = 50;
	public String				masterUrl;
	public InetAddress			ipAddress;
	public int					port;
	public ArrayList<Integer>	pList;
	public Game					game;
	public String				queryGameName;
	public boolean				isEnabled;
	public String				masterType;

	public Q3MasterServer(Game game, String url, int port, ArrayList<Integer> protocols, boolean masterEnabled) {
		masterUrl = url;
		setServerAddress(url, port);
		pList = protocols;
		setGame(game);
		queryGameName = null;
		isEnabled = masterEnabled;
		masterType = "q3m";
	}

	public Q3MasterServer(Game game, String url, int port, ArrayList<Integer> protocols, String name, boolean masterEnabled) {
		this (game, url, port, protocols, masterEnabled);
		queryGameName = name;
	}

    public MasterServer clone() {
        try {
            return (Q3MasterServer) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen
            throw new InternalError(e.toString());
        }
    }

	public String getMasterType() {
		return masterType;
	}

	public String getMasterUrl() {
		return masterUrl;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public void printInfo(int logLevel) {
		String prots = "{ ";
		for (int i = 0; i < pList.size(); i++) {
			prots += pList.get(i) + " ";
		}
		prots += "}";
		if (ipAddress == null) {
			SysLogger.logMsg(logLevel, "\tMaster: " + game.nodeName + ", IP: (UNKNOWN), Protocols: " + prots + ", Game Name Query: " + queryGameName);
		} else {
			SysLogger.logMsg(logLevel, "\tMaster: " + game.nodeName + ", IP: " + ipAddress.getHostAddress() + ":" + port + ", Protocols: " + prots + ", Game Name Query: " + queryGameName);
		}
	}

	public void setServerAddress(String server, int port) {
    	try {
    		ipAddress = InetAddress.getByName(server);
    	} catch (UnknownHostException e) {
    		SysLogger.logMsg(0, "Host \"" + server + "\" could not be resolved");
    		ipAddress = null;
    	}
    	
    	this.port = port;
	}

	private int ByteArrayToPort(byte[] b) {
		int value = 0;
		for (int i = 0; i < 2; i++) {
			int shift = (2 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	private class queryMasterServerThread implements Runnable {

		private int			protocol;
		private PLogger		logger;
		private ServerList	serverList;
		
		public queryMasterServerThread(ServerList list, int protocol) {
			serverList = list;
			this.protocol = protocol;
			logger = new PLogger(SysLogger.getLogLevel());
			logger.setOutputPath(AllKnowingMind.logDir + File.separator + "master_server_" + protocol + ".log");
			logger.setLogOutput(PLogger.SCREEN | PLogger.FILE);
		}
		
		private int buildServerList (ServerList serverList, ByteBuffer serverBuffer, int protocol) {
			byte[]				ipbuf = new byte[30];
			InetAddress			ipA = null;
			int					port;
			String				header;
			Q3Server			server;
			int					count = 0;
			boolean				found;
			int					i;
			
			serverBuffer.get(ipbuf, 0, 23);
			header = new String(ipbuf, 4, 19);
			logger.logMsg(9, "In parseServerStatus. " + header + " buffer limit = " + serverBuffer.limit());

	        while (serverBuffer.remaining() > 7) {
	        	logger.logMsg(9, "Bytes remaining: " + serverBuffer.remaining() + " position: " + serverBuffer.position());
	        	ipbuf = new byte[4];
	        	serverBuffer.get(ipbuf, 0, 4);	// Get the IP address (the next 4 bytes)
	        	
	        	try {
	        		ipA = InetAddress.getByAddress(ipbuf);	// Convert into an InetAddress
	        		if (!ipA.getHostAddress().equals("0.0.0.0")) {
	        			serverBuffer.get(ipbuf, 0, 2);			// Get the port number (next 2 bytes)
	        			port = ByteArrayToPort(ipbuf);			// Convert two bytes into an unsigned short
	        			logger.logMsg(6, String.format("Server: %s:%d", ipA.getHostAddress(), port));
	        			serverBuffer.get(ipbuf, 0, 1);			// Read and discard the backslash
	        			for (i = 0, found = false; i < serverList.getCount(); i++) {
	        				Server	qs = serverList.get(i);
	        				if (qs.address.getHostAddress().equals(ipA.getHostAddress()) && (qs.port == port)) {
	        					found = true;
	        					break;
	        				}
	        			}
	        			
	        			if (!found) {
	        				server = new Q3Server(game, ipA.getHostAddress(), port);
	        				serverList.add(server);
	        				count++;
	        			}
	        		}
	        	} catch (UnknownHostException e) {
	        		logger.logMsg(0, "Unknown host exception detected");
	        		// advance the buffer pointer past the port and the backslash
	        		serverBuffer.get(ipbuf, 0, 3);
	        	}
	        }
	        if (serverBuffer.remaining() > 0) {
	        	if (logger.getLogLevel() >= 7) {
	        		int rem = serverBuffer.remaining();
	        		byte[]	rb = new byte[rem];
	        		String end;
	        		serverBuffer.get(rb, 0, rem);
	        		end = new String(rb);
	        	
	        		logger.logMsg(7, "Remaining bytes: " + rem + " " + end);
	        	}
	        }
	        
	        return count;
	    }

		public void run () {
			final int		SOCKET_TIMEOUT_MSECS = 1000;
			final String	GETSERVERS = "getservers";
			final String	PARAMETERS = "empty full";
			final int		header = 0xFFFFFFFF;
			ByteBuffer		command = ByteBuffer.allocate(256);
			byte[]			buf = new byte[65535];
			String 			outString;
			DatagramSocket	socket;
			DatagramPacket	inPacket;
			DatagramPacket	outPacket;
			int				numServers = 0;
			
			logger.logMsg(6, "Querying Q3 master server for protocol " + protocol);
			if (queryGameName != null)
				outString = String.format("%s %s %d '%s'", GETSERVERS, queryGameName, protocol, PARAMETERS);
			else
				outString = String.format("%s %d '%s'", GETSERVERS, protocol, PARAMETERS);
			logger.logMsg(9, "outString: " + outString);

			// Construct getstatus command
			command.clear();
			command.putInt(header).put(outString.getBytes());
			command.flip();
        
			try {
				socket = new DatagramSocket();
			} catch (SocketException e) {
				SysLogger.logMsg(0, "Error creating socket: " + e.getMessage());
				return;
			}
        
			try {
				socket.setSoTimeout(SOCKET_TIMEOUT_MSECS);
			} catch (SocketException se) {
				SysLogger.logMsg(0, "Unable to set socket option SO_TIMEOUT");
				socket.close();
				return;
			}

			// Construct datagram and send to server
			outPacket = new DatagramPacket(command.array(), command.array().length, ipAddress, port);
			inPacket = new DatagramPacket(buf, buf.length);

			try {
				socket.send(outPacket);
			} catch (IOException e) {
				SysLogger.logMsg(0, "Error writing to socket: " + e.getMessage());
				socket.close();
				return;
			}

			boolean isMoreData = true;

			while (isMoreData) {
				try {
					socket.receive(inPacket);
					String received = new String(inPacket.getData(), 4, 19);
					logger.logMsg(8, received);
					numServers += buildServerList (serverList, ByteBuffer.wrap(inPacket.getData(), 0, inPacket.getLength()), protocol);
				} catch (SocketTimeoutException e) {
					isMoreData = false;
				} catch (IOException e) {
					SysLogger.logMsg(0, "Error reading from socket " + e.getMessage());
					socket.close();
					return;
				}
			}

			logger.logMsg(4, "Found " + numServers + " servers for protocol " + protocol);

			socket.close();
			logger.closeLogFile();
			}
		}
	
	public void queryMasterServer(ServerList serverList) {
		int		numThreads = pList.size();
		Thread queryThreads[]	= new Thread[numThreads];
		
		if (!isEnabled) {
			return;
		}

		for (int i = 0; i < numThreads; i++) {
			queryThreads[i] = new Thread(new queryMasterServerThread(serverList, pList.get(i)));
			queryThreads[i].start();
		}
		
		boolean done = false;
		while (!done) {
			done = true;
			for (int i = 0; i < numThreads; i++) {
				if (queryThreads[i].isAlive()) {
					done = false;
					break;
				}
			}
			
			if (!done) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					SysLogger.logMsg(0, "Thread interrupted");
				}
			}
		}

		SysLogger.logMsg(4, "Found grand total of " + serverList.getCount() + " servers");
		Server server;
		SysLogger.logMsg(7, "Server List\n=======================================");
		if (SysLogger.getLogLevel() >= 7) {
			for (int i = 0; i < serverList.getCount(); i++) {
				server = serverList.get(i);
				SysLogger.logMsg(7, "Server: " + server.address.getHostAddress() + ":" + server.port);
			}
		}
		SysLogger.logMsg(7, "=== End List ===");
	}
}
