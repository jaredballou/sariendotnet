/*
 * InstructionPut.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Put Instruction.
 *
 * <P><CODE><B>put.n</B> Instruction 0x5F</CODE><BR>
 * Stores the value <CODE>p2</CODE> in the room field of the object
 * <CODE>i[p1]</CODE>.
 * </P>
 *
 * <P><CODE><B>put.v</B> Instruction 0x60</CODE><BR>
 * Stores the value <CODE>v[p2]</CODE> in the room field of the object
 * <CODE>i[p1]</CODE>.
 * </P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionPut extends InstructionBi
{
    /** 
     * Creates new Put Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionPut(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        short p = p2;
        
        if (bytecode == 0x60)
        {
            p = logicContext.vars[p];
        }

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
        String[] names = new String[1];
        
        names[0] = "put";
        names[1] = "i" + p1;
        
        switch (bytecode)
        {
        case 0x5F:
            names[1] = Integer.toString(p2);
            break;
        case 0x60:
            names[1] = "v" + p2;
            break;
        }
        
        return names;
    }
//#endif DEBUG
}