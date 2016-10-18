/*
 * NoVolumeAvailableException.java
 */

package com.sierra.agi.res;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public final class NoVolumeAvailableException extends ResourceException
{
    public NoVolumeAvailableException()
    {
        super();
    }

    public NoVolumeAvailableException(String msg)
    {
        super(msg);
    }
}