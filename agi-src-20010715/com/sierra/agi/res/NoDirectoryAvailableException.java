/*
 * NoDirectoryAvailableException.java
 */

package com.sierra.agi.res;

/**
 * There is no directory available. Throwed when a ResourceProvider is created
 * with a folder that doesn't contain any resource directory.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public final class NoDirectoryAvailableException extends ResourceException
{
    /**
     * Creates a new NoDirectoryAvailableException.
     */
    public NoDirectoryAvailableException()
    {
        super();
    }

    /**
     * Creates a new NoDirectoryAvailableException with a message.
     *
     * @param msg Message
     */
    public NoDirectoryAvailableException(String msg)
    {
        super(msg);
    }
}