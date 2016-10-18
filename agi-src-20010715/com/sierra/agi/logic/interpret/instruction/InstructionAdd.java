/*
 * InstructionAdd.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Add Instruction.
 *
 * <P><CODE><B>add.n</B> Instruction 0x05</CODE><BR>
 * The value of variable <CODE>v[p1]</CODE> is incremented by <CODE>p2</CODE>,
 * i.e. <CODE>v[p1] += p2</CODE>.
 * </P>
 *
 * <P><CODE><B>add.v</B> Instruction 0x06</CODE><BR>
 * The value of variable <CODE>v[p1]</CODE> is incremented by <CODE>v[p2]</CODE>,
 * i.e. <CODE>v[p1] += v[p2]</CODE>.
 * </P>
 *
 * If the value is greater than <CODE>255</CODE> the result wraps over
 * <CODE>0</CODE> (so <CODE>250 + 10 == 4</CODE>).
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionAdd extends InstructionBi
{
    /** 
     * Creates new Add Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionAdd(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException
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
        short v;
        
        if (bytecode == 0x06)
        {
            v = logicContext.vars[p2];
        }
        else
        {
            v = p2;
        }

        if (v != 0)
        {
            v += logicContext.vars[p1];
            
            if (v > 255)
            {
                v -= 256;
            }

            logicContext.setVar(p1, v);
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
        String[] names = new String[3];
        
        names[0] = "add";
        names[1] = "v" + p1;

        switch (bytecode)
        {
        default:
        case 0x05:
            names[2] = Integer.toString(p2);
            break;
        case 0x06:
            names[2] = "v" + p2;
            break;
        }

        return names;
    }
//#endif DEBUG
}