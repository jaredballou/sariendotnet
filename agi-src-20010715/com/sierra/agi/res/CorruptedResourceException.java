/*
 * CorruptedResourceException.java
 */

package com.sierra.agi.res;

/**
 * The resource is currupted!
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public final class CorruptedResourceException extends ResourceException
{
    /**
     * Creates a new CorruptedResourceException.
     */
    public CorruptedResourceException()
    {
        super();
    }

    /**
     * Creates a new CorruptedResourceException with a message.
     *
     * @param msg Message
     */
    public CorruptedResourceException(String msg)
    {
        super(msg);
    }
}