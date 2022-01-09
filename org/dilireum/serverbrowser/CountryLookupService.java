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

import java.io.IOException;

import org.dilireum.logging.SysLogger;

import com.maxmind.geoip.*;

public class CountryLookupService {

	private String dbfile;
	private LookupService cl;
	
	public CountryLookupService(String dbPath) {

	    String sep = System.getProperty("file.separator");
	    dbfile = dbPath + sep + "GeoIP.dat"; 
		
	    try {
		    // You should only call LookupService once, especially if you use
		    // GEOIP_MEMORY_CACHE mode, since the LookupService constructor takes up
		    // resources to load the GeoIP.dat file into memory
		    //LookupService cl = new LookupService(dbfile,LookupService.GEOIP_STANDARD);
		    cl = new LookupService(dbfile,LookupService.GEOIP_MEMORY_CACHE);
		}
		catch (IOException e) {
		    SysLogger.logMsg(0, "Error in findServerCountry while opening " + dbfile);
		}
	}

	public void finalize() {
		close();
	}
	
	public void close() {
		cl.close();
	}
	
	public String findServerCountry(String ipAddress) {
	    String country;

	    country = cl.getCountry(ipAddress).getName();
	    SysLogger.logMsg(6, "Location: " + country);
	    return country;
	}

}
