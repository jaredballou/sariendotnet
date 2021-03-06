/*
 * CryptedStream.java
 */

package com.sierra.agi.res.v2;
import  com.sierra.agi.*;
import  java.io.*;

/**
 * Layer to support decryption of sierra's object file.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class CryptedStream extends FilterInputStream
{
    /** Decryption key. */
    protected char key[];
    
    /** Current offset. */
    protected int offset;
    
    /**
     * Offset of the last <CODE>mark</CODE> method call.
     *
     * @see #mark(int)
     */
    protected int marked;

    /**
     * Creates a new decryption layer.
     *
     * @param key    Decryption key.
     * @param stream <CODE>InputStream</CODE> to decrypt.
     */
    public CryptedStream(String key, InputStream stream)
    {
        super(stream);
        this.key = key.toCharArray();
    }

    public void mark(int readlimit)
    {
        in.mark(readlimit);
        marked = offset;
    }

    public void reset() throws IOException
    {
        in.reset();
        offset = marked;
    }

    public long skip(long n) throws IOException
    {
        long r = in.skip(n);
        
        offset += r;
        return r;
    }

    public int read() throws IOException
    {
        int r = in.read();
        
        if (r < 0)
            return r;

        r ^= key[offset % key.length]; 
        
        offset++;
        return r;
    }

    public int read(byte[] b, int off, int len) throws IOException
    {
        int i, j, off2;
        int r = in.read(b, off, len);
        
        if (r < 0)
            return r;
        
        off2 = off + len;
        for (i = off; i < off2; i++)
        {
            j  = (b[i] & 0xFF);
            j ^= key[offset % key.length];
            
            b[i] = (byte)j;
            offset++;
        }

        return r;
    }
}