/*
 * InternalLogicException.java
 *
 * Created on April 30, 2001, 7:55 PM
 */

package com.sierra.agi.logic;

import java.io.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InternalLogicException extends LogicException
{
    protected Throwable throwable;
    
    /**
     * Creates new <code>InternalLogicException</code> without detail message.
     */
    public InternalLogicException(Throwable throwable)
    {
        this.throwable = throwable;
    }

    /**
     * Constructs an <code>InternalLogicException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InternalLogicException(String msg, Throwable throwable)
    {
        super(msg);
        this.throwable = throwable;
    }
    
    public Throwable getExceptionTarget()
    {
        return throwable;
    }

    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream s)
    {
        s.print(getClass().getName());

        if (getMessage() == null)
        {
            s.println();
        }
        else
        {
            s.print(": ");
            s.println(getMessage());
        }
        
        throwable.printStackTrace(s);
    }
    
    public void printStackTrace(PrintWriter s)
    {
        s.print(getClass().getName());

        if (getMessage() == null)
        {
            s.println();
        }
        else
        {
            s.print(": ");
            s.print(getMessage());
        }
        
        throwable.printStackTrace(s);
    }
}