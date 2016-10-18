/*
 * ResourceProviderZip.java
 */

package com.sierra.agi.res;

import java.io.*;
import java.util.zip.*;
import com.sierra.agi.res.*;
import com.sierra.agi.Context;

/**
 * Reads Resources from a Zip file.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ResourceProviderZip extends Object implements ResourceProvider
{
    protected ZipFile file;
    
    protected long crc;
    
    protected short[][] enums = new short[4][];
    
    /** Creates new ResourceProviderZip */
    public ResourceProviderZip(Context context, File file) throws IOException
    {
        boolean         a1, a2;
        ZipEntry        entry;
        DataInputStream dstream;
        long            l1;
        int             i, j, k;
        
        try
        {
            this.file = new ZipFile(file);

            entry   = this.file.getEntry("info");
            dstream = new DataInputStream(this.file.getInputStream(entry));
            a1      = dstream.readBoolean();
            a2      = dstream.readBoolean();
            l1      = dstream.readLong();
            crc     = dstream.readLong();
            
            if (context.getEngineVersion() == 0)
            {
                context.setAGDS(a1);
                context.setAmiga(a2);
                context.setEngineVersion((int)l1);
            }
            
            for (i = 0; i < 4; i++)
            {
                k = dstream.readInt();
                enums[i] = new short[k];
                
                for (j = 0; j < k; j++)
                {
                    enums[i][j] = dstream.readShort();
                }
            }
        }
        catch (ZipException zex)
        {
            throw new IOException("Invalid zip format");
        }
    }

    /**
     * Calculate the CRC of the resources.
     *
     * @return CRC of the resources.
     */
    public long getCRC()
    {
        return crc;
    }
    
    /**
     * Retreive the count of resources of the specified type.
     *
     * @param  resType Resource type
     * @return Resource count.
     */
    public int count(byte resType) throws ResourceException
    {
        if (resType > TYPE_WORD)
        {
            throw new ResourceTypeInvalidException();
        }
        
        if (resType >= TYPE_OBJECT)
        {
            return 1;
        }
        
        return enums[resType].length;
    }
    
    /**
     * Enumerate the resource numbers of the specified type.
     *
     * @param  resType Resource type
     * @return Returns an array containing the resource numbers.
     */
    public short[] enum(byte resType) throws ResourceException
    {
        if (resType >= TYPE_OBJECT)
        {
            throw new ResourceTypeInvalidException();
        }
        
        return enums[resType];
    }
    
    /**
     * Retreive the size in bytes of the specified resource.
     *
     * @param  resType   Resource type
     * @param  resNumber Resource number
     * @return Returns the size in bytes of the specified resource.
     */
    public int getSize(byte resType,short resNumber) throws ResourceException
    {
        ZipEntry entry = getEntry(resType, resNumber);
        
        if (entry == null)
        {
            throw new ResourceNotAvailableException();
        }
        
        return (int)entry.getSize();
    }
    
    protected ZipEntry getEntry(byte resType, short resNumber) throws ResourceTypeInvalidException
    {
        ZipEntry entry = null;
        String   type;
        
        switch (resType)
        {
        case TYPE_LOGIC:
            type = "logic";
            break;
        case TYPE_PICTURE:
            type = "picture";
            break;
        case TYPE_SOUND:
            type = "sound";
            break;
        case TYPE_VIEW:
            type = "view";
            break;
        case TYPE_OBJECT:
            type = "object";
            break;
        case TYPE_WORD:
            type = "word";
            break;
        default:
            throw new ResourceTypeInvalidException();
        }
        
        if (resType < TYPE_OBJECT)
        {
            type += File.separator;
            type += resNumber;
        }
        
        return file.getEntry(type);
    }
    
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
    public InputStream open(byte resType, short resNumber) throws ResourceException, IOException
    {
        ZipEntry entry = getEntry(resType, resNumber);
        
        if (entry == null)
        {
            throw new ResourceNotAvailableException(resType + " - " + resNumber);
        }
        
        return file.getInputStream(entry);
    }
    
    /**
     * Convert all the resource of a Resource provider to a Zip file into
     * the specified Output Stream.
     */
    public static void convertToZip(Context context, ResourceProvider provider, OutputStream stream) throws IOException, ResourceException
    {
        DataOutputStream dstream;
        ZipEntry         entry;
        ZipOutputStream  zip;

        /** CRC */
        zip     = new ZipOutputStream(stream);
        dstream = new DataOutputStream(zip);
        entry   = new ZipEntry("info");
        zip.setLevel(9);
        zip.putNextEntry(entry);
        dstream.writeBoolean(context.getAGDS());
        dstream.writeBoolean(context.getAmiga());
        dstream.writeLong(context.getEngineVersion());
        dstream.writeLong(provider.getCRC());

        /** Enums */
        writeEnum(provider, dstream, "logic",   TYPE_LOGIC);
        writeEnum(provider, dstream, "picture", TYPE_PICTURE);
        writeEnum(provider, dstream, "sound",   TYPE_SOUND);
        writeEnum(provider, dstream, "view",    TYPE_VIEW);
        zip.closeEntry();

        writeRes(provider,  zip, "logic",   TYPE_LOGIC);
        writeRes(provider,  zip, "picture", TYPE_PICTURE);
        writeRes(provider,  zip, "sound",   TYPE_SOUND);
        writeRes(provider,  zip, "view",    TYPE_VIEW);
        writeData(provider, zip, "object",  TYPE_OBJECT);
        writeData(provider, zip, "word",    TYPE_WORD);
        
        zip.finish();
        zip.close();
    }
 
    /**
     * Writes an entry to the Zip file
     */
    protected static void writeData(ResourceProvider provider, ZipOutputStream zip, String typeStr, byte type) throws ResourceException, IOException
    {
        ZipEntry    entry = new ZipEntry(typeStr);
        InputStream in;
        
        zip.putNextEntry(entry);

        in = provider.open(type, (short)0);
        writeStream(in, zip);
        in.close();

        zip.closeEntry();
    }
    
    /**
     * Writes all the entry of a type to the Zip file
     */
    protected static void writeEnum(ResourceProvider provider, DataOutputStream dstream, String typeStr, byte type) throws ResourceException, IOException
    {
        short enum[] = provider.enum(type);
        int   i;
        
        dstream.writeInt(enum.length);
        
        for (i = 0; i < enum.length; i++)
        {
            dstream.writeShort(enum[i]);
        }
    }
    
    protected static void writeRes(ResourceProvider provider, ZipOutputStream zip, String typeStr, byte type) throws ResourceException, IOException
    {
        short       enum[] = provider.enum(type);
        ZipEntry    entry;
        int         i;
        InputStream in;
        
        for (i = 0; i < enum.length; i++)
        {
            entry = new ZipEntry(typeStr + File.separator + Integer.toString(enum[i]));
            zip.putNextEntry(entry);

            System.out.println(typeStr + File.separator + Integer.toString(enum[i]));

            try
            {
                in = provider.open(type, enum[i]);
                writeStream(in, zip);
                in.close();
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                System.exit(0);
            }

            zip.closeEntry();
        }
    }
    
    /**
     * Copies all data from input and writes it to the output.
     */
    protected static void writeStream(InputStream in, OutputStream out) throws IOException
    {
        byte b[] = new byte[64];
        int  l;
        
        while (true)
        {
            l = in.read(b, 0, 64);
            
            if (l <= 0)
                break;
            
            out.write(b, 0, l);
        }
    }
}