/*
 * BitStream.java
 */

package com.sierra.agi.misc;

import java.io.*;

/**
 * Bit Stream. Used to convert a byte stream into a
 * bit stream.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class BitStream extends Object
{
    /** Source Stream */
    protected InputStream in;
    
    /** End of Stream Flag */
    protected boolean endOfStream = false;
    
    /** Bit Buffer */
    protected long bitBuffer = 0;
    
    /** Bit Count */
    protected short bitCount = (short)0;
    
    /**
     * Creates a new BitStream
     *
     * @param in Source Input Stream.
     */
    public BitStream(InputStream in)
    {
        this.in = in;
    }
    
    protected void fillBuffer() throws IOException
    {
        long b;
        
        while (bitCount <= 52)
        {
            b = in.read();
            
            if (b < 0)
            {
                endOfStream = true;
                return;
            }
            
            b <<= bitCount;
            
            bitBuffer |= b;
            bitCount  += 8;
        }
    }

    public int readBits(int wanted) throws IOException
    {
        int wantedMask = 0;
        int i;
        
        for (i = 0; i < wanted; i++)
        {
            wantedMask <<= 1;
            wantedMask  |= 1;
        }
        
        return readBits(wanted, wantedMask);
    }
    
    public int readBits(int wanted, int wantedMask) throws IOException
    {
        int r;
        
        if (bitCount < wanted)
        {
            fillBuffer();
        }
        
        if ((bitCount == 0) && endOfStream)
        {
            return -1;
        }
        
        if (wanted > bitCount)
        {
            wanted = bitCount;
        }
        
        r           = (int)(bitBuffer & wantedMask);
        bitBuffer >>= wanted;
        bitCount   -= wanted;
        
        return r;
    }
    
    public void close() throws IOException
    {
        in.close();
    }
}