/*
 * ResourceProviderFS.java
 */

package com.sierra.agi.res.v2;
import  java.io.*;
import  java.util.*;

import  com.sierra.agi.Context;
import  com.sierra.agi.misc.*;
import  com.sierra.agi.res.*;

/**
 * Provide access to resources via the standard storage methods.
 * It reads unmodified sierra's resource files.
 * <P>
 * All AGI games have either one directory file, or more commonly, four.
 * AGI version 2 games will have the files LOGDIR, PICDIR, VIEWDIR, and SNDDIR.
 * This single file is basically the four version 2 files joined together
 * except that it has an 8 byte header giving the position of each directory
 * within the single file.
 * <P>
 * The directory files give the location of the data types within the VOL
 * files. The type of directory determines the type of data. For example, the
 * LOGDIR gives the locations of the LOGIC files.
 * <P>
 * <I>Note</I>: In this description and elsewhere in documents written by me,
 * the AGI data called LOGIC, PICTURE, VIEW, and SOUND data are referred to by
 * me as files even though they are part of a single VOL file. I think of
 * the VOL file as sort of a virtual storage device in itself that holds many
 * files. Some documents call the files contains in VOL files "resources".
 * <P>
 * <B>Version 2 directories</B><BR>
 * Each directory file is of the same format. They contain a finite number
 * of three byte entries, no more than 256. The size will vary depending on the
 * number of files of the type that the directory file is pointing to. Dividing
 * the filesize by three gives the maximum file number of that type of data
 * file. Each entry is of the following format:
 * <PRE>
 * Byte 1          Byte 2          Byte 3 
 * 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 
 * V V V V P P P P P P P P P P P P P P P P P P P P 
 *
 * V = VOL number.
 * P = Position (offset into VOL file)</PRE>
 *
 * The entry number itself gives the number of the data file that it is
 * pointing to. For example, if the following three byte entry is entry
 * number 45 in the SOUND directory file, <CODE>12 3D FE</CODE>
 * then SOUND.45 is located at position 0x23DFE in the VOL.1 file. The first
 * entry number is entry 0.
 * <P>
 * If the three bytes contain the value 0xFFFFFF, then the resource does not
 * exist.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ResourceProviderFS extends Object implements ResourceProvider
{
    /** Resource's CRC. */
    protected long crc;
    
    /** Resource's Types Counts. */
    protected short count[];
    
    /** Resource's Entries Tables. */
    protected DirectoryEntry entries[][];
    
    /** Path to resources files. */
    protected File path;
    
    /** Resource's directories files. */
    protected File dir[];
    
    /** Resource's volumes files. */
    protected File vol[];
    
    /** Game Context. */
    protected Context context;

    /**
     * Initialize the ResourceProvider implentation to access
     * resource on the file system.
     *
     * @param folder Resource's folder or File inside the resource's
     *               folder.
     */    
    public ResourceProviderFS(Context context, File folder) throws IOException, ResourceException
    {
        if (!folder.exists())
        {
            throw new FileNotFoundException();
        }

        this.context = context;

        if (folder.isDirectory())
            path = folder.getAbsoluteFile();
        else
            path = folder.getParentFile();
        
        findVolumes();
        findDirectories();
        calculateCRC();

        entries = new DirectoryEntry[4][256];
        count   = new short[4];

        readDirectories();
        
        if (context.getEngineVersion() == 0)
        {
            VersionTable.lookup(context, getClass().getResourceAsStream("version.conf"), getCRC());
        }
    }

    /**
     * Calculate the CRC of the resources. In this implentation
     * the CRC is not calculated by this function, it only return
     * the cached CRC value.
     *
     * @return CRC of the resources.
     */
    public long getCRC()
    {
        return crc;
    }

    /**
     * Retreive the count of resources of the specified type.
     * Only valid with Locic, Picture, Sound and View resource
     * types.
     *
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_LOGIC
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_PICTURE
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_SOUND
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_VIEW
     * @param  resType Resource type
     * @return Resource count.
     */
    public int count(byte resType) throws ResourceException
    {
        if (resType > TYPE_WORD)
            throw new ResourceTypeInvalidException();
        
        if (resType >= TYPE_OBJECT)
            return 1;
        
        return count[resType];
    }
    
    /**
     * Enumerate the resource numbers of the specified type.
     * Only valid with Locic, Picture, Sound and View resource
     * types.
     *
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_LOGIC
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_PICTURE
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_SOUND
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_VIEW
     * @param  resType Resource type
     * @return Array containing the resource numbers.
     */
    public short[] enum(byte resType) throws ResourceException
    {
        short i, j, k;
        short s[];
        
        if (resType >= TYPE_OBJECT)
        {
            throw new ResourceTypeInvalidException();
        }
        
        k = count[resType];
        s = new short[k];
        
        j = 0;
        for (i = 0; i < 256; i++)
        {
            if (entries[resType][i] != null)
            {
                s[j] = i;
                j++;
                
                if (j == k)
                    break;
            }
        }
        
        return s;
    }
    
    /**
     * Open the specified resource and return a pointer
     * to the resource. The InputStream is decrypted/decompressed,
     * if neccessary, by this function. (So you don't have to care
     * about them.)
     *
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_LOGIC
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_OBJECT
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_PICTURE
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_SOUND
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_VIEW
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_WORD
     * @param  resType   Resource type
     * @param  resNumber Resource number. Ignored if resource type
     *                   is <CODE>TYPE_OBJECT</CODE> or
     *                   <CODE>TYPE_WORD</CODE>
     * @return InputStream linked to the specified resource.
     */
    public InputStream open(byte resType, short resNumber) throws ResourceException
    {
        if (resType > TYPE_WORD)
        {
            throw new ResourceTypeInvalidException();
        }
        
        try
        {
            DirectoryEntry entry;
            
            switch (resType)
            {
            case ResourceProvider.TYPE_OBJECT:
                if (isCrypted(dir[resType]))
                {
                    return new CryptedStream(context.getKey(), new FileInputStream(dir[resType]));
                }
            case ResourceProvider.TYPE_WORD:
                return new FileInputStream(dir[resType]);
            }
            
            entry = entries[resType][resNumber];
            
            if (entry != null)
            {
                if (resType == ResourceProvider.TYPE_LOGIC)
                {
                    return new LogicInputStream(context.getKey(), entry);
                }
                else
                {
                    return new DirectoryEntryStream(entry);
                }
            }
        }
        catch (IOException ioex)
        {
        }
        
        throw new ResourceNotAvailableException();
    }
    
    /**
     * Retreive the size in bytes of the specified resource.
     *
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_LOGIC
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_OBJECT
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_PICTURE
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_SOUND
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_VIEW
     * @see    com.sierra.agi.res.ResourceProvider#TYPE_WORD
     * @param  resType   Resource type
     * @param  resNumber Resource number. Ignored if resource type
     *                   is <CODE>TYPE_OBJECT</CODE> or
     *                   <CODE>TYPE_WORD</CODE>
     * @return Size in bytes of the specified resource.
     */
    public int getSize(byte resType, short resNumber) throws ResourceException
    {
        DirectoryEntry entry;
        
        switch (resType)
        {
        case ResourceProvider.TYPE_OBJECT:
        case ResourceProvider.TYPE_WORD:
            return (int)dir[resType].length();
        }

        if (resType >= TYPE_OBJECT)
        {
            throw new ResourceTypeInvalidException();
        }
        
        entry = entries[resType][resNumber];
        
        if (entry != null)
        {
            return entry.getLength();
        }
        
        return -1;
    }
    
    /** Find volumes files */
    protected void findVolumes() throws NoVolumeAvailableException
    {
        File   volf;
        Vector vols = new Vector();
        
        int i = 0;
        
        while (true)
        {
            volf = new File(path, "vol." + Integer.toString(i));
            
            if (!volf.exists())
            {
                break;
            }
            
            vols.add(volf);
            i++;
        }

        if (vols.size() == 0)
        {
            throw new NoVolumeAvailableException();
        }
        
        vol = new File[vols.size()];
        
        for (i = 0; i < vol.length; i++)
        {
            vol[i] = (File)vols.get(i);
        }
    }
    
    /** Find all directory files */
    protected void findDirectories() throws NoDirectoryAvailableException
    {
        int i, j;
        
        dir     = new File[6];
        dir[0]  = new File(path, "logdir");
        dir[1]  = new File(path, "picdir");
        dir[2]  = new File(path, "snddir");
        dir[3]  = new File(path, "viewdir");
        dir[4]  = new File(path, "object");
        dir[5]  = new File(path, "words.tok");
        
        for (i = 0, j = 0; i < 6; i++)
        {
            if (dir[i].exists())
            {
                j++;
            }
            else
            {
                dir[i] = null;
            }
        }
        
        if (j < 3)
        {
            throw new NoDirectoryAvailableException();
        }
    }
    
    /** Calculate the Resource's CRC */
    protected void calculateCRC()
    {
        File dirf = new File(path, "crc");
        int  i, j;
        
        try
        {
            /* Check if the CRC has been pre-calculated */
            DataInputStream meta = new DataInputStream(new FileInputStream(dirf));
            
            crc = meta.readLong();
            meta.close();
        }
        catch (IOException ex)
        {
            /* CRC need to be calculated from scratch */
            for (i = 0; i < dir.length; i++)
            {
                try
                {
                    FileInputStream stream = new FileInputStream(dir[i]);

                    while (true)
                    {
                        j = stream.read();

                        if (j == -1)
                            break;

                        crc += j;
                    }
                    
                    stream.close();
                }
                catch (IOException ioex)
                {
                }
            }
            
            try
            {
                /* Write down the CRC for next times */
                DataOutputStream meta = new DataOutputStream(new FileOutputStream(dirf));
                
                meta.writeLong(crc);
                meta.close();
            }
            catch (IOException ioex)
            {
            }
        }
    }
    
    /** Read directory files */
    protected void readDirectories()
    {
        DirectoryEntry  entry;
        FileInputStream streamDir;
        DataInputStream streamData;
        int             i,  j;
        int             s1, s2, s3;
        
        for (i = 0; i < 4; i++)
        {
            if (dir[i] == null)
                continue;
            
            try
            {
                count[i] = readDirectory(new FileInputStream(dir[i]), entries[i]);
            }
            catch (IOException ioex)
            {
            }
        }
    }
    
    /** Read a single directory file */
    protected short readDirectory(InputStream streamDir, DirectoryEntry[] entries) throws IOException
    {
        DirectoryEntry entry;
        short          c = 0;
        int            i = 0;
        int            s1, s2, s3;
        
        try
        {
            while (true)
            {
                s1 = streamDir.read();
                
                if (s1 < 0)
                {
                    break;
                }
                
                s2 = streamDir.read();
                
                if (s2 < 0)
                {
                    break;
                }
                
                s3 = streamDir.read();
                
                if (s3 < 0)
                {
                    break;
                }

                if (s1 != 0xFF)
                {
                    entry = newEntry();
                    
                    try
                    {
                        entry.volume  = (byte)(s1 >> 4);
                        entry.offset  = (s1 << 16) + (s2 << 8) + s3;
                        entry.offset &= 0x000FFFFF;
                        entry.file    = vol[entry.volume];
                        
                        entries[i] = entry;
                        c++;
                    }
                    catch (RuntimeException rex)
                    {
                    }
                }

                i++;
            }
        }
        catch (IOException ioex)
        {
        }
        finally
        {
            streamDir.close();
        }
        
        return c;
    }
    
    protected DirectoryEntry newEntry()
    {
        return new DirectoryEntry();
    }
    
    public static boolean isCrypted(File file)
    {
        boolean b = false;
        
        try
        {
            ByteCasterStream bstream = new ByteCasterStream(new FileInputStream(file));
            
            if (bstream.lohiReadUnsignedShort() > file.length())
            {
                b = true;
            }
            
            bstream.close();
            return b;
        }
        catch (Throwable t)
        {
            return false;
        }
    }
}