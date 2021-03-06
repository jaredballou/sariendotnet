/*
 * InstructionToggle.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Toggle Flag Instruction.
 *
 * <P><CODE><B>toggle.n</B> Instruction 0x10</CODE><BR>
 * <CODE>f[p1]</CODE> toggle its value.</P>
 *
 * <P><CODE><B>toggle.v</B> Instruction 0x11</CODE><BR>
 * <CODE>f[v[p1]]</CODE> toggle its value.</P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionToggle extends InstructionUni
{
    /** 
     * Creates new Toggle Flag Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionToggle(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
    public int execute(Logic logic, LogicContext logicContext)
    {
        short p = p1;
        
        if (bytecode >= 0x11)
        {
            p = logicContext.vars[p];
        }
        
        logicContext.toggleFlag(p);
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
        
        switch (bytecode)
        {
        default:
        case 0x0E:
            names[0] = "toggle";
            names[1] = "f" + p1;
            break;
        case 0x11:
            names[0] = "toggle";
            names[1] = "vf" + p1;
            break;
        }
        
        return names;
    }
//#endif DEBUG
}