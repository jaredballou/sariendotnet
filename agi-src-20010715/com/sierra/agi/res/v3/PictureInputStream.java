/*
 * PictureInputStream.java
 */

package com.sierra.agi.res.v3;

import java.io.*;

/**
 * Picture Input Stream.
 * <P>
 * Pictures in AGI version 3 use a simple form of compression to shrink their
 * size my a tiny amount. It was obviously recognised by the interpreter coders
 * that four bits were being wasted for picture codes <CODE>0xF0</CODE> and
 * <CODE>0xF2</CODE>. These are the two codes that change the visual and the
 * priority colour respectively. Since there are only 16 colours, there need
 * not be a whole byte set aside for storing the colour. All the picture
 * compression does is store these colours in 4 bits rather than 8.
 * </P><P>
 * Example:<BR>
 * Original picture codes: <CODE>F0 06 F8 12 45 F0 07 F2 05 F8 14 67 ...</CODE><BR>
 * Compressed picture code: <CODE>F0 6F 81 24 5F 07 F2 5F 81 46 7 ...</CODE>
 * </P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class PictureInputStream extends InputStream
{
    /** Previous Byte */
    int previous;
    
    /** Current Byte */
    int current;

    int mode = 0;
    
    /** Source Stream */
    InputStream in;
    
    /**
     * Creates new Picture Input Stream
     */
    public PictureInputStream(InputStream in)
    {
        this.in = in;
    }
    
    public int read() throws IOException
    {
        int x = 0, y;

        if (in == null)
        {
            return -1;
        }
        
        if (mode <= 1)
        {
            current = in.read();

            if (mode == 0)
            {
                x = current;
            }
            else
            {
                x   = (current & 0xf0);
                x >>= 4;
                y   = (previous & 0x0f);
                y <<= 4;
                x  |= y;
            }
        
            if (x == 0xff)
            {
                close();
                return -1;
            }

            if (x == 0xf0 || x == 0xf2)
            {
                if (mode == 1)
                {
                    mode = 2;
                }
                else
                {
                    mode = 3;
                }
            }
        }
        else if (mode == 2)
        {
            mode = 0;
            return current & 0x0f;
        }
        else if (mode == 3)
        {
            mode    = 1;
            current = in.read();
            x       = current & 0xf0;
            x     >>= 4;
        }
        
        previous = current;
        return x;
    }
    
    public void close() throws IOException
    {
        if (in != null)
        {
            in.close();
            in = null;
        }
    }
}