/*
 * InstructionPent.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public abstract class InstructionPent extends Instruction
{
    /** Bytecode */
    protected short bytecode;

    /** Parameter #1 */
    protected short p1;
    
    /** Parameter #2 */
    protected short p2;
    
    /** Parameter #3 */
    protected short p3;
    
    /** Parameter #4 */
    protected short p4;
    
    /** Parameter #5 */
    protected short p5;
    
    /** Creates new Instruction */
    protected InstructionPent(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
    {
        super(context, stream, reader, bytecode);
        this.bytecode = bytecode;
        this.p1       = (short)stream.read();
        this.p2       = (short)stream.read();
        this.p3       = (short)stream.read();
        this.p4       = (short)stream.read();
        this.p5       = (short)stream.read();
    }
    
    /** Determine Instruction Size */
    public int getSize()
    {
        return 6;
    }
}