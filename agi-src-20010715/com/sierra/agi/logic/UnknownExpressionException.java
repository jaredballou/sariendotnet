/*
 * UnknownInstructionException.java
 */

package com.sierra.agi.logic;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class UnknownExpressionException extends LogicException
{
    /**
     * Creates new <code>UnknownInstructionException</code> without detail
     * message.
     */
    public UnknownExpressionException()
    {
    }

    /**
     * Constructs an <code>UnknownInstructionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnknownExpressionException(String msg)
    {
        super(msg);
    }
}