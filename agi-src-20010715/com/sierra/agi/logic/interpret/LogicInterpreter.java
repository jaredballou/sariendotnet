/*
 * LogicInterpreter.java
 */

package com.sierra.agi.logic.interpret;

import com.sierra.agi.Context;
import com.sierra.agi.debug.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.instruction.*;
import java.io.*;
import java.util.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class LogicInterpreter extends Logic implements Dumpable
{
    /** Messages */
    protected String[] messages;
    
    /** Instructions */
    protected Vector instructions;
    
    /** Scan Start */
    protected int scanStart = 0;
    
    /** Execute! */
    public void execute(LogicContext context) throws LogicException
    {
        Instruction instruction;
        int         result = 0;
        int         ip     = scanStart;
        int         lastip = scanStart;

        try
        {
            while (true)
            {
                instruction = (Instruction)instructions.get(ip);
                lastip      = ip;

                try
                {
                    result = instruction.execute(this, context);
                }
                catch (LogicSetScanStart ex)
                {
                    scanStart = ip;
                    result    = instruction.getSize();
                }
                catch (LogicResetScanStart ex)
                {
                    scanStart = 0;
                    result    = instruction.getSize();
                }

                if (result == 0)
                {
                    /** Return Instruction */
                    return;
                }
                else if (result > 0)
                {
                    while (result != 0)
                    {
                        instruction = (Instruction)instructions.get(ip++);
                        result     -= instruction.getSize();
                    }
                }
                else
                {
                    while (result != 0)
                    {
                        instruction = (Instruction)instructions.get(--ip);
                        result     += instruction.getSize();
                    }
                }
            }
        }
        catch (LogicException lex)
        {
            if (lex.logic == null)
            {
                lex.logic        = this;
                lex.logicIP      = ip;
                lex.logicContext = context;
            }
            
            throw lex;
        }
        catch (Throwable t)
        {
            LogicException lex = new InternalLogicException(t);

            lex.logic        = this;
            lex.logicIP      = ip;
            lex.logicContext = context;
            throw lex;
        }
    }
    
    /** Obtain a String */
    public String getString(int stringNumber)
    {
        return messages[stringNumber];
    }
    
    /** Modify Starting IP */
    public void setScanStart(int scanStart)
    {
        if (scanStart < 0)
            this.scanStart = 0;
        else
            this.scanStart = scanStart;
    }
    
    /** Dump Logic context */
    public void dump(PrintWriter writer)
    {
//#ifdef DUMP
        Enumeration enum = instructions.elements();
        int         i = 0, j, ip = 0;
        Instruction instruction;
        
        writer.println();
        writer.println("** Instruction Table **");
        while (enum.hasMoreElements())
        {
            instruction = (Instruction)enum.nextElement();
            
            writer.print(Integer.toHexString(i));
            writer.print(": ");
            
            writer.print(Integer.toHexString(ip));
            writer.print(": ");
            
            if (instruction instanceof InstructionMoving)
            {
                InstructionMoving imov = (InstructionMoving)instruction;
                
                writer.print(imov.toString());
                writer.print(" (addr: ");
                writer.print(Integer.toHexString(ip + imov.getSize() + imov.getAddress()));
                writer.println(")");
            }
            else
            {
                writer.println(instruction.toString());
            }

            ip += instruction.getSize();
            i++;
        }

        for (i = 1, j = 0; i < messages.length; i++)
        {
            if (messages[i] != null)
            {
                if (j == 0)
                {
                    writer.println();
                    writer.println("** Message Table **");
                }
                
                writer.print(i);
                writer.print(": ");
                writer.println(messages[i]);
                j++;
            }
        }
//#endif DUMP
    }
    
    /** Creates new Logic Interpreter */
    public LogicInterpreter(Context context, InputStream stream, int size) throws IOException, LogicException
    {
        LogicReader    reader = new LogicReader();
        LogicException lex    = null;
        
        try
        {
            reader.loadLogic(context, stream, size);
        }
        catch (LogicException ex)
        {
            lex = ex;
        }
        
        messages     = reader.getMessages();
        instructions = reader.getInstructions();
        
        if (lex != null)
        {
            lex.logic = this;
            throw lex;
        }
    }
}