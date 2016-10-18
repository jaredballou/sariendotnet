/*
 * Object.java
 */

package com.sierra.agi.object;

import java.io.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InventoryObject extends Object
{
    /** Location */
    public short location;

    /** Original location */
    protected short originalLocation;
    
    /** Offset */
    public int offset;
    
    /** Name */
    public String name;
    
    public static final short EGO_OWNED = 0x00FF;

    public InventoryObject(int offset, short location)
    {
        this.location         = location;
        this.originalLocation = location;
        this.offset           = offset;
    }
    
    public final void reset()
    {
        location = originalLocation;
    }
}