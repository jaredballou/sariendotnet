/*
 * PictureException.java
 */

package com.sierra.agi.pic;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class PictureException extends java.lang.Exception {

    /**
     * Creates new <code>PictureException</code> without detail message.
     */
    public PictureException()
    {
    }


    /**
     * Constructs an <code>PictureException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public PictureException(String msg)
    {
        super(msg);
    }
}