/*
 * DirectoryEntry.java
 */

package com.sierra.agi.res.v2;
import  java.io.*;
import  com.sierra.agi.res.*;
import  com.sierra.agi.misc.*;

/**
 * A resource's directory entry.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class DirectoryEntry extends Object
{
    /** Volume that contain the entry. */
    public File file;
    
    /** Volume ID. */
    public byte volume;
    
    /** Offset of the resource inside the volume. */
    public int offset;
    
    /**
     * Length of the resource. Do not access directly use
     * the <CODE>getLength</CODE> method.
     *
     * @see #getLength()
     */
    public int length = -1;
    
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
                byte        b[] = new byte[5];
                InputStream in;

                in = new FileInputStream(file);
                in.skip(offset);
                in.read(b);

                if ((b[0] != 0x12) || (b[1] != 0x34))
                {
                    throw new CorruptedResourceException();
                }
                
                length = ByteCaster.lohiUnsignedShort(b, 3);
                
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
        return getLength();
    }
    
    public int getOffset()
    {
        return offset + 5;
    }
}