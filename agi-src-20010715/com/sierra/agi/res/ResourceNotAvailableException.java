/*
 * ResourceNotAvailableException.java
 */

package com.sierra.agi.res;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public final class ResourceNotAvailableException extends ResourceException
{
    public ResourceNotAvailableException()
    {
        super();
    }

    public ResourceNotAvailableException(String msg)
    {
        super(msg);
    }
}