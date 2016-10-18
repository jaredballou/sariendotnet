/*
 * VersionTable.java
 */

package com.sierra.agi.misc;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import com.sierra.agi.Context;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public abstract class VersionTable extends Object
{
    public static void lookup(Context context, InputStream stream, long gameCRC)
    {
        Properties props = new Properties();
        String     crc   = "0x" + Long.toString(gameCRC, 16);
        String     ver;

        try
        {
            props.load(stream);
        }
        catch (IOException ioex)
        {
        }
        
        ver = props.getProperty(crc, "0x2917");

        context.setAmiga(ver.indexOf('a') != -1);
        context.setAGDS(ver.indexOf('g') != -1);
        ver = ver.substring(2);

        while (!Character.isDigit(ver.charAt(ver.length() - 1)))
        {
            ver = ver.substring(0, ver.length() - 1);
        }

        context.setEngineVersion(Integer.valueOf(ver, 16).intValue());
    }
}