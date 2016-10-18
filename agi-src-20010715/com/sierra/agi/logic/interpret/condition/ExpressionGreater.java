/*
 * ExpressionGreater.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Greater Expression.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExpressionGreater extends ExpressionBi
{
    /**
     * Creates a new Greater Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionGreater(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException
    {
        super(context, stream, reader, bytecode);
    }

    /**
     * Evaluate Expression.
     *
     * @param logic         Logic used to evaluate the expression.
     * @param logicContext  Logic Context used to evaluate the expression.
     * @return Returns the result of the evaluation.
     */
    public boolean evaluate(Logic logic, LogicContext logicContext)
    {
        short p = p2;
        
        if (bytecode == 0x06)
        {
            p = logicContext.vars[p];
        }
        
        return logicContext.vars[p1] > p;
    }

//#ifdef DEBUG
    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual name of the expression.
     */
    public String[] getNames()
    {
        String[] names = new String[3];
        
        names[0] = "greater";
        names[1] = "v" + p1;

        if (bytecode == 0x06)
        {
            names[2] = "v" + p2;
        }
        else
        {
            names[2] = Integer.toString(p2);
        }
        
        return names;
    }

    /**
     * Returns a String represention of the expression.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a String representation.
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer("v");
        
        buffer.append(p1);
        buffer.append(" > ");
        
        if (bytecode == 0x06)
        {
            buffer.append("v");
        }

        buffer.append(p2);
        return buffer.toString();
    }
//#endif DEBUG
}