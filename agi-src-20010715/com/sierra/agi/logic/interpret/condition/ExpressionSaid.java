/*
 * ExpressionSaid.java
 */

package com.sierra.agi.logic.interpret.condition;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import com.sierra.agi.misc.*;
import com.sierra.agi.word.*;
import java.io.*;
import java.util.*;

/**
 * Said Expression.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExpressionSaid extends Expression
{
    /** Word Table */
    protected Words words;
    
    /** Word Numbers */
    protected int wordNumbers[];
    
    /**
     * Creates a new Said Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionSaid(Context context, InputStream stream, LogicReader reader, short bytecode) throws Exception
    {
        int              i, count;
        ByteCasterStream bstream = new ByteCasterStream(stream);
        
        count = stream.read();
        
        if (context != null)
        {
            if (context.cache != null)
            {
                words = context.cache.getWords();
            }
        }
        
        wordNumbers = new int[count];
        
        for (i = 0; i < count; i++)
        {
            wordNumbers[i] = bstream.lohiReadUnsignedShort();
        }
    }
    
    /**
     * Evaluate Expression.
     *
     * @param logic         Logic used to evaluate the expression.
     * @param logicContext  Logic Context used to evaluate the expression.
     * @return Returns the result of the evaluation.
     */
    public boolean evaluate(Logic logic, LogicContext context)
    {
        return false;
    }

    /**
     * Determine Expression Size.
     *
     * @return Returns the expression size.
     */
    public int getSize()
    {
        return 2 + (wordNumbers.length * 2);
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
        String[] names = new String[1 + wordNumbers.length];
        int      i;
        
        names[0] = "said";
        
        for (i = 0; i < wordNumbers.length; i++)
        {
            names[i+1] = "w" + wordNumbers[i];
        }
        
        return names;
    }
//#endif DEBUG
}