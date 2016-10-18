/*
 * PointStack.java
 */

package com.sierra.agi.misc;

import java.awt.Point;
import java.util.EmptyStackException;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class PointStack extends Object
{
    protected int increment;

    protected int elementCount;
    
    protected short[] x;
    
    protected short[] y;
    
    /** Creates new Point Stack */
    public PointStack()
    {
        increment = 15;
    }

    public PointStack(int initialSize, int increment)
    {
        this.increment = increment;
        ensureCapacity(initialSize);
    }
    
    public void pop(Point pt)
    {
        if (elementCount == 0)
        {
            throw new EmptyStackException();
        }
        
        elementCount--;
        pt.x = x[elementCount];
        pt.y = y[elementCount];
    }
    
    public void push(int x, int y)
    {
        ensureCapacity(elementCount + 1);
        
        this.x[elementCount] = (short)x;
        this.y[elementCount] = (short)y;
        elementCount++;
    }
    
    public void ensureCapacity(int minCapacity)
    {
        if (x == null)
        {
            x = new short[minCapacity + increment];
            y = new short[minCapacity + increment];
        }
        else if (x.length < minCapacity)
        {
            short[] nx = new short[minCapacity + increment];
            short[] ny = new short[minCapacity + increment];
            int     i, l = elementCount;

            for (i = 0; i < l; i++)
            {
                nx[i] = x[i];
            }

            for (i = 0; i < l; i++)
            {
                ny[i] = y[i];
            }
            
            x = nx;
            y = ny;
        }
    }
}