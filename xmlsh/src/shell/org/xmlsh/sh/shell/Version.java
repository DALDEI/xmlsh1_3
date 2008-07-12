/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.sh.shell;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Version {
	
	private static Properties mProperties ;
	static Logger mLogger = Logger.getLogger(Version.class);
	static {
		mProperties = new Properties();
		try {
			mProperties.load( Version.class.getResourceAsStream("version.properties"));
		} catch (IOException e) {
			mLogger.debug("Exception loading version.properties",e);
		}
	}
	
	
	
	public static String getBuildDate()
	{
		
		if( mProperties == null )
		
			return "";
		else
			return mProperties.getProperty("version.build_date");
	}
	
	public static String getRelease()
	{
		if( mProperties == null )
			
			return "";
		else
			return mProperties.getProperty("version.release");
	}
	
}
