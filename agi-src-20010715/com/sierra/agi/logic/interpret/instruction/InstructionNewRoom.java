/*
 * InstructionNewRoom.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * New Room Instruction.
 *
 * <P>The <CODE>new.room</CODE> instruction is one of the most powerful
 * commands of the interpreter.</P>
 *
 * <P>It is used to change algorithms of the object behaviour, props, etc.
 * Automatic change of Ego coordinates imitates moving into a room adjacent to
 * the edge of the initial one.</P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionNewRoom extends InstructionUni
{
    /**
     * Creates a new New Room Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionNewRoom(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException
    {
        super(context, stream, reader, bytecode);
    }
    
    /**
     * Execute the Instruction.
     *
     * <P>
     * Do the following:
     * <OL>
     * <LI>Commands stop.update and unanimate are issued to all objects;</LI>
     * <LI>All resources except Logic(0) are discarded;</LI>
     * <LI>Command player.control is issued;</LI>
     * <LI>unblock command is issued;</LI>
     * <LI>set.horizon(36) command is issued;</LI>
     * <LI>v1 is assigned the value of v0; v0 is assigned n (or the value of vn when the command is new.room.v); v4 is assigned 0; v5 is assigned 0; v16 is assigned the ID number of the VIEW resource that was associated with Ego (the player character).</LI>
     * <LI>Logic(i) resource is loaded where i is the value of v0 !</LI>
     * <LI>Set Ego coordinates according to v2:</LI>
     *   <UL>
     *   <LI>if Ego touched the bottom edge, put it on the horizon;</LI>
     *   <LI>if Ego touched the top edge, put it on the bottom edge of the screen;</LI>
     *   <LI>if Ego touched the right edge, put it at the left and vice versa.</LI>
     *   </UL>
     * <LI>v2 is assigned 0 (meaning Ego has not touched any edges).</LI>
     * <LI>f5 is set to 1 (meaning in the first interpreter cycle after the new_room command all initialization parts of all logics loaded and called from the initialization part of the new room's logic will be called. In the subsequent cycle f5 is reset to 0.</LI>
     * <LI>Clear keyboard input buffer and return to the main AGI loop.</LI>
     * </OL>
     * </P>
     *
     * @param logic         Logic used to execute the instruction.
     * @param logicContext  Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) throws Exception
    {
        short nr = p1;
        
        if (bytecode == 0x13)
        {
            nr = logicContext.vars[nr];
        }

        /* 1 */
        /* 2 */
        /* 3 */
        /* 4 */
        /* 5 */
        
        /* 6 */
        logicContext.setVar(LogicContext.VAR_PREVIOUS_ROOM, logicContext.vars[LogicContext.VAR_CURRENT_ROOM]);
        logicContext.setVar(LogicContext.VAR_CURRENT_ROOM,  nr);
        
        /* 7 */
        logicContext.context.cache.loadLogic(nr);
        
        /* 8 */
        
        /* 9 */
        logicContext.setVar(LogicContext.VAR_EGO_TOUCHING, (short)0);
        
        /* 10 */
        logicContext.setFlag(LogicContext.FLAG_NEW_ROOM_EXEC, true);
        
        /* 11 */
        logicContext.addPendingJob(new FlagUpdaterPlacer(logicContext));
        throw new LogicExitAll();
    }

    /**
     * After the <CODE>LogicExitAll</CODE> exception is thrown, this Inner class
     * will be executed. It will only place another inner class in pending job
     * queue.
     */
    public class FlagUpdaterPlacer extends Object implements Runnable
    {
        protected LogicContext logicContext;
        
        public FlagUpdaterPlacer(LogicContext logicContext)
        {
            this.logicContext = logicContext;
        }
        
        public void run()
        {
            logicContext.addPendingJob(new FlagUpdater(logicContext));
        }
    }
    
    /**
     * Will reset the <CODE>VAR_NEW_ROOM</CODE> to <CODE>false</CODE>. Needed
     * to ensure proper execution.
     */
    public class FlagUpdater extends Object implements Runnable
    {
        protected LogicContext logicContext;
        
        public FlagUpdater(LogicContext logicContext)
        {
            this.logicContext = logicContext;
        }
        
        public void run()
        {
            logicContext.setFlag(LogicContext.FLAG_NEW_ROOM_EXEC, false);
        }
    }
    
//#ifdef DEBUG
    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames()
    {
        String[] names = new String[2];
        
        names[0] = "new.room";
        
        switch (bytecode)
        {
        default:
        case 0x12:
            names[1] = Integer.toString(p1);
            break;
        case 0x13:
            names[1] = "v" + p1;
            break;
        }
        
        return names;
    }
//#endif DEBUG
}