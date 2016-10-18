/*
 * Picture.java
 */

package com.sierra.agi.pic;

import com.sierra.agi.Context;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.misc.PointStack;
import com.sierra.agi.view.Cell;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class Picture extends Object
{
    /** Game Context */
    protected Context context;

    /** Input Stream */
    protected InputStream in;
    
    /** Picture Context */
    protected PictureContext picContext = new PictureContext();
    
    /** Creates new Picture */
    protected Picture(Context context, InputStream stream) throws PictureException
    {
        this.context = context;
        this.in      = stream;
    }
    
    public static Picture loadPicture(Context context, InputStream stream) throws PictureException
    {
        return new Picture(context, stream);
    }

    protected int nextCommand = -1;

    protected static final short CMD_START = (short)0xF0;
    
    protected static final short CMD_CHANGEPICCOLOR = (short)0xF0;
    protected static final short CMD_DISABLEPICDRAW = (short)0xF1;
    protected static final short CMD_CHANGEPRICOLOR = (short)0xF2;
    protected static final short CMD_DISABLEPRIDRAW = (short)0xF3;
    protected static final short CMD_DRAWYCORNER    = (short)0xF4;
    protected static final short CMD_DRAWXCORNER    = (short)0xF5;
    protected static final short CMD_DRAWABSLINE    = (short)0xF6;
    protected static final short CMD_DRAWRELLINE    = (short)0xF7;
    protected static final short CMD_FILL           = (short)0xF8;
    protected static final short CMD_CHANGEPEN      = (short)0xF9;
    protected static final short CMD_PLOT           = (short)0xFA;
    protected static final short CMD_EOP            = (short)0xFF;
    
    public synchronized boolean next() throws PictureException
    {
        if (in == null)
        {
            return false;
        }
        
        try
        {
            if (nextCommand < 0)
            {
                nextCommand = in.read();

                if (nextCommand < 0)
                {
                    in.close();
                    in = null;
                    return false;
                }
            }

            switch (nextCommand)
            {
            case CMD_CHANGEPICCOLOR:
                picContext.picColor = (byte)in.read();
                nextCommand = -1;
                break;

            case CMD_CHANGEPRICOLOR:
                picContext.priColor = (byte)in.read();
                nextCommand = -1;
                break;

            case CMD_DISABLEPICDRAW:
                picContext.picColor = (byte)-1;
                nextCommand = -1;
                break;

            case CMD_DISABLEPRIDRAW:
                picContext.priColor = (byte)-1;
                nextCommand = -1;
                break;

            case CMD_DRAWXCORNER:
                drawXCorner();
                break;

            case CMD_DRAWYCORNER:
                drawYCorner();
                break;

            case CMD_DRAWABSLINE:
                drawAbsoluteLine();
                break;

            case CMD_DRAWRELLINE:
                drawRelativeLine();
                break;

            case CMD_FILL:
                drawFill();
                break;

            case CMD_CHANGEPEN:
                picContext.penStyle = (byte)in.read();
                nextCommand = -1;
                break;

            case CMD_PLOT:
                drawPlot();
                break;

            case CMD_EOP:
                in.close();
                in = null;
                break;

            default:
                throw new CorruptedPictureException();
            }
        }
        catch (IOException ioex)
        {
            in = null;
        }
        
        if (imgSource != null)
        {
            imgSource.newPixels();
        }
        
        return true;
    }
    
    /**
     * <P><B><CODE>0xF5</CODE></B>: Draw an X corner</P>
     *
     * <P>Function: The first two arguments for this action are the coordinates of
     * the starting position on the screen in the order x and then y. The remaining
     * arguments are in the order x1, y1, x2, y2, ...
     * </P><P>
     * Note that the x component is the first to be changed and also note that this
     * action does not necessarily end on either component, it just ends when the
     * next byte of 0xF0 or above is encountered. A line is drawn after each byte
     * is processed.
     * </P><P>
     * Example: <CODE>F5 16 16 18 12 16 F?</CODE>
     * </P><PRE>
     * (0x16, 0x12)   (0x18, 0x12)
     *             EXX
     *               X            S = Start
     *               X            E = End
     *               X            X = normal piXel
     *             SXX
     * (0x16, 0x16)   (0x18, 0x16)</PRE>
     */
    protected void drawXCorner() throws IOException
    {
        int     x1, y1, x2, y2, c;
        boolean b = false;
        
        x1 = x2 = in.read();
        y1 = y2 = in.read();
        
        while (true)
        {
            c = in.read();
            
            if ((c < 0) || (c >= CMD_START))
            {
                nextCommand = c;
                break;
            }
            
            if (b)
            {
                x2 = c;
            }
            else
            {
                y2 = c;
            }
            
            picContext.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
            b  = !b;
        }
    }

    /**
     * <P><B><CODE>0xF4</CODE></B>: Draw a Y corner</P>
     * <P>
     * Function: The first two arguments for this action are the coordinates of
     * the starting position on the screen in the order x and then y. The remaining
     * arguments are in the order y1, x1, y2, x2, ...
     * </P><P>
     * Note that the y component is the first to be changed and also note that this
     * action does not necessarily end on either component, it just ends when the
     * next byte of 0xF0 or above is encountered. A line is drawn after each byte
     * is processed.
     * </P><P>
     * Example: <CODE>F4 16 16 18 12 16 F? </CODE>
     * <PRE>
     * (0x12, 0x16)     (0x16, 0x16)
     *            E   S                  S = Start
     *            X   X                  E = End
     *            XXXXX                  X = normal piXel
     * (0x12, 0x18)     (0x16, 0x18)</PRE></P>
     */
    protected void drawYCorner() throws IOException
    {
        int     x1, y1, x2, y2, c;
        boolean b = false;
        
        x1 = x2 = in.read();
        y1 = y2 = in.read();
        
        while (true)
        {
            c = in.read();
            
            if ((c < 0) || (c >= CMD_START))
            {
                nextCommand = c;
                break;
            }
            
            if (b)
            {
                y2 = c;
            }
            else
            {
                x2 = c;
            }
            
            picContext.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
            b  = !b;
        }
    }

    /**
     * <P><B><CODE>0xF6</CODE></B>: Absolute line</P>
     * <P>
     * Function: Draws lines between points. The first two arguments are the
     * starting coordinates. The remaining arguments are in groups of two which
     * give the coordinates of the next location to draw a line to. There can be
     * any number of arguments but there should always be an even number.
     * </P><P>
     * Example: <CODE>F6 30 50 34 51 38 53 F?</CODE>
     * </P><P>
     * This sequence draws a line from (48, 80) to (52, 81), and a line from
     * (52, 81) to (56, 83).
     * </P>
     */
    protected void drawAbsoluteLine() throws IOException
    {
        int x1, y1, x2, y2, c;

        x1 = in.read();
        y1 = in.read();
        
        while (true)
        {
            c = in.read();
            
            if ((c < 0) || (c >= CMD_START))
            {
                nextCommand = c;
                break;
            }
            
            x2 = c;
            y2 = in.read();
            picContext.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }
    }

    /**
     * <P><B><CODE>0xF7</CODE></B>: Relative line</P>
     * <P>
     * Function: Draw short relative lines. By relative we mean that the data gives
     * displacements which are relative from the current location. The first
     * argument gives the standard starting coordinates. All the arguments which
     * follow these first two are of the following format:
     * </P><PRE>
     * +---+-----------+---+-----------+
     * | S |   Xdisp   | S |   Ydisp   |
     * +---+---+---+---+---+---+---+---+
     * | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
     * +---+---+---+---+---+---+---+---+</PRE>
     * <P>
     * This gives a displacement range of between -7 and +7 for both the X and the Y
     * direction.
     * </P><P>
     * Example: <CODE>F7 10 10 22 40 06 CC F?</CODE>
     * </P><PRE>
     *             S
     *              +              S = Start
     *               X+++X         X = End of each line
     *                   +         + = pixels in each line
     *               E   +         E = End
     *                +  +
     *                 + +         Remember that CC = (x-4, y-4).
     *                  ++
     *                   X</PRE>
     */
    protected void drawRelativeLine() throws IOException
    {
        int x1, y1, x2, y2, c;
        
        x1 = in.read();
        y1 = in.read();
        
        while (true)
        {
            c = in.read();
            
            if ((c < 0) || (c >= CMD_START))
            {
                nextCommand = c;
                break;
            }
            
            x2 = (c & 0x70) >> 4;
            y2 = (c & 0x07);
            
            if ((c & 0x80) == 0x80)
            {
                x2 = -x2;
            }
            
            if ((c & 0x08) == 0x08)
            {
                y2 = -y2;
            }
            
            x2 += x1;
            y2 += y1;
            picContext.drawLine(x1, y1, x2, y2);
            
            x1 = x2;
            y1 = y2;
        }
    }

    /**
     * <P><B><CODE>0xF8</CODE></B>: Fill</P>
     * <P>
     * Function: Flood fill from the locations given. Arguments are given in groups
     * of two bytes which give the coordinates of the location to start the fill
     * at. If picture drawing is enabled then it flood fills from that location on
     * the picture screen to all pixels locations that it can reach which are white
     * in colour. The boundary is given by any pixels which are not white.
     * </P><P>
     * If priority drawing is enabled, and picture drawing is not enabled, then it
     * flood fills from that location on the priority screen to all pixels that it
     * can reach which are red in colour. The boundary in this case is given by any
     * pixels which are not red.
     * </P><P>
     * If both picture drawing and priority drawing are enabled, then a flood fill
     * naturally enough takes place on both screens. In this case there is a
     * difference in the way the fill takes place in the priority screen. The
     * difference is that it not only looks for its own boundary, but also stops if
     * it reaches a boundary that exists in the picture screen but does not
     * necessarily exist in the priority screen.
     * </P>
     */
    protected void drawFill() throws IOException
    {
        int        x, y, c;
        Point      current = new Point();
        PointStack stack   = new PointStack(200, 200);
        int        width   = picContext.width  - 1;
        int        height  = picContext.height - 1;
        
        while (true)
        {
            c = in.read();
            
            if ((c < 0) || (c >= CMD_START))
            {
                nextCommand = c;
                break;
            }
            
            x = c;
            y = in.read();
            stack.push(x, y);

            try
            {
                while (true)
                {
                    stack.pop(current);
                    
                    if (picContext.isFillCorrect(current.x, current.y))
                    {
                        picContext.putPixel(current.x, current.y);

                        if (current.x > 0 && picContext.isFillCorrect(current.x - 1, current.y))
                        {
                            stack.push(current.x - 1, current.y);
                        }

                        if (current.x < width && picContext.isFillCorrect(current.x + 1, current.y))
                        {
                            stack.push(current.x + 1, current.y);
                        }

                        if (current.y < height && picContext.isFillCorrect(current.x, current.y + 1))
                        {
                            stack.push(current.x, current.y + 1);
                        }

                        if (current.y > 0 && picContext.isFillCorrect(current.x, current.y - 1))
                        {
                            stack.push(current.x, current.y - 1);
                        }
                    }
                }
            }
            catch (EmptyStackException esex)
            {
            }
        }
    }

    /** Circle Bitmaps */
    protected static final short circles[][] = new short[][]
    {
        { 0x80 },
        { 0xfc },
        { 0x5f, 0xf4 },
        { 0x66, 0xff, 0xf6, 0x60 },
        { 0x23, 0xbf, 0xff, 0xff, 0xee, 0x20 },
        { 0x31, 0xe7, 0x9e, 0xff, 0xff, 0xde, 0x79, 0xe3, 0x00 },
        { 0x38, 0xf9, 0xf3, 0xef, 0xff, 0xff, 0xff, 0xfe, 0xf9, 0xf3, 0xe3, 0x80 },
        { 0x18, 0x3c, 0x7e, 0x7e, 0x7e, 0xff, 0xff, 0xff, 0xff, 0xff, 0x7e, 0x7e, 0x7e, 0x3c, 0x18 }
    };

    /** Splatter Brush Bitmaps */
    protected static final short splatterMap[] = new short[]
    {
        0x20, 0x94, 0x02, 0x24, 0x90, 0x82, 0xa4, 0xa2,
        0x82, 0x09, 0x0a, 0x22, 0x12, 0x10, 0x42, 0x14,
        0x91, 0x4a, 0x91, 0x11, 0x08, 0x12, 0x25, 0x10,
        0x22, 0xa8, 0x14, 0x24, 0x00, 0x50, 0x24, 0x04
    };

    /** Starting Bit Position */
    protected static final short splatterStart[] = new short[]
    {	
        0x00, 0x18, 0x30, 0xc4, 0xdc, 0x65, 0xeb, 0x48,
        0x60, 0xbd, 0x89, 0x05, 0x0a, 0xf4, 0x7d, 0x7d,
        0x85, 0xb0, 0x8e, 0x95, 0x1f, 0x22, 0x0d, 0xdf,
        0x2a, 0x78, 0xd5, 0x73, 0x1c, 0xb4, 0x40, 0xa1,
        0xb9, 0x3c, 0xca, 0x58, 0x92, 0x34, 0xcc, 0xce,
        0xd7, 0x42, 0x90, 0x0f, 0x8b, 0x7f, 0x32, 0xed,
        0x5c, 0x9d, 0xc8, 0x99, 0xad, 0x4e, 0x56, 0xa6,
        0xf7, 0x68, 0xb7, 0x25, 0x82, 0x37, 0x3a, 0x51,
        0x69, 0x26, 0x38, 0x52, 0x9e, 0x9a, 0x4f, 0xa7,
        0x43, 0x10, 0x80, 0xee, 0x3d, 0x59, 0x35, 0xcf,
        0x79, 0x74, 0xb5, 0xa2, 0xb1, 0x96, 0x23, 0xe0,
        0xbe, 0x05, 0xf5, 0x6e, 0x19, 0xc5, 0x66, 0x49,
        0xf0, 0xd1, 0x54, 0xa9, 0x70, 0x4b, 0xa4, 0xe2,
        0xe6, 0xe5, 0xab, 0xe4, 0xd2, 0xaa, 0x4c, 0xe3,
        0x06, 0x6f, 0xc6, 0x4a, 0xa4, 0x75, 0x97, 0xe1
    };
    
    protected void drawPlot() throws IOException
    {
        int c, x, y;
        
        while (true)
        {
            c = in.read();
            
            if ((c < 0) || (c >= CMD_START))
            {
                nextCommand = c;
                break;
            }
            
            if ((picContext.penStyle & 0x20) == 0x20)
            {
                c = (c >> 1) & 0x7f;
                x = in.read();
                y = in.read();
                drawPlot(c, x, y);
            }
            else
            {
                x = c;
                y = in.read();
                drawPlot(x, y);
            }
        }
    }
    
    protected void drawPlot(int patternNumber, int x, int y)
    {
	int     circlePos = 0;
        int     bitPos    = splatterStart[patternNumber];
	int     x1, y1, penSize, penSizeTrue;
        boolean circle;

        circle      = !((picContext.penStyle & 0x10) == 0x10);
        penSize     = (picContext.penStyle & 0x07);
        penSizeTrue = penSize;

	if (x < penSize)
        {
            x = penSize - 1;
        }
        
	if (y < penSize)
        {
            y = penSize;
        }

	for (y1 = y - penSize; y1 <= y + penSize; y1++)
        {
            for (x1 = x - (penSize + 1) / 2; x1 <= x + penSize / 2; x1++)
            {
                if (circle)
                {
                    if (!(((circles[penSizeTrue][circlePos >> 0x3] >> (0x7 - (circlePos & 0x7))) & 0x1) == 0x1))
                    {
                        circlePos++;
                        continue;
                    }
                    
                    circlePos++;
                }
                
                if (((splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) == 1)
                {
                    picContext.putPixel(x1, y1);
                }

                bitPos++;

                if (bitPos == 0xff)
                {
                    bitPos = 0;
                }
            }
	}
    }
    
    protected void drawPlot(int x, int y)
    {
	int     circlePos = 0;
	int     x1, y1, penSize, penSizeTrue;
        boolean circle;

        circle      = !((picContext.penStyle & 0x10) == 0x10);
        penSize     = (picContext.penStyle & 0x07);
        penSizeTrue = penSize;

	if (x < penSize)
        {
            x = penSize - 1;
        }
        
	if (y < penSize)
        {
            y = penSize;
        }

	for (y1 = y - penSize; y1 <= y + penSize; y1++)
        {
            for (x1 = x - (penSize + 1) / 2; x1 <= x + penSize / 2; x1++)
            {
                if (circle)
                {
                    if (!(((circles[penSizeTrue][circlePos >> 0x3] >> (0x7 - (circlePos & 0x7))) & 0x1) == 0x1))
                    {
                        circlePos++;
                        continue;
                    }
                    
                    circlePos++;
                }

                picContext.putPixel(x1, y1);
            }
	}
    }
    
    protected Image img;
    
    protected MemoryImageSource imgSource;

    public byte[] getPictureData() throws PictureException
    {
        if (in != null)
        {
            while (next())
                ;
        }

        return picContext.picData;
    }
    
    public byte[] getPriorityData() throws PictureException
    {
        if (in != null)
        {
            while (next())
                ;
        }
        
        return picContext.priData;
    }
    
    public Image getImage() throws PictureException
    {
        if (img == null)
        {
            MemoryImageSource   mis;
            FilteredImageSource fis;
        
            mis = new MemoryImageSource(160, 168, EgaUtils.getColorModel(), getPictureData(), 0, 160);
            fis = new FilteredImageSource(mis, new ReplicateScaleFilter(
                        160 * context.getZoomW(),
                        168 * context.getZoomH()));
            img = context.getToolkit().createImage(fis);
        }
        
        return img;
    }
    
    public Image getImage(boolean animated) throws PictureException
    {
        if (!animated)
        {
            return getImage();
        }
        
        if (img == null)
        {
            MemoryImageSource   mis;
            FilteredImageSource fis;
        
            mis = new MemoryImageSource(160, 168, EgaUtils.getColorModel(), picContext.picData, 0, 160);
            mis.setAnimated(true);
            fis = new FilteredImageSource(mis, new ReplicateScaleFilter(
                        160 * context.getZoomW(),
                        168 * context.getZoomH()));
            img = context.getToolkit().createImage(fis);
            
            imgSource = mis;
        }
        
        return img;
    }
}