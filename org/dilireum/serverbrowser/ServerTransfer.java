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

import org.dilireum.logging.SysLogger;
import org.eclipse.swt.dnd.*;
import java.io.*;

public class ServerTransfer extends ByteArrayTransfer {

	private static final String		SERVERTRANSFERNAME	= "ServerTransfer";
	private static final int		SERVERTRANSFERID	= registerType(SERVERTRANSFERNAME);
	private static ServerTransfer	_instance			= new ServerTransfer();
	
	public ServerTransfer() {
		super();
	}

	public static Transfer getInstance() {
		return _instance;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	@Override
	protected int[] getTypeIds() {
		return new int[] { SERVERTRANSFERID };
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	@Override
	protected String[] getTypeNames() {
		return new String[] { SERVERTRANSFERNAME };
	}

	boolean checkServerClass(Object object) {
		return (object != null) && object instanceof Server[];
	}

	protected boolean validate(Object object) {
		return checkServerClass(object);
	}

	byte[] javaToByteArray(Object object) {
		Server[] servers = (Server[]) object;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream wOut = new DataOutputStream(out);
			byte[] buffer;
			
			wOut.writeInt(servers.length);
			for (int i = 0; i < servers.length; i++) {
				
				buffer = servers[i].game.nodeID.getBytes();		// Write the Game ID
				wOut.writeInt(buffer.length);
				wOut.write(buffer);

				buffer = servers[i].address.getHostAddress().getBytes();	// Write IP address
				wOut.writeInt(buffer.length);
				wOut.write(buffer);

				wOut.writeInt(servers[i].port);				// Write port

				buffer = servers[i].gameMod.getBytes();		// Write the Mod Name
				wOut.writeInt(buffer.length);
				wOut.write(buffer);

				if ((servers[i].gameType != null) && (servers[i].gameType.length() > 0)) {
					buffer = servers[i].gameType.getBytes();	// Write the game type
					wOut.writeInt(buffer.length);
					wOut.write(buffer);
				} else {
					wOut.writeInt(0);
				}

				wOut.writeBoolean(servers[i].needPassword);	// Write PW needed

				wOut.writeBoolean(servers[i].pbEnabled);	// Write PB enabled

				buffer = servers[i].hostName.getBytes();		// Write the host name
				wOut.writeInt(buffer.length);
				wOut.write(buffer);

				buffer = servers[i].readableHostName.getBytes();	// Write the readable host name
				wOut.writeInt(buffer.length);
				wOut.write(buffer);

				wOut.writeInt(servers[i].pingTime);			// Write ping time

				wOut.writeInt(servers[i].maxClients);		// Write max clients

				buffer = servers[i].gameEngine.getBytes();	// Write the game engine
				wOut.writeInt(buffer.length);
				wOut.write(buffer);

				buffer = servers[i].engineVersion.getBytes();	// Write the engine version
				wOut.writeInt(buffer.length);
				wOut.write(buffer);

				buffer = servers[i].mapName.getBytes();	// Write the map name
				wOut.writeInt(buffer.length);
				wOut.write(buffer);
			}
			
			buffer = out.toByteArray();
			wOut.close();
			return buffer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	Object byteArrayToJava(byte[] bytes) {
		Server server = null;
		Server[] servers;

		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			DataInputStream rIn = new DataInputStream(in);
			String	gameId;
			Game	game;
			String	ipAddress;
			int		port;
			int		count = rIn.readInt();

			servers = new Server[count];
			for (int i = 0; i < count; i++) {
				
				int		size = rIn.readInt();
				byte[] buffer = new byte[size];
				rIn.read(buffer);					// Read the Game ID
				gameId = new String(buffer);
				SysLogger.logMsg(9, "Game ID: " + gameId);
				game = AllKnowingMind.games.get(gameId);

				size = rIn.readInt();				// Read the IP address
				buffer = new byte[size];
				rIn.read(buffer);
				ipAddress = new String(buffer);

				port = rIn.readInt();				// Read the port

				if (game.gameNetProtocol.equals("q3s") ||
					game.gameNetProtocol.equals("q3i")) {
					server = new Q3Server(game, ipAddress, port);
				} else if (game.gameNetProtocol.equals("etqw")) {
					server = new ETQWServer(game, ipAddress, port);
				} else {
					SysLogger.logMsg(9, game.gameNetProtocol);
					return null;
				}

				size = rIn.readInt();				// Read the Mod Name
				buffer = new byte[size];
				rIn.read(buffer);
				server.gameMod = new String(buffer); 

				size = rIn.readInt();				// Read the game type
				if (size > 0) {
					buffer = new byte[size];
					rIn.read(buffer);
					server.gameType = new String(buffer);
				} else {
					server.gameType = null;
				}

				server.needPassword = rIn.readBoolean();	// Read PW needed

				server.pbEnabled = rIn.readBoolean();

				size = rIn.readInt();				// Read the host name
				buffer = new byte[size];
				rIn.read(buffer);
				server.hostName = new String(buffer); 

				size = rIn.readInt();				// Read the readable host name
				buffer = new byte[size];
				rIn.read(buffer);
				server.readableHostName = new String(buffer); 

				server.pingTime = rIn.readInt();

				server.maxClients = rIn.readInt();

				size = rIn.readInt();				// Read the game engine
				buffer = new byte[size];
				rIn.read(buffer);
				server.gameEngine = new String(buffer); 

				size = rIn.readInt();				// Read the engine version
				buffer = new byte[size];
				rIn.read(buffer);
				server.engineVersion = new String(buffer); 

				size = rIn.readInt();				// Read the engine version
				buffer = new byte[size];
				rIn.read(buffer);
				server.mapName = new String(buffer); 
				servers[i] = server;
			}			
			rIn.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return servers;
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (!checkServerClass(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		byte[] buffer = javaToByteArray(object);
		super.javaToNative(buffer, transferData);
	}

	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null) {
				return null;
			}
			return byteArrayToJava(buffer);
		}
		return null;
	}
}
