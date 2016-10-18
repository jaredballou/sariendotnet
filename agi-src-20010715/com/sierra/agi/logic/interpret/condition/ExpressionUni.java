/*
 * InstructionUni.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Base Class for all Logic's Instructions that has 1 parameter.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public abstract class ExpressionUni extends Expression
{
    /** Bytecode */
    protected short bytecode;
    
    /** Parameter #1 */
    protected short p1;
    
    /**
     * Creates a new Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    protected ExpressionUni(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException
    {
        this.bytecode = bytecode;
        this.p1       = (short)stream.read();
    }
    
    /**
     * Determine Expression Size. In this class, it always return 2. (It is the
     * size of a expression that has 1 parameter.)
     *
     * @return Returns the instruction size.
     */
    public int getSize()
    {
        return 2;
    }
}