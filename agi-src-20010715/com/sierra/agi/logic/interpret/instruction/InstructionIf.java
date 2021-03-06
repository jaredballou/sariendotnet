/*
 * InstructionIf.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import com.sierra.agi.logic.interpret.condition.*;
import com.sierra.agi.misc.*;
import java.io.*;

/**
 * If Instruction.
 *
 * The most important instruction in the Logic system. It has the ability to
 * modify the instruction executed with a sequence of conditions.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionIf extends InstructionMoving
{
    /** Goto Address */
    protected short gotoAddress;

    /** Contained Expression */
    protected Expression contained;
    
    /**
     * Creates a new If Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used.
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction.
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionIf(Context context, InputStream stream, LogicReader reader, short bytecode) throws Exception
    {
        ByteCasterStream bstream    = new ByteCasterStream(stream);
        Expression       expression = null;
        ExpressionAnd    and        = null;
        int              j, b       = 0;
        
        j = 0;
        while (true)
        {
            b = stream.read();
            
            if ((b <= 0) || (b == 0xFF))
            {
                /* Error in stream or end of condition.*/
                break;
            }
            
            if (j == 1)
            {
                /* There is more than one expression in this condition... */
                and = new ExpressionAnd();
                and.add(expression);
            }
            
            /* Read the next expression */
            expression = reader.readExpression((short)b, stream);
            
            if (j > 0)
            {
                and.add(expression);
            }
            
            j++;
        }
        
        contained   = (j == 1)? expression: and;
        gotoAddress = (short)bstream.lohiReadUnsignedShort();
    }

    /**
     * Execute the Instruction.
     *
     * @param logic         Logic used to execute the instruction.
     * @param logicContext  Logic Context used to execute the instruction.
     * @return Returns the address referenced by this instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) throws Exception
    {
        int size = getSize();
        
        if (contained.evaluate(logic, logicContext))
        {
            /* Condidition is true. No move. Continue has if nothing happened. */
            return size;
        }
        
        /* Move! */
        return size + gotoAddress;
    }
    
    /**
     * Determine Instruction Size.
     *
     * @return Returns the instruction size.
     */
    public int getSize()
    {
        return contained.getSize() + 4;
    }

//#ifdef DEBUG
    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual name of the instruction.
     */
    public String[] getNames()
    {
        String[] names = new String[3];
        
        names[0] = "if";
        names[1] = Integer.toString(gotoAddress);
        names[2] = contained.toString();
        
        return names;
    }
    
    public String toString()
    {
        StringBuffer buff = new StringBuffer(32);
        
        buff.append("if (");
        buff.append(contained.toString());
        buff.append(")");
        return buff.toString();
    }
//#endif DEBUG
    
    /**
     * Retreive the Expession contained in this instruction.
     *
     * @return Returns Instructions contained into the instruction.
     */
    public Expression getExpression()
    {
        return contained;
    }
    
    /**
     * Retreive the Address which is referenced by this instruction.
     *
     * @return Returns the Address referenced by this instruction.
     */
    public int getAddress()
    {
        return gotoAddress;
    }
}