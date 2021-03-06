/*
 * InstructionStopMotion.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Stop Motion Instruction.
 *
 * <P><CODE><B>stop.motion</B> Instruction 0x4D</CODE><BR>
 * Motion of object <CODE>o[p1]</CODE> is stopped. If <CODE>p1 == 0</CODE>,
 * <CODE>program.control</CODE> is automatically executed.
 * </P>
 *
 * @see     com.sierra.agi.logic.interpret.instruction.InstructionProgramControl
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionStopMotion extends InstructionUni
{
    /** 
     * Creates new Stop Motion Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionStopMotion(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        return new String[] {"stop.motion", "o" + p1};
    }
//#endif DEBUG
}