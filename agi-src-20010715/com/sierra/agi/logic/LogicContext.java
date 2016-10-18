/*
 * LogicContext.java
 */

package com.sierra.agi.logic;

import com.sierra.agi.*;
import com.sierra.agi.debug.*;
import com.sierra.agi.object.InventoryObjects;
import com.sierra.agi.sound.Sound;
import com.sierra.agi.view.ViewTable;
import java.io.*;
import java.util.*;

/**
 * Logic Context that Logic Instruction are run with. Contains all variables
 * flags, and needed information in order to make AGI Instruction runnable.
 *
 * <P><B>Variables</B><BR>
 * On interpreter startup all variables are set to 0.</P>
 *
 * <TABLE>
 * <TR><TD VALIGN=TOP>0</TD><TD>Current room number (parameter new_room cmd), initially 0.</TD></TR>
 * <TR><TD VALIGN=TOP>1</TD><TD>Previous room number.</TD></TR>
 * <TR><TD VALIGN=TOP>2</TD><TD>Code of the border touched by Ego:<BR>
 * 0 - Touched nothing;<BR>
 * 1 - Top edge of the screen or the horizon;<BR>
 * 2 - Right edge of the screen;<BR>
 * 3 - Bottom edge of the screen;<BR>
 * 4 - Left edge of the screen.</TD></TR>
 * <TR><TD VALIGN=TOP>3</TD><TD>Current score.</TD></TR>
 * <TR><TD VALIGN=TOP>4</TD><TD>Number of object, other than Ego, that touched the border.</TD></TR>
 * <TR><TD VALIGN=TOP>5</TD><TD>The code of border touched by the object in Var (4).</TD></TR>
 * <TR><TD VALIGN=TOP>6</TD><TD>Direction of Ego's motion.<PRE>
 *                      1
 *                8     |     2
 *                  \   |   /
 *                    \ | /
 *              7 ------------- 3      0 - the object
 *                    / | \                is motionless
 *                  /   |   \
 *                6     |     4
 *                      5
 * </PRE></TD></TR>
 * <TR><TD VALIGN=TOP>7</TD><TD>Maximum score.</TD></TR>
 * <TR><TD VALIGN=TOP>8</TD><TD>Number of free 256-byte pages of the interpreter's memory.</TD></TR>
 * <TR><TD VALIGN=TOP>9</TD><TD>If == 0, it is the number of the word in the user message that was not found in the dictionary. (I would assume they mean "if != 0", but that's what they say. --VB)</TD></TR>
 * <TR><TD VALIGN=TOP>10</TD><TD>Time delay between interpreter cycles in 1/20 second intervals.</TD></TR>
 * <TR><TD VALIGN=TOP>11</TD><TD>Seconds (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>12</TD><TD>Minutes (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>13</TD><TD>Hours (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>14</TD><TD>Days (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>15</TD><TD>Joystick sensitivity (if Flag (8) = 1).</TD></TR> 
 * <TR><TD VALIGN=TOP>16</TD><TD>ID number of the view-resource associated with Ego.</TD></TR>
 * <TR><TD VALIGN=TOP>17</TD><TD>Interpreter error code (if == 0) (Again I would expect this to say ``if != 0''. --VB)</TD></TR>
 * <TR><TD VALIGN=TOP>18</TD><TD>Additional information that goes with the error code.</TD></TR>
 * <TR><TD VALIGN=TOP>19</TD><TD>Key pressed on the keyboard.</TD></TR>
 * <TR><TD VALIGN=TOP>20</TD><TD>Computer type. For IBM-PC it is always 0.</TD></TR>
 * <TR><TD VALIGN=TOP>21</TD><TD>If Flag (15) == 0 (command reset 15 was issued) and Var (21) is not equal to 0, the window is automatically closed after 1/2 * Var (21) seconds.</TD></TR>
 * <TR><TD VALIGN=TOP>22</TD><TD>Sound generator type:<BR>
 *         1 - PC<BR>
 *         3 - Tandy</TD></TR>
 * <TR><TD VALIGN=TOP>23</TD><TD>0:F - sound volume (for Tandy).<BR>
 * <TR><TD VALIGN=TOP>24</TD><TD>This variable stores the maximum number that can be entered in the input line. By default, this variable is set to 41 (29h). (information by Dark Minister) 
 * <TR><TD VALIGN=TOP>25</TD><TD>ID number of the item selected using status command or 0xFF if ESC was pressed. 
 * <TR><TD VALIGN=TOP>26</TD><TD>monitor type<BR>
 *         0 - CGA<BR>
 *         2 - Hercules<BR>
 *         3 - EGA</TD></TR>
 * </TABLE>
 *
 * <P><B>Flags</B><BR>
 * On interpreter startup all flags are set to 0.</P>
 *
 * <TABLE>
 * <TR><TD VALIGN=TOP>0</TD><TD>Ego base line is completely on pixels with priority = 3 (water surface).</TD></TR>
 * <TR><TD VALIGN=TOP>1</TD><TD>Ego is invisible of the screen (completely obscured by another object).</TD></TR>
 * <TR><TD VALIGN=TOP>2</TD><TD>the player has issued a command line.</TD></TR>
 * <TR><TD VALIGN=TOP>3</TD><TD>Ego base line has touched a pixel with priority 2 (signal).</TD></TR>
 * <TR><TD VALIGN=TOP>4</TD><TD><CODE>said</CODE> command has accepted the user input.</TD></TR>
 * <TR><TD VALIGN=TOP>5</TD><TD>The new room is executed for the first time.</TD></TR>
 * <TR><TD VALIGN=TOP>6</TD><TD><CODE>restart.game</CODE> command has been executed.</TD></TR>
 * <TR><TD VALIGN=TOP>7</TD><TD>if this flag is 1, writing to the script buffer is blocked.</TD></TR>
 * <TR><TD VALIGN=TOP>8</TD><TD>if 1, Var(15) determines the joystick sensitivity.</TD></TR>
 * <TR><TD VALIGN=TOP>9</TD><TD>sound on/off.</TD></TR>
 * <TR><TD VALIGN=TOP>10</TD><TD>1 turns on the built-in debugger.</TD></TR>
 * <TR><TD VALIGN=TOP>11</TD><TD>Logic 0 is executed for the first time.</TD></TR>
 * <TR><TD VALIGN=TOP>12</TD><TD><CODE>restore.game</CODE> command has been executed.</TD></TR>
 * <TR><TD VALIGN=TOP>13</TD><TD>1 allows the <CODE>status</CODE> command to select items.</TD></TR>
 * <TR><TD VALIGN=TOP>14</TD><TD>1 allows the menu to work.</TD></TR>
 * <TR><TD VALIGN=TOP>15</TD><TD>Determines the output mode of <CODE>print</CODE> and <CODE>print.at</CODE> commands:<BR>
 *    1 - message window is left on the screen<BR>
 *    0 - message window is closed when ENTER or ESC key are pressed. If Var(21) is not 0, the window is closed automatically after 1/2 * Var(21) seconds</TD></TR>
 * </TABLE>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class LogicContext extends Object implements Dumpable
{
    /** Maximum number of flags */
    public static final short MAX_FLAGS = 256;
    
    /** Maximum number of variables */
    public static final short MAX_VARS  = 256;
    
    /** Flags */
    protected byte[] flags = new byte[MAX_FLAGS / 8];
    
    /** Variables */
    public short[] vars = new short[MAX_VARS];
    
    /** String Variables */
    public String[] strings;
    
    /** Objects */
    public InventoryObjects objects;
    
    /** Context */
    public Context context;
    
    /** Currently Playing Sound */
    public Sound currentSound;
    
    /** View Table */
    public ViewTable viewTable = new ViewTable(this);
    
    /** Menu Entries */
    public Vector menus = new Vector(6, 2);
    
    /** Horizon */
    public short horizon = DEFAULT_HORIZON;

    /** Jobs */
    protected Vector jobs  = new Vector(5, 5);
    protected Vector ojobs = new Vector(5, 5);
    
    public static final short DEFAULT_HORIZON = (short)36;
    
    public static final short FLAG_EGO_WATER            = (short)0;
    public static final short FLAG_EGO_INVISIBLE        = (short)1;
    public static final short FLAG_ENTERED_COMMAND      = (short)2;
    public static final short FLAG_EGO_TOUCHED_ALERT    = (short)3;
    public static final short FLAG_SAID_ACCEPTED_INPUT  = (short)4;
    public static final short FLAG_NEW_ROOM_EXEC        = (short)5;
    public static final short FLAG_RESTART_GAME         = (short)6;
    public static final short FLAG_SCRIPT_BLOCKED       = (short)7;
    public static final short FLAG_JOYSTICK_SENSITIVITY = (short)8;
    public static final short FLAG_SOUND_ON             = (short)9;
    public static final short FLAG_DEBUGGER_ON          = (short)10;
    public static final short FLAG_LOGIC_ZERO_FIRSTTIME = (short)11;
    public static final short FLAG_RESTORE_JUST_RAN     = (short)12;
    public static final short FLAG_STATUS_SELECTS_ITEMS = (short)13;
    public static final short FLAG_MENUS_WORK           = (short)14;
    public static final short FLAG_OUTPUT_MODE          = (short)15;

    public static final short VAR_CURRENT_ROOM          = (short)0;
    public static final short VAR_PREVIOUS_ROOM         = (short)1;
    public static final short VAR_EGO_TOUCHING          = (short)2;
    public static final short VAR_SCORE                 = (short)3;
    public static final short VAR_BORDER_CODE           = (short)4;
    public static final short VAR_BORDER_TOUCHING       = (short)5;
    public static final short VAR_EGO_DIRECTION         = (short)6;
    public static final short VAR_MAX_SCORE             = (short)7;
    public static final short VAR_FREE_PAGES            = (short)8;
    public static final short VAR_WORD_NOT_FOUND        = (short)9;
    public static final short VAR_TIME_DELAY            = (short)10;
    public static final short VAR_SECONDS               = (short)11;
    public static final short VAR_MINUTES               = (short)12;
    public static final short VAR_HOURS                 = (short)13;
    public static final short VAR_DAYS                  = (short)14;
    public static final short VAR_JOYSTICK_SENSITIVITY  = (short)15;
    public static final short VAR_EGO_VIEW_RESOURCE     = (short)16;
    public static final short VAR_AGI_ERR_CODE          = (short)17;
    public static final short VAR_AGI_ERR_CODE_INFO     = (short)18;
    public static final short VAR_KEY                   = (short)19;
    public static final short VAR_COMPUTER              = (short)20;
    public static final short VAR_WINDOW_RESET          = (short)21;
    public static final short VAR_SOUND_GENERATOR       = (short)22;
    public static final short VAR_VOLUME                = (short)23;
    public static final short VAR_MAX_INPUT_CHARS       = (short)24;
    public static final short VAR_SEL_ITEM              = (short)25;
    public static final short VAR_MONITOR               = (short)26;
    
    /** Creates new Logic Context */
    public LogicContext(Context context) throws Exception
    {
        this.context = context;
        this.objects = context.cache.getObjects();
        
        reset();
    }

    public void reset()
    {
        int i;
        
        for (i = 0; i < MAX_FLAGS / 8; i++)
        {
            flags[i] = 0;
        }
        
        for (i = 0; i < MAX_FLAGS; i++)
        {
            vars[i] = 0;
        }

        strings = new String[24];
        
        vars[VAR_FREE_PAGES]      = (byte)255; // 255 Pages of 256 bytes are free.
        vars[VAR_SOUND_GENERATOR] = (byte)3;   // Tandy Compatible Sound Generator. (because we support 4 channel sound output)
        vars[VAR_MONITOR]         = (byte)3;   // EGA Compatible Graphic Generator.
        
        setFlag(FLAG_LOGIC_ZERO_FIRSTTIME, true);
        setFlag(FLAG_SOUND_ON,             true);
        
        horizon = DEFAULT_HORIZON;
    }
    
    public void setVar(short var, short value)
    {
        vars[var] = value;
    }
    
    public boolean getFlag(short flag)
    {
        int d, r, m;
        
        d = flag / 8;
        r = flag % 8;
        m = 1 << r;
        
        return ((flags[d] & m) == m)? true: false;
    }
    
    public void setFlag(short flag, boolean value)
    {
        int d, r, m;
        
        d = flag / 8;
        r = flag % 8;
        m = 1 << r;
        
        if (value)
        {
            flags[d] |= m;
        }
        else
        {
            flags[d] &= ~m;
        }
    }
    
    public void toggleFlag(short flag)
    {
        int d, r, m;
        
        d = flag / 8;
        r = flag % 8;
        m = 1 << r;
        
        if ((flags[d] & m) == m)
        {
            flags[d] &= ~m;
        }
        else
        {
            flags[d] |= m;
        }
    }
    
    public void dump(PrintWriter writer)
    {
//#ifdef DUMP
        int i;
        
        writer.println();
        writer.println("** Logic Context **");
        writer.println();
        
        writer.println("Variables: ");
        for (i = 0; i < MAX_VARS; i++)
        {
            writer.print(i);
            writer.print(": ");
            writer.println(vars[i] & 0xFF);
        }

        writer.println();
        writer.println("Flags: ");
        for (i = 0; i < MAX_FLAGS; i++)
        {
            writer.print(i);
            writer.print(": ");
            writer.println(getFlag((short)i));
        }
//#endif DUMP
    }
    
    public void addPendingJob(Runnable job)
    {
        jobs.add(job);
    }
    
    public void runPendingJobs()
    {
        Vector      v;
        Enumeration enum;
        Runnable    job;
        
        if (jobs.size() == 0)
        {
            return;
        }
        
        enum = jobs.elements();
        
        /* Switch */
        v     = jobs;
        jobs  = ojobs;
        ojobs = v;
        
        while (enum.hasMoreElements())
        {
            job = (Runnable)enum.nextElement();
            job.run();
        }
        
        ojobs.clear();
    }
}