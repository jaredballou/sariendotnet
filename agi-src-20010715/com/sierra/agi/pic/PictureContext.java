/*
 * PictureContext.java
 */

package com.sierra.agi.pic;

import java.util.Arrays;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class PictureContext extends Object
{
    /** Picture data. */
    public byte[] picData = new byte[160 * 168];
    
    /** Priority data. */
    public byte[] priData = new byte[160 * 168];

    /** Picture Picture Color. */
    public byte picColor = -1;

    /** Picture Priority Color. */
    public byte priColor = -1;
    
    /** Picture Width */
    public int width = 160;
    
    /** Picture Height */
    public int height = 168;
    
    /** Pen Style */
    public byte penStyle = 0;
    
    /** Creates new Picture Context. */
    public PictureContext()
    {
        Arrays.fill(picData, (byte)15);
        Arrays.fill(priData, (byte)4);
    }
    
    /**
     * Clips a variable with a maximum.
     *
     * @param v    Variable to be clipped.
     * @param max  Maximum value that the to be clipped variable can have.
     * @return     The Variable clipped.
     */    
    public final int clip(int v, int max)
    {
        if (v > max)
            v = max;
        
        return v;
    }
    
    /**
     * Obtain the index in the buffer where (x,y) is located.
     *
     * @param  x X coordinate.
     * @param  y Y coordinate.
     * @return Index in the buffer.
     */    
    public final int getIndex(int x, int y)
    {
        return (y * width) + x;
    }
    
    /**
     * Obtain the color of the pixel asked.
     *
     * @param  x X coordinate.
     * @param  y Y coordinate.
     * @return Color at the specified pixel.
     */    
    public final byte getPixel(int x, int y)
    {
        return picData[(y * width) + x];
    }

    /**
     * Obtain the priority of the pixel asked. 
     *
     * @param  x X coordinate.
     * @param  y Y coordinate.
     * @return Priority at the specified pixel.
     */    
    public final byte getPriorityPixel(int x, int y)
    {
        return priData[(y * width) + x];
    }
    
    /**
     * Set the (x,y) pixel to the current color and priority.
     *
     * @see #picColor
     * @see #priColor
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public final void putPixel(int x, int y)
    {
        int i;
        
        if ((x >= width) || (y >= height))
        {
            return;
        }
        
        i = (y * width) + x;
        
        if (picColor >= 0)
        {
            picData[i] = picColor;
        }
        
        if (priColor >= 0)
        {
            priData[i] = priColor;
        }
    }
    
    /**
     * Draw a line with current color and current priority.
     *
     * @param x1 Start X Coordinate.
     * @param y1 Start Y Coordinate.
     * @param x2 End X Coordinate.
     * @param y2 End Y Coordinate.
     * @see #picColor
     * @see #priColor
     * @see #putPixel(int,int)
     */
    public final void drawLine(int x1, int y1, int x2, int y2)
    {
        int x, y;
        
        /* Clip! */
        x1 = clip(x1, width-1);
        x2 = clip(x2, width-1);
        y1 = clip(y1, height-1);
        y2 = clip(y2, height-1);
        
        /* Vertical Line */
        if (x1 == x2)
        {
            if (y1 > y2)
            {
                y  = y1;
                y1 = y2;
                y2 = y;
            }
            
            for( ; y1 <= y2; y1++)
            {
                putPixel(x1, y1);
            }
        }
        /* Horizontal Line */
        else if (y1 == y2)
        {
            if (x1 > x2)
            {
                x  = x1;
                x1 = x2;
                x2 = x;
            }

            for( ; x1 <= x2; x1++)
            {
                putPixel(x1, y1);
            }
        }
        else
        { 
            int deltaX = x2 - x1;
            int deltaY = y2 - y1;
            int stepX  = 1;
            int stepY  = 1;
            int detDelta;
            int errorX;
            int errorY;
            int count;
            
            if (deltaY < 0)
            {
                stepY  = -1;
                deltaY = -deltaY;
            }

            if (deltaX < 0)
            {
                stepX  = -1;
                deltaX = -deltaX;
            }

            if (deltaY > deltaX)
            {
                count    = deltaY;
                detDelta = deltaY;
                errorX   = deltaY / 2;
                errorY   = 0;
            }
            else
            {
                count    = deltaX;
                detDelta = deltaX;
                errorX   = 0;
                errorY   = deltaX / 2;
            }

            x = x1;
            y = y1;
            putPixel(x, y);
            
            do
            {
                errorY = (errorY + deltaY);
		if (errorY >= detDelta)
		{
			errorY -= detDelta;
			y      += stepY;
		}

		errorX = (errorX + deltaX);
		if (errorX >= detDelta)
		{
			errorX -= detDelta;
			x      += stepX;
		}
                
		putPixel(x, y);
		count--;
            } while(count > 0);
            
            putPixel(x, y);
        }
    }

    public final boolean isFillCorrect(int x, int y)
    {
	if (picColor < 0 && priColor < 0)
        {
            return false;
        }

	if (priColor < 0 && picColor >= 0 && picColor != 15)
	{
            return (getPixel(x, y) == 15);
	}

	if (priColor >= 0 && picColor < 0 && priColor != 4)
	{
            return (getPriorityPixel(x, y) == 4);
	}

	return (picColor >= 0 && getPixel(x, y) == 15 && picColor != 15);
    }
}