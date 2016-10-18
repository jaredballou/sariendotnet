/*
 * BufferedCanvas.java
 */

package com.sierra.agi.awt;
import  java.awt.*;
import  java.awt.event.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class BufferedCanvas extends Canvas implements ComponentListener
{
    protected Dimension offDimension;
    protected Graphics  offGraphics;
    protected Image     offImage;

    /** Creates new BufferedCanvas */
    public BufferedCanvas()
    {
        addComponentListener(this);
    }
    
    public boolean isDoubleBuffered()
    {
        return true;
    }
    
    public final void paint(Graphics g)
    {
        update(g);
    }
    
    public final void update(Graphics g)
    {
        if (offGraphics == null)
        {
            generateBuffer();
            generate(offGraphics);
        }

        g.drawImage(offImage, 0, 0, null);
    }
    
    protected final void generateBuffer()
    {
        offDimension = getSize();
        offImage     = createImage(offDimension.width, offDimension.height);
        offGraphics  = offImage.getGraphics();
    }
    
    public boolean generate(Graphics g)
    {
        return false;
    }
    
    public void regenerate()
    {
        if (offGraphics == null)
        {
            return;
        }
        
        if (generate(offGraphics))
        {
            repaint();
        }
    }
    
    public void componentShown(ComponentEvent ev)
    {
    }
    
    public void componentResized(ComponentEvent ev)
    {
        finalize();
        offGraphics = null;
    }
    
    public void componentHidden(ComponentEvent ev)
    {
    }
    
    public void componentMoved(ComponentEvent ev)
    {
    }
    
    public void dispose()
    {
        if (offGraphics != null)
        {
            offGraphics.dispose();
            offImage.flush();
            offDimension = null;
        }
    }
    
    protected final void finalize()
    {
        dispose();
    }
}