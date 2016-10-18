/*
 * DirectoryEntry.java
 */

package com.sierra.agi.res.v3;
import  java.io.*;
import  com.sierra.agi.res.*;
import  com.sierra.agi.misc.*;

/**
 * A resource's directory entry.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class DirectoryEntry extends com.sierra.agi.res.v2.DirectoryEntry
{
    /**
     * Length of the compressed resource. Do not access directly use
     * the <CODE>getLength</CODE> method.
     *
     * @see #getLength()
     */
    public int compressedLength;
    
    /**
     * Retreive the length of the resource. The first time
     * it is called, it calculate the size.
     *
     * @return Returns the length of the resource.
     */
    public int getLength() throws ResourceException
    {
        if (length < 0)
        {
            try
            {
                byte        b[] = new byte[7];
                InputStream in;

                in = new FileInputStream(file);
                in.skip(offset);
                in.read(b);

                if ((b[0] != 0x12) || (b[1] != 0x34))
                {
                    throw new CorruptedResourceException();
                }
                
                length = ByteCaster.lohiUnsignedShort(b, 3);
                compressedLength = ByteCaster.lohiUnsignedShort(b, 5);
                
                in.close();
            }
            catch (IOException ioex)
            {
                throw new CorruptedResourceException();
            }
        }
        
        return length;
    }

    public int getPhysicalLength() throws ResourceException
    {
        if (length < 0)
        {
            getLength();
        }
        
        return compressedLength;
    }
    
    public int getOffset()
    {
        return offset + 7;
    }
}