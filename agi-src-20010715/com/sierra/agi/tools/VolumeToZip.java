/*
 * VolumeToZip.java
 */

package com.sierra.agi.tools;

import java.io.*;
import com.sierra.agi.res.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class VolumeToZip extends Object
{
    /** Creates new Volume To Zip Converter */
    protected VolumeToZip()
    {
    }
    
    public static void main(String args[]) throws Exception
    {
        AGI          agi    = new AGI(args);
        OutputStream stream = new FileOutputStream("res.zip");
        
        try
        {
            ResourceProviderZip.convertToZip(agi.context, agi.context.cache.getProvider(), stream);
        }
        finally
        {
            System.exit(0);
        }
    }
}