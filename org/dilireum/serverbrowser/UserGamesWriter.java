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
import org.znerd.xmlenc.*;

/**
 * Class for creating a new usergames.xml file from an existing and populated
 * GameMap.
 * 
 * @author brapp
 *
 */
public class UserGamesWriter {

	private final String		outputFile;
	private final GameMap		games;
	private XMLOutputter		pw;
	private LineBreak			lineBreak;

	UserGamesWriter (GameMap gameList, String xmlFile) {
		games		= gameList;
		outputFile	= xmlFile;
		lineBreak	= LineBreak.UNIX;
	}

	/**
	 * Output an element with proper indentation as
	 * <pre>&lt;tag&gt;value&lt;/tag&gt;</pre>
	 * The line break must be eliminated between the tag elements and the value
	 * element to create the desired standard formatting. The indentation character
	 * and line break must be reestablished after the end tag is written.
	 * @param tag XML entity name
	 * @param value String value of the XML entity
	 */
	private void writeElement(String tag, String value) {
		try {
			pw.startTag(tag);
			pw.setLineBreak(LineBreak.NONE);
			pw.pcdata(value);
			pw.endTag();
			pw.setLineBreak(lineBreak);
			pw.setIndentation("\t");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Outputs a complete usergames.xml file.
	 * @param xmlVersion String containing the AKM XML version
	 * @param osArch String containing the OS and architecture so the correct 
	 * line break is used
	 * @return 0 on success, throws an exception on error.
	 * @throws IOException
	 */
	public int writeUserGamesFile(String xmlVersion, String osArch) throws IOException {
		String	dtdFile = "usergames.dtd";
		
        try {
			pw = new XMLOutputter(new java.io.FileWriter(outputFile, false), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

        try {
    		if (osArch.startsWith("win")) {
    			dtdFile = "data/usergames.dtd";
    			lineBreak = LineBreak.DOS;
    		} else if (osArch.startsWith("linux")) {
    			dtdFile = "usergames.dtd";
    			lineBreak = LineBreak.UNIX;
    		}
        	pw.setLineBreak(lineBreak);
    		pw.setIndentation("\t");
        	pw.declaration();
        	pw.dtd("usergames", null, dtdFile);
			pw.comment("usergames for " + osArch);
			pw.startTag("usergames");
			pw.attribute("version", xmlVersion);

	    	for (Game game = games.getFirst(); game != null; game = games.getNext(game.nodeID)) {
	    		pw.startTag("game");
	    		pw.attribute("id", game.nodeID);
	    		writeElement("displaygame", Boolean.toString(game.isDisplayed));
	    		writeElement("gameinstalled", Boolean.toString(game.isInstalled));
	    		
				VarEntry	wd = game.workingDir.getFirst();
				VarEntry	sc = game.startCommand.getFirst();
				VarEntry	sp = game.startParms.getFirst();

				while (wd != null) {
					pw.startTag("start");
					if (!wd.getKey().equals("default")) {
						pw.attribute("engine", wd.getKey());
					}
					writeElement("workingdir", wd.getValue());
					writeElement("startcommand", sc.getValue());
					writeElement("startparms", sp.getValue());
					
					wd = game.workingDir.getNext(wd.getKey());
					sc = game.startCommand.getNext(sc.getKey());
					sp = game.startParms.getNext(sp.getKey());
					pw.endTag();		// start
				}

				if (game.folders.getCount() > 0) {
					for (int i = 0; i < game.folders.getCount(); i++) {
						Folder folder = (Folder) game.folders.get(i);
						pw.startTag("folder");
						pw.attribute("name", folder.nodeID);
						for (int j = 0; j < folder.serverList.getCount(); j++) {
							Server s = folder.serverList.get(j);
							pw.startTag("server");
							writeElement("address", s.address.getHostAddress());
							writeElement("port", Integer.toString(s.port));
							writeElement("hostname", s.readableHostName);
							pw.endTag();		// server
						}
						pw.endTag();		// folder
					}
				}

				if (game.defaultModParms != null) {
					writeElement("defaultmodparms", game.defaultModParms);
				}

				// Write all of the master server elements
				for (int i = 0; i < game.masterList.getCount(); i++) {
					pw.startTag("master");
					if (game.masterList.get(i).getMasterType().equalsIgnoreCase("q3m")) {
						Q3MasterServer qms = (Q3MasterServer) game.masterList.get(i);
						if (!qms.isEnabled) { 
							pw.attribute("enabled", "no");
						}
						writeElement("url", qms.masterUrl);
						writeElement("masterport", Integer.toString(qms.port));
						for (int j = 0; j < qms.pList.size(); j++) {
							writeElement("protid", qms.pList.get(j).toString());
						}
						if (qms.queryGameName != null) {
							writeElement("queryname", qms.queryGameName);
						}
						writeElement("mastertype", qms.getMasterType());
					} else if (game.masterList.get(i).getMasterType().equalsIgnoreCase("http")) {
						ETQWMasterServer qwms = (ETQWMasterServer) game.masterList.get(i);
						if (!qwms.isEnabled) {
							pw.attribute("enabled", "no");
						}
						writeElement("url", qwms.masterURL);
						writeElement("mastertype", qwms.getMasterType());
					} else {
						throw new IllegalArgumentException("Unknown master server type " + game.masterList.get(i).getMasterType());
					}
					pw.endTag();
				}

				for (int i = 0; i < game.childList.getCount(); i++) {
					Mod mod = (Mod) game.childList.get(i);
					if (!mod.nodeID.equals("other") && !mod.nodeID.equals("unknown")) {
						pw.startTag("mod");
						pw.attribute("id", mod.nodeID);
						
						writeElement("displaymod", Boolean.toString(mod.isDisplayed));
						writeElement("modinstalled", Boolean.toString(mod.isInstalled));
						
						if (mod.workingDir != null) {
							writeElement("moddir", mod.workingDir);
						}
						if (mod.startCommand != null) {
							writeElement("modcommand", mod.startCommand);
						}
						if (mod.startParms != null) {
							writeElement("modparm", mod.startParms);
						}
						pw.endTag();		// mod
					}
				}

				pw.endTag();		// game
	    	}
			
			pw.endTag();		// usergames
			pw.endDocument();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
