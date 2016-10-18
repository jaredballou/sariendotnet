/*
 * ExpressionIsSet.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Is Set Expression.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExpressionIsSet extends ExpressionUni
{
    /**
     * Creates a new Is Set Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionIsSet(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException
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
        short p = p1;
        
        if (bytecode == 0x08)
        {
            p = logicContext.vars[p];
        }
        
        return logicContext.getFlag(p);
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
        String[] names = new String[2];
        
        names[0] = "isset";
       
        if (bytecode == 0x08)
        {
            names[1] = "vf" + p1;
        }
        else
        {
            names[1] = "f" + p1;
        }
        
        return names;
    }
//#endif DEBUG
}