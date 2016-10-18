/*
 * Screen.java
 */

package com.sierra.agi.awt;

import java.awt.*;
import java.awt.event.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class MainScreen extends BufferedCanvas
{
    protected Rectangle output = new Rectangle(0, 0, 320, 200);
    protected byte      zoom   = (byte)1;
    protected Image     invisible;
    protected Image     onScreen;
    
    /** Creates new Screen */
    public MainScreen()
    {
        Image invisible;

        invisible = getToolkit().createImage(getClass().getResource("invisible.gif"));
        onScreen  = null;

        setBackground(Color.black);
        setCursor(getToolkit().createCustomCursor(invisible, new Point(8,8), "Invisible"));
    }
    
    public Dimension getPreferredSize()
    {
        return new Dimension(640, 400);
    }
    
    public Dimension getMinimumSize()
    {
        return new Dimension(320, 200);
    }
    
    public boolean generate(Graphics g)
    {
        Dimension d = getSize();
        
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        
        /*g.drawImage(
            onScreen,
            output.x,     output.y,
            output.width, output.height,
            Color.black,
            this);*/
        
        return true;
    }
    
    public void componentResized(ComponentEvent ev)
    {
        super.componentResized(ev);
        
        Dimension d = getSize();
        
        if (d.width >= 640)
        {
            output.width  = 640;
            output.height = 400;
            zoom          = (byte)2;
        }
        else
        {
            output.width  = 320;
            output.height = 200;
            zoom          = (byte)1;
        }
        
        output.x = (d.width  - output.width)  / 2;
        output.y = (d.height - output.height) / 2;
    }
}