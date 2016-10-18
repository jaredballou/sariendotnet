/*
 * Cell.java
 */

package com.sierra.agi.view;

import com.sierra.agi.Context;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.misc.ByteCaster;
import java.awt.*;
import java.awt.image.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class Cell extends Object
{
    /** Cell's Width */
    public short width;
    
    /** Cell's Height */
    public short height;
    
    /** Cell's Data */
    public byte data[];
    
    /** Cached Image */
    protected Image img;
    
    /** Game Context */
    protected Context context;
    
    /** Creates new Cell */
    public Cell(Context context, byte b[], int start, int loopNumber)
    {
        short trans;
        short mirrorInfo;
        byte  transColor;
        
        this.context = context;
        width        = ByteCaster.lohiUnsignedByte(b, start);
        height       = ByteCaster.lohiUnsignedByte(b, start + 1);
        trans        = ByteCaster.lohiUnsignedByte(b, start + 2);
        
        transColor  = (byte)(trans & 0x0F);
        mirrorInfo  = (short)((trans & 0xF0) >> 4);
        
        loadData(b, start + 3, transColor);
        
        if ((mirrorInfo & 0x8) == 0x8)
        {
            if ((mirrorInfo & 0x7) != loopNumber)
            {
                mirror();
            }
        }
    }
    
    protected void loadData(byte b[], int off, byte transColor)
    {
        int  i, j, x, y;
        byte color;
        byte count;
        
        data = new byte[width * height];
        
        for (j = 0, y = 0; y < height; y++)
        {
            for (x = 0; b[off] != 0; off++)
            {
                color = (byte)((b[off] & 0xF0) >> 4);
                count = (byte)(b[off] & 0xF);
                
                if (color == transColor)
                {
                    color = 16;
                }
                
                for (i = 0; i < count; i++, j++, x++)
                {
                    data[j] = color;
                }
            }
            
            for (; x < width; j++, x++)
            {
                data[j] = 16;
            }
            
            off++;
        }
    }

    protected void mirror()
    {
        int  i1, i2, x1, x2, y;
        byte b;
        
        for (y = 0; y < height; y++)
        {
            for (x1 = width - 1, x2 = 0; x1 > x2; x1--, x2++)
            {
                i1 = (y * width) + x1;
                i2 = (y * width) + x2;
                
                b        = data[i1];
                data[i1] = data[i2];
                data[i2] = b;
            }
        }
    }
    
    /**
     * Obtain an standard Image object that is a graphical representation of the
     * cell.
     *
     * @param context Game context used to generate the image.
     */
    public Image getImage()
    {
        if (img == null)
        {
            MemoryImageSource   mis;
            FilteredImageSource fis;
        
            mis = new MemoryImageSource(width, height, EgaUtils.getColorModel(), data, 0, width);
            fis = new FilteredImageSource(mis, new ReplicateScaleFilter(
                        width  * context.getZoomW(),
                        height * context.getZoomH()));
            img = context.getToolkit().createImage(fis);
        }
        
        return img;
    }
    
    /**
     * Release Image. If <CODE>getImage</CODE> is called again, the image will
     * be regenerated.
     */
    public void flushImage()
    {
        if (img != null)
        {
            img.flush();
            img = null;
        }
    }
}