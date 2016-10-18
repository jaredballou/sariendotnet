/*
 * ViewTable.java
 */

package com.sierra.agi.view;

import com.sierra.agi.logic.*;

/**
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ViewTable extends Object
{
    /** Maximum number of view table entries */
    public static final short MAX_VIEW_TABLE = 64;
    
    /** View Table */
    public ViewTableEntry[] viewTable = new ViewTableEntry[MAX_VIEW_TABLE];
    
    /** Logic Context */
    public LogicContext logicContext;

    /** Creates new View Table */
    public ViewTable(LogicContext logicContext)
    {
        short i;
        
        this.logicContext = logicContext;
        
        for (i = 0; i < MAX_VIEW_TABLE; i++)
        {
            viewTable[i] = new ViewTableEntry(logicContext, this, i);
        }
    }

    public boolean checkClutter(ViewTableEntry v)
    {
        int            i;
	ViewTableEntry u;

	if ((v.flags & ViewTableEntry.FLAG_IGNORE_OBJECTS) != 0)
        {
            return false;
        }

        for (i = 0; i < MAX_VIEW_TABLE; i++)
        {
            u = viewTable[i];
            
            if (u == v)
            {
                continue;
            }

            if ((u.flags & (ViewTableEntry.FLAG_ANIMATED | ViewTableEntry.FLAG_DRAWN)) != (ViewTableEntry.FLAG_ANIMATED | ViewTableEntry.FLAG_DRAWN))
            {
                continue;
            }

            if ((u.flags & ViewTableEntry.FLAG_IGNORE_OBJECTS) != 0)
            {
                continue;
            }

            if (v.x + v.width < u.x)
            {
                continue;
            }

            if (v.x > u.x + u.width)
            {
                continue;
            }

            if (v.y == u.y)
            {
                return true;
            }

            if (v.y > u.y)
            {
                if (v.y2 < u.y2)
                {
                    return true;
                }
            }

            if (v.y >= u.y)
            {
                continue;
            }

            if (v.y <= u.y)
            {
                continue;
            }

            return true;
	}
	
	return true;
    }
    
    public boolean tick()
    {
        int     i;
        boolean b = false;
        
        logicContext.setVar(LogicContext.VAR_BORDER_CODE,     (short)0);
        logicContext.setVar(LogicContext.VAR_BORDER_TOUCHING, (short)0);
        logicContext.setVar(LogicContext.VAR_EGO_TOUCHING,    (short)0);
        
        for (i = 0; i < MAX_VIEW_TABLE; i++)
        {
            b |= viewTable[i].tick();
        }
        
        return b;
    }
    
    public void unanimateAll()
    {
        int i;
        
        for (i = 0; i < MAX_VIEW_TABLE; i++)
        {
            viewTable[i].unanimate();
        }
    }
    
    public ViewTableEntry getViewTableEntry(int n)
    {
        return viewTable[n];
    }
}