/*
 * ExpressionObjInRoom.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * Object In Room Expression.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExpressionObjInRoom extends ExpressionBi
{
    /**
     * Creates a new Object In Room Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionObjInRoom(Context context,InputStream stream,LogicReader reader,short bytecode) throws IOException
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
        return (logicContext.objects.getObject(p1).location == logicContext.vars[p2]);
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
        return new String[] {"obj.in.room", "i" + p1, "v" + p2};
    }
//#endif DEBUG
}