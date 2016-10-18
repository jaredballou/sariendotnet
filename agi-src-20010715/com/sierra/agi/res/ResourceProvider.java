/*
 * ResourceProvider.java
 */

package com.sierra.agi.res;
import java.lang.*;
import java.io.*;

/**
 * The ResourceProvider interface is a standard
 * way for loading resources dynamicly. Gives the
 * interresing possibility of being able to read
 * them from every kind of data container.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public interface ResourceProvider
{
    /** Logic Resource Type. */
    public static final byte TYPE_LOGIC = 0;

    /** Picture Resource Type. */
    public static final byte TYPE_PICTURE = 1;

    /** Sound Resource Type. */
    public static final byte TYPE_SOUND = 2;

    /** View Resource Type. */
    public static final byte TYPE_VIEW = 3;

    /** Object File. */
    public static final byte TYPE_OBJECT = 4;

    /** Word Tokenizer File */
    public static final byte TYPE_WORD = 5;

    /**
     * Calculate the CRC of the resources.
     *
     * @return CRC of the resources.
     */
    public long getCRC();
    
    /**
     * Retreive the count of resources of the specified type.
     *
     * @param  resType Resource type
     * @return Resource count.
     */
    public int count(byte resType) throws ResourceException;
    
    /**
     * Enumerate the resource numbers of the specified type.
     *
     * @param  resType Resource type
     * @return Returns an array containing the resource numbers.
     */
    public short[] enum(byte resType) throws ResourceException;

    /**
     * Retreive the size in bytes of the specified resource.
     *
     * @param  resType   Resource type
     * @param  resNumber Resource number
     * @return Returns the size in bytes of the specified resource.
     */
    public int getSize(byte resType, short resNumber) throws ResourceException;
    
    /**
     * Open the specified resource and return a pointer
     * to the resource. The InputStream is decrypted/decompressed,
     * if neccessary, by this function. (So you don't have to care
     * about them.)
     *
     * @param  resType   Resource type
     * @param  resNumber Resource number
     * @return InputStream linked to the specified resource.
     */
    public InputStream open(byte resType, short resNumber) throws ResourceException, IOException;
}