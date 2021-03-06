/*
 * InstructionGoto.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import com.sierra.agi.misc.*;
import java.io.*;

/**
 * Goto Instruction.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionGoto extends InstructionMoving
{
    /** Goto Position */
    protected short gotoPosition;
    
    /**
     * Creates a new Goto Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionGoto(Context context,InputStream stream,LogicReader reader, short bytecode) throws IOException
    {
        ByteCasterStream bstream = new ByteCasterStream(stream);

        gotoPosition = (short)(bstream.lohiReadUnsignedShort());
    }

    /**
     * Execute the Instruction.
     *
     * @param logic         Logic used to execute the instruction.
     * @param logicContext  Logic Context used to execute the instruction.
     * @return Returns the address referenced by this instruction.
     */
    public int execute(Logic logic, LogicContext logicContext)
    {
        return gotoPosition + 3;
    }
    
    /**
     * Determine Instruction Size. In this class, it always return 3. (It is the
     * size of a goto instruction.)
     *
     * @return Returns the instruction size.
     */
    public int getSize()
    {
        return 3;
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
        
        names[0] = "goto";
        names[1] = Integer.toString(gotoPosition);
        
        return names;
    }
//#endif DEBUG
    
    /**
     * Retreive the Address which is referenced by this instruction.
     *
     * @return Returns the Address referenced by this instruction.
     */
    public int getAddress()
    {
        return gotoPosition;
    }
}