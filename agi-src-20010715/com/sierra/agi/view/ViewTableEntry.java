/*
 * ViewTableEntry.java
 */

package com.sierra.agi.view;

import com.sierra.agi.logic.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ViewTableEntry extends Object
{
    public final static short MOTION_NORMAL      = (short)0;
    public final static short MOTION_WANDER      = (short)1;
    public final static short MOTION_FOLLOW_EGO  = (short)2;
    public final static short MOTION_MOVE_OBJECT = (short)3;
    
    public final static short CYCLE_NORMAL       = (short)0;
    public final static short CYCLE_NORMAL_LOOP  = (short)1;
    public final static short CYCLE_REVERSE      = (short)2;
    public final static short CYCLE_REVERSE_LOOP = (short)3;

    public final static short FLAG_DRAWN          = (short)0x0001;
    public final static short FLAG_UPDATE         = (short)0x0002;
    public final static short FLAG_CYCLING        = (short)0x0004;
    public final static short FLAG_ANIMATED       = (short)0x0008;
    public final static short FLAG_MOTION         = (short)0x0010;
    
    public final static short FLAG_ON_WATER       = (short)0x0020;
    public final static short FLAG_ON_LAND        = (short)0x0040;

    public final static short FLAG_IGNORE_BLOCKS  = (short)0x0080;
    public final static short FLAG_IGNORE_HORIZON = (short)0x0100;
    public final static short FLAG_IGNORE_OBJECTS = (short)0x0200;

    public final static short FLAG_FIXED_LOOP     = (short)0x0400;
    public final static short FLAG_FIXED_PRIORITY = (short)0x0800;
    
    public final static short FLAG_DONT_UPDATE    = (short)0x1000;
    public final static short FLAG_10             = (short)0x2000;
    
    protected LogicContext logicContext;
    protected ViewTable    viewTable;
    
    protected View         view;
    protected Loop         loop;
    protected Cell         cell;

    protected short entryNumber;
    
    protected short x, x2;
    protected short y, y2;
    protected short width;
    protected short height;
    
    protected short stepTime;
    protected short stepTimeCount;
    protected short stepSize;

    protected short cycle;
    protected short cycleTime;
    protected short cycleTimeCount;
    
    protected short currentView;
    protected short currentLoop;
    protected short currentCell;
    
    protected short numLoops;
    protected short numCells;
    
    protected short direction;
    protected short motion;
    protected short priority;
    protected short flags;
    
    protected short parm1;
    protected short parm2;
    protected short parm3;
    protected short parm4;
    
    /** Creates new View Table Entry */
    public ViewTableEntry(LogicContext logicContext, ViewTable viewTable, short entryNumber)
    {
        this.logicContext = logicContext;
        this.viewTable    = viewTable;
        this.entryNumber  = entryNumber;
    }
    
    public void animate()
    {
	if ((flags & FLAG_ANIMATED) == 0)
        {
            flags     = FLAG_ANIMATED | FLAG_UPDATE | FLAG_CYCLING;
            motion    = MOTION_NORMAL;
            cycle     = CYCLE_NORMAL;
            direction = 0;
	}
    }
    
    public void unanimate()
    {
        flags &= ~(FLAG_ANIMATED | FLAG_DRAWN);
    }
    
    public void setView(short viewNumber)
    {
        view        = logicContext.context.cache.getView(viewNumber);
        currentView = viewNumber;
        
        setLoop(currentLoop >= view.getLoopCount()? 0: currentLoop);
    }
    
    public void setLoop(short loopNumber)
    {
        loop        = view.getLoop(loopNumber);
        currentLoop = loopNumber;
        
        setCell(currentCell >= loop.getCellCount()? 0: currentCell);
    }
    
    public void setCell(short cellNumber)
    {
        cell        = loop.getCell(cellNumber);
        currentCell = cellNumber;
        flags      &= ~FLAG_DONT_UPDATE;
    }
    
    public final short getLastCell()
    {
        return (short)(loop.getCellCount() - 1);
    }
    
    public final short getLastLoop()
    {
        return (short)(view.getLoopCount() - 1);
    }
    
    public final short getCurrentCell()
    {
        return currentCell;
    }
    
    public final short getCurrentLoop()
    {
        return currentLoop;
    }
    
    public final short getCurrentView()
    {
        return currentView;
    }
    
    public void fixLoop()
    {
        flags |= FLAG_FIXED_LOOP;
    }
    
    public void releaseLoop()
    {
        flags &= ~FLAG_FIXED_LOOP;
    }
    
    protected static final short chooseLoopTable1[] = {-1, -1, 0, 0, 0, -1, 1, 1, 1};
    protected static final short chooseLoopTable2[] = {-1,  3, 0, 0, 0,  2, 1, 1, 1};
    
    protected static final short dirTableX[] = {0,  0,  1,  1,  1,  0, -1, -1, -1};
    protected static final short dirTableY[] = {0, -1, -1,  0,  1,  1,  1,  0, -1};
    
    protected final void chooseLoop()
    {
        short c, lc = view.getLoopCount();
        
        if (lc >= 4)
        {
            c = chooseLoopTable2[direction];
        }
        else if (lc > 1)
        {
            c = chooseLoopTable1[direction];
        }
        else
        {
            c = 0;
        }
        
        if (c >= 0)
        {
            setLoop(c);
        }
    }
    
    public void setPriority(short priority)
    {
        flags        |= FLAG_FIXED_PRIORITY;
        this.priority = priority;
    }
    
    public final short getPriority()
    {
        return priority;
    }
    
    public void releasePriority()
    {
        flags &= ~FLAG_FIXED_PRIORITY;
    }
    
    public void setDirection(short direction)
    {
        this.direction = direction;
    }
    
    public final short getDirection()
    {
        return direction;
    }
    
    public void setCycleTime(short cycleTime)
    {
        this.cycleTime      = cycleTime;
        this.cycleTimeCount = cycleTime;
    }
    
    public boolean tick()
    {
        if ((flags & (FLAG_ANIMATED | FLAG_UPDATE | FLAG_DRAWN)) != (FLAG_ANIMATED | FLAG_UPDATE | FLAG_DRAWN))
        {
            return false;
        }

        cycle();
        step();
        return true;
    }
    
    protected void cycle()
    {
        if ((flags & FLAG_FIXED_LOOP) == 0)
        {
            chooseLoop();
        }

        if ((flags & FLAG_CYCLING) == 0)
        {
            return;
        }

        if (cycleTimeCount == 0)
        {
            return;
        }

        cycleTimeCount--;
        
        if (cycleTimeCount == 0)
        {
            update();
            cycleTimeCount = cycleTime;
        }
    }
    
    protected void update()
    {
	short cel, lastCel;

	if ((flags & FLAG_DONT_UPDATE) != 0)
        {
            flags &= ~FLAG_DONT_UPDATE;
            return;
	}

	cel     = currentCell;
        lastCel = (short)(loop.getCellCount() - 1);

	switch (cycle)
        {
	case CYCLE_NORMAL:
            if (++cel > lastCel)
            {
                cel = 0;
            }
            break;
	case CYCLE_NORMAL_LOOP:
            if (cel < lastCel)
            {
                if (++cel != lastCel)
                    break;
                
                logicContext.setFlag(parm1, true);
                
                flags    &= ~FLAG_CYCLING;
                direction = 0;
                cycle     = CYCLE_NORMAL;
            }
            break;
	case CYCLE_REVERSE_LOOP:
            if (cel == 0)
            {
                logicContext.setFlag(parm1, true);
                
                flags    &= ~FLAG_CYCLING;
                direction = 0;
                cycle     = CYCLE_NORMAL;
            }
            else
            {
                cel--;
            }
            break;
	case CYCLE_REVERSE:
            if (cel == 0)
            {
                cel = lastCel;
            }
            else
            {
                cel--;
            }
            break;
	}

	setCell(cel);
    }
    
    protected void step()
    {
	short x, y, x2, y2, dir, step, border;

        if (stepTimeCount != 0)
        {
            if (--stepTimeCount != 0)
                return;
        }

        stepTimeCount = stepTime;

        x = x2 = this.x;
        y = y2 = this.y;

        if ((flags & FLAG_10) == 0)
        {
            dir  = direction;
            step = stepSize;

            x += step * dirTableX[dir];
            y += step * dirTableY[dir];
        }

        border = 0;

        if (x < 0)
        {
            x      = 0;
            border = 4;
        }
        else if (x + width > WIDTH)
        {
            x      = (short)(WIDTH - width);
            border = 2;
        }
        else if (y - height + 1 < 0)
        {
            y      = (short)(height - 1);
            border = 1;
        }
        else if (y > HEIGHT - 1)
        {
            y      = HEIGHT - 1;
            border = 3;
        }
        else if (((flags & FLAG_IGNORE_HORIZON) == 0) && y <= logicContext.horizon)
        {
            y++;
            border = 1;
        }

        this.x = x;
        this.y = y;

        if (viewTable.checkClutter(this) || !checkPriority())
        {
            this.x = x2;
            this.y = y2;
            border = 0;
            
            fixPosition();
        }

        if (border != 0)
        {
            if (entryNumber == 0)
            {
                logicContext.setVar(LogicContext.VAR_EGO_TOUCHING, border);
            }
            else
            {
                logicContext.setVar(LogicContext.VAR_EGO_TOUCHING,    border);
                logicContext.setVar(LogicContext.VAR_BORDER_CODE,     entryNumber); 
                logicContext.setVar(LogicContext.VAR_BORDER_TOUCHING, border); 
            }
            
            if (motion == MOTION_MOVE_OBJECT)
            {
                inDestination();
            }
        }

        flags &= ~FLAG_10;
    }

    public boolean checkPriority()
    {
	int     i,       pri,   p;
        boolean trigger, water, pass;
        byte[]  pdata = null;

	if ((flags & FLAG_FIXED_PRIORITY) == 0)
        {
            /* Priority bands */
            priority = y < 48? (short)4: (short)(y / 12 + 1);
	}

	trigger = false;
	water   = true;
	pass    = true;

        if (priority != (short)0x0f)
        {
            p = x + (y * WIDTH);

            for (i = 0; i < width; i++, p++)
            {
                pri = pdata[p];

                if (pri == 0)
                {
                    /* unconditional black. no go at all! */
                    pass = false;
                    break;
                }

                if (pri == 3)
                {
                    /* water surface */
                    continue;
                }

                water = false;

                if (pri == 1)
                {
                    /* conditional blue */
                    if ((flags & FLAG_IGNORE_BLOCKS) != 0)
                    {
                        continue;
                    }

                    pass = false;
                    break;
                }

                if (pri == 2)
                {
                    /* trigger */
                    trigger = true;
                }
            }

            if (pass)
            {
                if (!water && (flags & FLAG_ON_WATER) != 0)
                {
                    pass = false;
                }

                if (water && (flags & FLAG_ON_LAND) != 0)
                {
                    pass = false;
                }
            }
        }

	if (entryNumber == 0)
        {
            logicContext.setFlag(LogicContext.FLAG_EGO_TOUCHED_ALERT, trigger);
            logicContext.setFlag(LogicContext.FLAG_EGO_WATER,         water);
	}

	return pass;
    }
    
    protected void fixPosition()
    {
	int count, dir, tries;

	/* Test Horizon */
	dir   = 0;
	count = tries = 1;

	while (!checkPosition() || viewTable.checkClutter(this) || !checkPriority())
        {
            switch (dir)
            {
            case 0:			/* West */
                x--;

                if (--count != 0)
                    continue;

                dir = 1;
                break;

            case 1:			/* South */
                y++;

                if (--count != 0)
                    continue;

                dir = 2;
                tries++;
                break;

            case 2:			/* East */
                x++;

                if (--count != 0)
                    continue;

                dir = 3;
                break;

            case 3:			/* North */
                y--;

                if (--count != 0)
                    continue;

                dir = 0;
                tries++;
                break;
            }

            count = tries;
	}
    }
    
    protected void inDestination()
    {
        stepSize = parm3;
	motion   = MOTION_NORMAL;
	logicContext.setFlag(parm4, true);
        
	if (entryNumber == 0)
        {
//            game.player_control = TRUE;
        }
    }

    protected static final int WIDTH  = 160;
    protected static final int HEIGHT = 168;
    
    protected boolean checkPosition()
    {
        return !(   x              <  0      ||
                    x + width      >  WIDTH  ||
		    y + height + 1 <  0      ||
		    y              <  height ||
		    y              >= HEIGHT ||
		    (((flags & FLAG_IGNORE_HORIZON) == 0) && y <= logicContext.horizon)
                );
    }
}