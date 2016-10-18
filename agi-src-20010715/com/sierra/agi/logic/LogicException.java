/*
 * LogicException.java
 */

package com.sierra.agi.logic;

import com.sierra.agi.debug.Dumpable;
import java.io.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class LogicException extends Exception implements Dumpable
{
    /** Logic */
    public Logic logic;
    
    /** Logic Instruction Pointer */
    public int logicIP = -1;
    
    /** Logic Context */
    public LogicContext logicContext;
    
    /**
     * Creates new <code>LogicException</code> without detail message.
     */
    public LogicException()
    {
    }

    /**
     * Constructs an <code>LogicException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public LogicException(String msg)
    {
        super(msg);
    }

    /** Exception Dump */
    public void dump(PrintWriter p)
    {
        if (logic != null)
        {
            if (logic instanceof Dumpable)
            {
                ((Dumpable)logic).dump(p);
            }
        }

        if (logicContext != null)
        {
            logicContext.dump(p);
            p.println();
            
            if (logicIP != -1)
            {
                p.print("ip=");
                p.println(logicIP);
            }
        }
    }
}