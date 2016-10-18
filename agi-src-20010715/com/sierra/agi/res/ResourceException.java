/*
 * ResourceException.java
 */

package com.sierra.agi.res;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ResourceException extends Exception
{
    /**
     * Creates new <code>ResourceException</code> without detail message.
     */
    public ResourceException()
    {
        super();
    }

    /**
     * Constructs an <code>ResourceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ResourceException(String msg)
    {
        super(msg);
    }
}