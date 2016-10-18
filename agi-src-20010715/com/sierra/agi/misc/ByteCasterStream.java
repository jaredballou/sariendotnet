/*
 * UniDataInputStream.java
 *
 * Created on 12 janvier 2001, 10:24
 */

package com.sierra.agi.misc;
import  java.io.*;

/**
 * Interprets stream.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ByteCasterStream extends FilterInputStream
{
    public ByteCasterStream(InputStream stream)
    {
        super(stream);
    }
    
    public short hiloReadUnsignedByte() throws IOException
    {
        int v = in.read();
        
        if (v < 0)
            throw new EOFException();
        
        return (short)v;
    }
    
    public int hiloReadUnsignedShort() throws IOException
    {
        byte b[] = new byte[2];
        
        fill(b, 0, 2);
        
        return ((b[0] & 0xFF) << 8) |
                (b[1] & 0xFF);
    }

    public long hiloReadUnsignedInt() throws IOException
    {
        byte b[] = new byte[4];
        
        fill(b, 0, 4);
        
        return ((b[0] & 0xFF) << 24) |
               ((b[1] & 0xFF) << 16) |
               ((b[2] & 0xFF) << 8)  |
                (b[3] & 0xFF);
    }

    public short lohiReadUnsignedByte() throws IOException
    {
        int v = in.read();
        
        if (v < 0)
            throw new EOFException();
        
        return (short)v;
    }
    
    public int lohiReadUnsignedShort() throws IOException
    {
        byte b[] = new byte[2];
        
        fill(b, 0, 2);
        
        return ((b[1] & 0xFF) << 8) |
                (b[0] & 0xFF);
    }

    public long lohiReadUnsignedInt() throws IOException
    {
        byte b[] = new byte[4];
        
        fill(b, 0, 4);
        
        return ((b[3] & 0xFF) << 24) |
               ((b[2] & 0xFF) << 16) |
               ((b[1] & 0xFF) << 8)  |
                (b[0] & 0xFF);
    }
    
    protected final void fill(byte b[], int off, int len) throws IOException
    {
        int c;
        
        while (len != 0)
        {
            c = in.read(b, off, len);

            if (c <= 0)
            {
                throw new EOFException();
            }
            
            off += c;
            len -= c;
        }
    }
}