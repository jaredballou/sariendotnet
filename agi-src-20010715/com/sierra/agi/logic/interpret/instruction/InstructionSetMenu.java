/*
 * InstructionSetMenu.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;
import java.util.Vector;

/**
 * Set Menu Instruction.
 *
 * <P><CODE><B>set.menu</B> Instruction 0x9c</CODE><BR>
 * <CODE>m[p1]</CODE> is used as the header of the menu elements which follow.
 * </P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionSetMenu extends InstructionUni
{
    /** 
     * Creates new Set Menu Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionSetMenu(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        Vector v = new Vector(6, 2);
        
        v.add(logic.getString(p1));
        logicContext.menus.add(v);
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
        return new String[] {"set.menu", "m" + p1};
    }
//#endif DEBUG
}