/*
 * BottomButtonBarLayout.java
 */

package com.sierra.agi.awt;

import java.awt.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class BottomButtonBarLayout extends Object implements LayoutManager
{
    /** Distance between Components */
    public int gap = 5;
    
    /** Extra Height for Components */
    public int extraHeight = 5;
    
    /** Extra Width for Components */
    public int extraWidth = 5;
    
    /** Component Count */
    protected int compCount = 0;
    
    /** Buttons Size */
    protected Dimension buttonSize = null;
    
    /** Creates new BottomButtonBarLayout */
    public BottomButtonBarLayout()
    {
    }

    protected void getSizes(Container parent)
    {
        int       c = parent.getComponentCount();
        Component comp;
        Dimension d;
        int       i;
     
        buttonSize = new Dimension();
        
        for (i = 0; i < c; i++)
        {
            comp = parent.getComponent(i);
            d    = comp.getPreferredSize();
            
            if (buttonSize.width < d.width)
                buttonSize.width = d.width;
            
            if (buttonSize.height < d.height)
                buttonSize.height = d.height;
        }
        
        buttonSize.height += extraHeight;
        buttonSize.width  += extraWidth;
        compCount = c;
    }
    
    public void addLayoutComponent(String str, Component component)
    {
    }
    
    public void layoutContainer(Container parent)
    {
        Insets insets = parent.getInsets();
        int maxWidth  = parent.getSize().width  - (insets.left + insets.right);
        int x, y;

        Component comp;
        
        int c = parent.getComponentCount();
        int i;
       
        getSizes(parent);
        
        x = maxWidth;
        y = insets.top + gap;
        
        for (i = 0; i < c; i++)
        {
            x -= (buttonSize.width + gap);
            
            comp = parent.getComponent(i);
            comp.setBounds(x, y, buttonSize.width, buttonSize.height);
        }
    }
    
    public Dimension minimumLayoutSize(Container parent)
    {
        Dimension d      = new Dimension();
        Insets    insets = parent.getInsets();
        
        if (buttonSize == null)
        {
            getSizes(parent);
        }
        
        d.height  = buttonSize.height;
        d.height += insets.top + insets.bottom + (gap * 2);
        d.width   = (buttonSize.width + gap) * compCount;
        d.width  += insets.left + insets.right + gap;
        return d;
    }
    
    public Dimension preferredLayoutSize(Container container)
    {
        return minimumLayoutSize(container);
    }
    
    public void removeLayoutComponent(Component component)
    {
    }    
}