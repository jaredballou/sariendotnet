/*
 * InstructionSetPriority.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Set Priority Instruction.
 *
 * <P><CODE><B>set.priority.n</B> Instruction 0x36</CODE><BR>
 * Set priority of the view of the object <CODE>p1</CODE> to <CODE>p2</CODE>.</P>
 *
 * <P><CODE><B>set.priority.v</B> Instruction 0x37</CODE><BR>
 * Set priority of the view of the object <CODE>p1</CODE> to <CODE>v[p2]</CODE>.</P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionSetPriority extends InstructionBi
{
    /** 
     * Creates new Set Priority Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionSetPriority(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        
        if (bytecode == 0x36)
        {
            p = logicContext.vars[p];
        }
        
        logicContext.viewTable.getViewTableEntry(p1).setPriority(p);
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
        
        names[0] = "set.priority";
        names[1] = "o" + p1;
        
        switch (bytecode)
        {
        case 0x36:
            names[2] = Integer.toString(p2);
            break;
        case 0x37:
            names[2] = "v" + p2;
            break;
        }
        
        return names;
    }
//#endif DEBUG
}