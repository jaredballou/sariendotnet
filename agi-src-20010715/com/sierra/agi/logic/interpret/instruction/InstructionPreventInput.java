/*
 * InstructionPreventInput.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Prevent Instruction.
 *
 * <P><CODE><B>prevent.input</B> Instruction 0x77</CODE><BR>
 * Prevents the user from entering anything using the keyboard.
 * </P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionPreventInput extends Instruction
{
    /** 
     * Creates new Prevent Input Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionPreventInput(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
    {
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
        return 1;
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
        return new String[] {"prevent.input"};
    }
//#endif DEBUG
}