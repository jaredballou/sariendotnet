/*
 * EgaUtil.java
 */

package com.sierra.agi.awt;
import java.awt.image.*;

/**
 * Misc. Utility for EGA support in Java's AWT.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public abstract class EgaUtils extends Object
{
    protected static ColorModel model;

    protected static final byte[] r = {0x00,0x00,0x00,0x00,0x2A,0x2A,0x2A,0x2A,0x15,0x15,0x15,0x15,0x3F,0x3F,0x3F,0x3F,0x00};
    protected static final byte[] g = {0x00,0x00,0x2A,0x2A,0x00,0x00,0x15,0x2A,0x15,0x15,0x3F,0x3F,0x15,0x15,0x3F,0x3F,0x00};
    protected static final byte[] b = {0x00,0x2A,0x00,0x2A,0x00,0x2A,0x00,0x2A,0x15,0x3F,0x15,0x3F,0x15,0x3F,0x15,0x3F,0x00};
    
    public static synchronized ColorModel getColorModel()
    {
        int i;
        
        if (model == null)
        {
            for (i = 0; i < 16; i++)
            {
                r[i] <<= 2;
                g[i] <<= 2;
                b[i] <<= 2;
            }
            
            model = new IndexColorModel(8, 17, r, g, b, 16);
        }
        
        return model;
    }
}