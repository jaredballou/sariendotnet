/*
 * DirectoryEntryStream.java
 *
 * Created on 10 janvier 2001, 20:50
 */

package com.sierra.agi.res.v2;
import  com.sierra.agi.misc.*;
import  com.sierra.agi.res.ResourceException;
import  java.io.*;

/**
 * Implentation of <CODE>InputStream</CODE> that gives access
 * to a logic inside a volume file.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class LogicInputStream extends FilterInputStream
{
    /** Crypted Offset. */
    protected int offsetCrypted;
    
    /** Key. */
    protected String key;
    
    /** Crypted Stream. */
    protected CryptedStream cryptedStream = null;
    
    /**
     * Creates a new DirectoryEntryLogicStream.
     *
     * @param  pEntry      Pointer to a entry in the directory table.
     * @throws IOException Can throw IOException.
     */
    public LogicInputStream(String key,DirectoryEntry entry) throws IOException, ResourceException
    {
        super(new DirectoryEntryStream(entry));
        
        DirectoryEntryStream dstream    = (DirectoryEntryStream)in;
        RandomAccessFile     randomFile = dstream.getRandomAccessFile();
        
        byte b[]    = new byte[3];
        int  offset = dstream.getOffset();
        int  startPos, numMessages;

        this.key = key;
        
        /* Determining where the crypted data starts. */
        randomFile.read(b, 0, 2);
        startPos = ByteCaster.lohiUnsignedShort(b, 0) + 2;
        randomFile.seek(offset + startPos);
        randomFile.read(b, 0, 3);
        numMessages   = ByteCaster.lohiUnsignedByte(b, 0);
        offsetCrypted = startPos + 3 + (numMessages * 2) + offset;
        
        /* Replacing the offset at his original place. */
        randomFile.seek(offset);
        
        cryptedStream = new CryptedStream(key, in);
    }
  
    public int read() throws IOException
    {
        int offset = ((DirectoryEntryStream)in).getOffset();
        
        if (offset >= offsetCrypted)
        {
            return cryptedStream.read();
        }
        else
        {
            return in.read();
        }
    }
    
    public int read(byte b[]) throws IOException
    {
        return read(b, 0, b.length);
    }
    
    public int read(byte b[], int off, int len) throws IOException
    {
        int offset = ((DirectoryEntryStream)in).getOffset();
        int l, m, n;

        if (offset < offsetCrypted)
        {
            l = len;
            
            if ((offset + l) >= offsetCrypted)
            {
                l = offsetCrypted - offset;
            }
            
            n = in.read(b, off, l);
            
            if (n < 0)
                return n;
            
            off += n;
            len -= n;
            
            if (len > 0)
            {
                m = cryptedStream.read(b, off, len);
                
                if (m > 0)
                {
                    n += m;
                }
            }
            
            return n;
        }
        else
        {
            return cryptedStream.read(b, off, len);
        }
    }
}