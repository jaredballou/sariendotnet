/*
 * DirectoryEntryStream.java
 */

package com.sierra.agi.res.v2;
import  com.sierra.agi.misc.*;
import  com.sierra.agi.res.ResourceException;
import  java.io.*;

/**
 * Implentation of <CODE>InputStream</CODE> that gives access
 * to a specific resource inside a volume file.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class DirectoryEntryStream extends SegmentedInputStream
{
    /**
     * Creates a new DirectoryEntryStream.
     *
     * @param  pEntry      Pointer to a entry in the directory table.
     * @throws IOException Can throw IOException.
     */
    public DirectoryEntryStream(DirectoryEntry entry) throws IOException, ResourceException
    {
        super(new RandomAccessFile(entry.file, "r"), entry.getOffset(), entry.getPhysicalLength());
    }
}