/*
 * UnknownInstructionException.java
 */

package com.sierra.agi.logic;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class UnknownInstructionException extends LogicException
{
    /**
     * Creates new <code>UnknownInstructionException</code> without detail
     * message.
     */
    public UnknownInstructionException()
    {
    }

    /**
     * Constructs an <code>UnknownInstructionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnknownInstructionException(String msg)
    {
        super(msg);
    }
}