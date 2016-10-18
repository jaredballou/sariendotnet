/*
 * ExpressionObjInBox.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Object In Box Expression.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExpressionObjInBox extends ExpressionPent
{
    /**
     * Creates a new Object In Box Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionObjInBox(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        return false;
    }

//#ifdef DEBUG
    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Always return <CODE>null</CODE> in this implentation.
     */
    public String[] getNames()
    {
        return new String[] {"obj.in.box", Integer.toString(p1), Integer.toString(p2), Integer.toString(p3), Integer.toString(p4), Integer.toString(p5)};
    }
//#endif DEBUG
}