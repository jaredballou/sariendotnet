/*
 * LogicReader.java
 */

package com.sierra.agi.logic.interpret;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.condition.*;
import com.sierra.agi.logic.interpret.instruction.*;
import com.sierra.agi.misc.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class LogicReader extends Object
{
    /** Game Context */
    protected Context context;
    
    /** Instruction Table */
    protected Properties instructionTable;
    
    /** Expression Table */
    protected Properties expressionTable;
    
    /** Logic Messages */
    protected String[] messages;
    
    /** Instructions */
    protected Vector instructions = new Vector(30, 10);
    
    /** Creates new Logic Reader */
    public LogicReader()
    {
    }

    public void loadLogic(Context context, InputStream stream, int size) throws IOException, LogicException
    {
        byte b[] = new byte[size];
        
        this.context = context;
        ByteCaster.fill(stream, b, 0, size);
        stream.close();
        
        readMessages(b);
        
        instructionTable = new Properties();
        instructionTable.load(getClass().getResourceAsStream("instruction.conf"));
        expressionTable = new Properties();
        expressionTable.load(getClass().getResourceAsStream("expression.conf"));

        readInstructions(new ByteArrayInputStream(b, 2, ByteCaster.lohiUnsignedShort(b, 0)));
    }
    
    /** Load Instruction Parameters Definitions */
    private Class loadInstructionTypes[] = {Context.class, InputStream.class, LogicReader.class, short.class};

    public Expression readExpression(short bytecode, InputStream stream) throws LogicException
    {
        Expression  expression;
        String      expressionByte;
        String      expressionClass;
        Class       clazz;
        Constructor cons;
        Object      o[];
        
        expressionByte  = Integer.toHexString((int)bytecode);
        expressionClass = expressionTable.getProperty(expressionByte);

        if (expressionClass == null)
        {
            throw new UnknownExpressionException(expressionByte);
        }

        if (expressionClass.indexOf(".") < 0)
        {
            expressionClass = "com.sierra.agi.logic.interpret.condition." + expressionClass;
        }

        try
        {
            clazz = Class.forName(expressionClass);
            cons  = clazz.getConstructor(loadInstructionTypes);

            o    = new Object[4];
            o[0] = context;
            o[1] = stream;
            o[2] = this;
            o[3] = new Short((short)bytecode);

            expression = (Expression)cons.newInstance(o);
            return expression;
        }
        catch (ClassNotFoundException ex)
        {
            throw new UnknownExpressionException();
        }
        catch (InvocationTargetException ex)
        {
            if (ex.getTargetException() instanceof LogicException)
            {
                throw (LogicException)ex.getTargetException();
            }

            throw new InternalLogicException(expressionByte, ex.getTargetException());
        }
        catch (Exception ex)
        {
            throw new InternalLogicException(expressionByte, ex);
        }
    }

    public Expression readExpression(InputStream stream) throws IOException, LogicException
    {
        int bytecode = stream.read();

        if (bytecode < 0)
        {
            return null;
        }

        return readExpression((short)bytecode, stream);
    }
    
    public Instruction readInstruction(short bytecode, InputStream stream) throws LogicException
    {
        Instruction    instruct;
        String         instructionByte;
        String         instructionClass;
        Class          clazz;
        Constructor    cons;
        Object         o[];
        
        instructionByte  = Integer.toHexString((int)bytecode);
        instructionClass = instructionTable.getProperty(instructionByte);

        if (instructionClass == null)
        {
            throw new UnknownInstructionException(instructionByte);
        }

        if (instructionClass.indexOf(".") < 0)
        {
            instructionClass = "com.sierra.agi.logic.interpret.instruction." + instructionClass;
        }

        try
        {
            clazz = Class.forName(instructionClass);
            cons  = clazz.getConstructor(loadInstructionTypes);

            o    = new Object[4];
            o[0] = context;
            o[1] = stream;
            o[2] = this;
            o[3] = new Short((short)bytecode);

            instruct = (Instruction)cons.newInstance(o);
            return instruct;
        }
        catch (ClassNotFoundException ex)
        {
            throw new UnknownInstructionException(instructionClass + " (" + instructionByte + ")");
        }
        catch (InvocationTargetException ex)
        {
            if (ex.getTargetException() instanceof LogicException)
            {
                throw (LogicException)ex.getTargetException();
            }

            throw new InternalLogicException(instructionByte, ex.getTargetException());
        }
        catch (Exception ex)
        {
            throw new InternalLogicException(instructionByte, ex);
        }
    }
    
    public Instruction readInstruction(InputStream stream) throws IOException, LogicException
    {
        int bytecode = stream.read();

        if (bytecode < 0)
        {
            return null;
        }

        return readInstruction((short)bytecode, stream);
    }
    
    public void readInstructions(InputStream stream) throws IOException, LogicException
    {
        Instruction instruct;
        
        try
        {
            while (true)
            {
                instruct = readInstruction(stream);
                
                if (instruct == null)
                {
                    break;
                }
                
                instructions.add(instruct);
            }
        }
        catch (EOFException ex)
        {
            ex.printStackTrace();
        }
    }
    
    protected void readMessages(byte[] b)
    {
        int    startPos;
        int    numMessages;
        int    fileData, marker;
        int    i, j, k, l;
    
        startPos    = ByteCaster.lohiUnsignedShort(b, 0) + 2;
        numMessages = ByteCaster.lohiUnsignedByte(b, startPos);
        fileData    = startPos + 3;
        
        messages = new String[numMessages + 1];
        marker   = fileData;
        
        for (i = 1; i <= numMessages; i++, marker += 2)
        {
            j = ByteCaster.lohiUnsignedShort(b, marker);
            
            if (j == 0)
            {
                continue;
            }
            
            j -= 2;
            j += fileData;
            l  = j;
            
            while (b[l] != 0)
            {
                l++;
            }
            
            try
            {
                messages[i] = new String(b, j, l - j, "US-ASCII");
            }
            catch (UnsupportedEncodingException ex)
            {
            }
        }
    }
    
    public String[] getMessages()
    {
        return messages;
    }
    
    public Vector getInstructions()
    {
        return instructions;
    }
}