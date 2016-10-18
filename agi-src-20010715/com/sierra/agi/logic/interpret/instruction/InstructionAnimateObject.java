/*
 * InstructionAnimateObject.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;
import com.sierra.agi.view.*;

/**
 * Animate Object instruction.
 *
 * <P>Object number n is included in the list of object controlled by the
 * interpreter. <I>Objects not included in that list are considered inexistent!</I></P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionAnimateObject extends InstructionUni
{
    /** 
     * Creates new Animate Object Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when stream.read() fails.
     */
    public InstructionAnimateObject(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        logicContext.viewTable.getViewTableEntry(p1).animate();
        return 2;
    }

//#ifdef DEBUG
    /**
     * Retreive the textual name of the instruction.
     *
     * @return Returns the textual name of the instruction.
     */
    public String[] getNames()
    {
        return new String[] {"animate.obj", "o" + p1};
    }
//#endif DEBUG
}