/*
 * ExpressionOr.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;
import java.util.*;

/**
 * Or Expression.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExpressionOr extends Expression
{
    /** Contained Expression. */
    protected Vector contained = new Vector(5, 2);
    
    /** Expression size. */
    protected int instructionSize = 2;
    
    /**
     * Creates a new Or Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionOr(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException, LogicException
    {
        int b;
        
        while (true)
        {
            b = stream.read();
            
            if (b == 0xFC)
            {
                break;
            }
            
            add(reader.readExpression((short)b, stream));
        }
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
        Enumeration enum = contained.elements();
        Expression  expression;
        
        while (enum.hasMoreElements())
        {
            expression = (Expression)enum.nextElement();
            
            if (expression.evaluate(logic, logicContext))
            {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Add a expression to this expression.
     *
     * @param expression Expression to add.
     */
    public void add(Expression expression)
    {
        contained.add(expression);
        instructionSize += expression.getSize();
    }
    
    /**
     * Determine Expression Size.
     *
     * @return Returns the expression size.
     */
    public int getSize()
    {
        return instructionSize;
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
        return contained.elements();
    }

    /**
     * Returns a String represention of the expression.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a String representation.
     */
    public String toString()
    {
        String      s    = "(";
        Enumeration enum = getContainedExpressions();
        Expression  expression;
        
        while (enum.hasMoreElements())
        {
            expression = (Expression)enum.nextElement();
            s += expression.toString();
            
            if (enum.hasMoreElements())
            {
                s += " || ";
            }
        }
        
        s += ")";
        return s;
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