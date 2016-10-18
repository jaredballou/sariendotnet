/*
 * Loop.java
 */

package com.sierra.agi.view;

import com.sierra.agi.Context;
import com.sierra.agi.misc.ByteCaster;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class Loop extends Object
{
    /** Cells */
    protected Cell cells[] = null;
    
    /** Creates new Loop */
    public Loop(Context context, byte b[], int start, int loopNumber)
    {
        short cellCount;
        int   i, j;
        
        cellCount = ByteCaster.lohiUnsignedByte(b, start);
        cells     = new Cell[cellCount];
        
        j = start + 1;
        for (i = 0; i < cellCount; i++)
        {
            cells[i] = new Cell(context, b, start + ByteCaster.lohiUnsignedShort(b, j), loopNumber);
            j += 2;
        }
    }
    
    public Cell getCell(int cellNumber)
    {
        return cells[cellNumber];
    }

    public byte getCellCount()
    {
        return (byte)cells.length;
    }
}