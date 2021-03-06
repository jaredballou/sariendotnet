/*
 * InstructionCall.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.res.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Call Logic Instruction.
 *
 * <P><CODE><B>call.n</B> Instruction 0x16</CODE><BR>
 * Logic resource number p1 is executed as a subroutine.</P>
 *
 * <P><CODE><B>call.v</B> Instruction 0x17</CODE><BR>
 * Logic resource number v[p1] is executed as a subroutine.</P>
 *
 * <P>If the logic with the given ID is not loaded in memory, it is temporarily
 * loaded and discarded after returning from the call (this takes extra time).
 * call does not change any variables or flags.</P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionCall extends InstructionUni
{
    /** 
     * Creates new Call Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionCall(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException
    {
        super(context, stream, reader, bytecode);
    }
    
    /**
     * Execute the Instruction.
     *
     * @param logic         Logic used to execute the instruction.
     * @param logicContext  Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) throws LogicException
    {
        Logic logicToCall;
        short p = p1;
        
        if (bytecode == 0x17)
        {
            p = logicContext.vars[p];
        }
        
        logicToCall = logicContext.context.cache.getLogic(p);
        logicToCall.execute(logicContext);
        return 2;
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
        
        names[0] = "call";
        
        switch (bytecode)
        {
        default:
        case 0x16:
            names[1] = Integer.toString(p1);
            break;
        case 0x17:
            names[1] = "v" + p1;
            break;
        }
        
        return names;
    }
//#endif DEBUG
}