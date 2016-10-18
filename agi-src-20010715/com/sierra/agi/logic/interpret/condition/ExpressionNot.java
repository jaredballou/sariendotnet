/*
 * ExpressionNot.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;
import java.util.*;

/**
 * Not Expression.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExpressionNot extends Expression
{
    /** Contained Expression */
    protected Expression contained;
    
    /**
     * Creates a new Not Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionNot(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException, LogicException
    {
        contained = reader.readExpression(stream);
    }
    
    /**
     * Evaluate Expression.
     *
     * @param logic         Logic used to evaluate the expression.
     * @param logicContext  Logic Context used to evaluate the expression.
     * @return Returns the result of the evaluation.
     */
    public boolean evaluate(Logic logic, LogicContext logicContext) throws Exception
    {
        return !contained.evaluate(logic, logicContext);
    }

    /**
     * Determine Expression Size.
     *
     * @return Returns the expression size.
     */
    public int getSize()
    {
        return contained.getSize() + 1;
    }

//#ifdef DEBUG
    /**
     * Retreive contained expressions.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a Enumeration of expression contained. May be <CODE>null</CODE>.
     */
    public Enumeration getContainedExpressions()
    {
        return new NotEnumeration();
    }
    
    /**
     * Enumeration support for this Expression.
     */
    class NotEnumeration extends Object implements Enumeration
    {
        protected int count = 0;
        
        public boolean hasMoreElements()
        {
            return (count == 0)? true: false;
        }
        
        public Object nextElement()
        {
            count++;
            return (count == 1)? contained: null;
        }
    }

    /**
     * Returns a String represention of the expression.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a String representation.
     */
    public String toString()
    {
        return "!" + contained.toString();
    }

    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Always return <CODE>null</CODE> in this implentation.
     */
    public String[] getNames()
    {
        return null;
    }
//#endif DEBUG
}