/*
 * InstructionDivide.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Divide Instruction.
 *
 * <P><CODE><B>div.n</B> Instruction 0xa7</CODE><BR>
 * The value of variable <CODE>v[p1]</CODE> is divided by <CODE>p2</CODE>,
 * i.e. <CODE>v[p1] /= p2</CODE>.
 * </P>
 *
 * <P><CODE><B>div.v</B> Instruction 0xa8</CODE><BR>
 * The value of variable <CODE>v[p1]</CODE> is divided by <CODE>v[p2]</CODE>,
 * i.e. <CODE>v[p1] /= v[p2]</CODE>.
 * </P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionDivide extends InstructionBi
{
    /** 
     * Creates new Divide Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionDivide(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        short u, v;
        
        if (bytecode == 0xa6)
        {
            v = logicContext.vars[p2];
        }
        else
        {
            v = p2;
        }

        if (v != 0)
        {
            u  = logicContext.vars[p1];
            u /= v;
            
            logicContext.setVar(p1, u);
        }
        else
        {
            logicContext.setVar(p1, (short)0);
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
        
        names[0] = "mul";
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