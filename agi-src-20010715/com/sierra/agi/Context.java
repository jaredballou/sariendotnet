/*
 * Context.java
 */

package com.sierra.agi;

import  com.sierra.agi.res.ResourceCache;
import  java.awt.*;
import  java.util.*;
import  java.io.*;
import  java.net.URL;
import  java.net.URLConnection;

/**
 * Contains the operating parameters.
 * <ul>
 * <li> AGDS Support
 * <li> Decryption Key
 * <li> Engine Version Emulation
 * <li> Waveform used by sound reproduction
 * </ul>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class Context extends Object
{
    /** AGDS Support Flag. */
    protected boolean agds;

    /** Amiga Support Flag. */
    protected boolean amiga;
    
    /** Decryption Key. Used to decrypt object file. */
    protected String key;
    
    /** Engine Version Emulation. Used by the Logic interpreter. */
    protected int engineVersion;
    
    /** Waveform ID. Used by the AGI Sound System. */
    protected byte waveid;

    /** Zoom Factor for Width */
    protected int zoomw;
    
    /** Zoom Factor for Height */
    protected int zoomh;
    
    /** AWT Toolkit to use */
    protected Toolkit toolkit;
    
    /**
     * AGDS's Decryption Key. This key is used to decrypt
     * AGDS games.
     */
    protected static final String agdsKey = "Alex Simkin";
    
    /**
     * Sierra's Decryption Key. This key used to decrypt
     * original sierra games.
     */
    protected static final String sierraKey = "Avis Durgan";
    
    /** Ramp waveform ID */
    public static final byte WAVEID_RAMP = (byte)0;

    /** Square waveform ID (PC) */
    public static final byte WAVEID_SQUARE = (byte)1;
    
    /** Square waveform ID (Mac) */
    public static final byte WAVEID_MAC = (byte)2;
    
    /** Resource Cache */
    public ResourceCache cache = null;
    
    /** Context constructor. */
    public Context()
    {
        this.toolkit = Toolkit.getDefaultToolkit();
        setDefaults();
    }
    
    public Context(Toolkit toolkit)
    {
        this.toolkit = toolkit;
        setDefaults();
    }
    
    /**
     * Set AGDS support. Alteration of this property will alter
     * the value of the decryption key.
     *
     * @param enabled <CODE>true</CODE> to enable AGDS support.
     */
    public void setAGDS(boolean enabled)
    {
        agds = enabled;
        
        if (key == null)
            return;
        
        if (!enabled)
        {
            if (key.equals(agdsKey))
            {
                key = sierraKey;
            }
        }
        else
        {
            if (key.equals(sierraKey))
            {
                key = agdsKey;
            }
        }
    }
    
    /**
     * Retreive AGDS support.
     *
     * @return Returns <CODE>true</CODE> if the AGDS support is enabled.
     */
    public boolean getAGDS()
    {
        return agds;
    }

    /**
     * Set Amiga support.
     *
     * @param enabled <CODE>true</CODE> to enable Amiga support.
     */
    public void setAmiga(boolean enabled)
    {
        amiga = enabled;
    }
    
    /**
     * Retreive Amiga support.
     *
     * @return Returns <CODE>true</CODE> if the Amiga support is enabled.
     */
    public boolean getAmiga()
    {
        return amiga;
    }

    /**
     * Retreive current decryption key.
     *
     * @return Returns the current decryption key.
     */
    public String getKey()
    {
        return key;
    }
    
    /**
     * Set decryption key.
     *
     * @param newKey The new decryption key.
     */
    public void setKey(String newKey)
    {
        key = newKey;
    }
    
    /**
     * Retreive current engine version emulation.
     *
     * @return The engine version emulation.
     */
    public int getEngineVersion()
    {
        return engineVersion;
    }
    
    /**
     * Set engine version emulation. Alteration of this value
     * may alter the logic interpreter engine behaviour.
     *
     * @param newEngineVersion The new engine version emulation.
     */
    public void setEngineVersion(int newEngineVersion)
    {
        engineVersion = newEngineVersion;
    }
    
    /**
     * Set current Waveform ID.
     *
     * @param newWaveid New Waveform ID to use.
     */
    public void setWaveform(byte newWaveid)
    {
        waveid = newWaveid;
    }
    
    /**
     * Retreive current Waveform ID.
     *
     * @return Returns Waveform ID.
     */
    public byte getWaveform()
    {
        return waveid;
    }
    
    public int getZoomW()
    {
        return zoomw;
    }
    
    public int getZoomH()
    {
        return zoomh;
    }
    
    public Toolkit getToolkit()
    {
        return toolkit;
    }
    
    /**
     * Load configuration file from the default Configuration file.
     *
     * @throws IOException Throws IOException throwed during read.
     */
    public void loadConfig() throws IOException
    {
        InputStream stream = getClass().getResourceAsStream("agi.conf");
        Properties  p;
        
        if (stream == null)
        {
            throw new FileNotFoundException("agi.conf");
        }
        
        p = new Properties();
        p.load(stream);
        loadConfig(p);
    }

    /**
     * Load configuration file from a specified Input Stream.
     *
     * @param stream Stream from where to load the configuration.
     * @throws IOException Throws IOException throwed during read.
     */
    public void loadConfig(InputStream stream) throws IOException
    {
        Properties p = new Properties();

        p.load(stream);
        loadConfig(p);
    }
    
    /**
     * Load configuration file from a specified Properties object.
     *
     * @param p <CODE>Properties</CODE> Object to load configuration from.
     */
    public void loadConfig(Properties p)
    {
        String prop;
        
        setDefaults();
        
        prop = p.getProperty("agds");
        if (prop != null)
        {
            if (prop.equalsIgnoreCase("true"))
                setAGDS(true);
            else
                setAGDS(false);
        }

        prop = p.getProperty("amiga");
        if (prop != null)
        {
            if (prop.equalsIgnoreCase("true"))
                setAmiga(true);
            else
                setAmiga(false);
        }
        
        prop = p.getProperty("engine");
        if (prop != null)
        {
            setEngineVersion(Integer.valueOf(prop, 16).intValue());
        }

        prop = p.getProperty("key");
        if (prop != null)
        {
            key = prop;
        }

        prop = p.getProperty("waveform");
        if (prop != null)
        {
            setWaveform(toWaveID(prop));
        }
        
        prop = p.getProperty("zoom");
        if (prop != null)
        {
            int val = Integer.valueOf(prop).intValue();
            
            if (val > 0)
            {
                zoomw = val * 2;
                zoomh = val;
            }
        }
        
        prop = p.getProperty("zoomw");
        if (prop != null)
        {
            zoomw = Integer.valueOf(prop).intValue();
        }

        prop = p.getProperty("zoomh");
        if (prop != null)
        {
            zoomh = Integer.valueOf(prop).intValue();
        }
    }

    /**
     * Save configuration to a stream.
     *
     * @param stream Stream to store configuration.
     * @throws IOException Throws IOException throwed during writes.
     */
    public void saveConfig(OutputStream stream) throws IOException
    {
        Properties p = new Properties();
        
        saveConfig(p);
        p.store(stream, "AGI Configuration File");
    }

    /**
     * Save configuration to a <CODE>Properties</CODE> object.
     *
     * @param p <CODE>Properties</CODE> Object to store configuration.
     */    
    public void saveConfig(Properties p)
    {
        p.setProperty("agds",   getAGDS()?  "true": "false");
        p.setProperty("amiga",  getAmiga()? "true": "false");
        p.setProperty("engine", Integer.toHexString(getEngineVersion()));
        p.setProperty("key",    getKey());
        
        switch (getWaveform())
        {
        case WAVEID_SQUARE:
            p.setProperty("waveform", "square");
            break;
        case WAVEID_MAC:
            p.setProperty("waveform", "mac");
            break;
        default:
            p.setProperty("waveform", "ramp");
            break;
        }
    }
    
    /**
     * Load default settings.
     * <ul>
     *  <li>Disable AGDS Support</li>
     *  <li>Disable Amiga Support</li>
     *  <li>Sets Engine Emulation version to 2.9.17</li>
     *  <li>Sets Decryption key to Sierra's standard key.</li>
     * </ul>
     */
    public void setDefaults()
    {
        Dimension d;
        
        setAGDS(false);
        setAmiga(false);
        setEngineVersion(0);
        setKey(sierraKey);
        setWaveform(WAVEID_RAMP);
        
        d = Toolkit.getDefaultToolkit().getScreenSize();
        
        if (d.width >= 640)
        {
            zoomw = 4;
            zoomh = 2;
        }
        else if (d.width < 320)
        {
            zoomw = 1;
            zoomh = 1;
        }
        else
        {
            zoomw = 2;
            zoomh = 1;
        }
    }
    
    /**
     * Transforms Wave String representation into a internal ID.
     *
     * @param  wavename String representation of a built-in wave format.
     * @return Returns ID that represent a built-in wave.
     */    
    public final static byte toWaveID(String wavename)
    {
        if (wavename.equalsIgnoreCase("square") || wavename.equalsIgnoreCase("pc"))
        {
            return WAVEID_SQUARE;
        }
        else if (wavename.equalsIgnoreCase("mac"))
        {
            return WAVEID_MAC;
        }
        else
        {
            return WAVEID_RAMP;
        }
    }
    
    /**
     * Transforms internal Wave ID into a String representation.
     *
     * @param  waveid Internal Wave ID.
     * @return Returns String representation of a wave.
     */
    public final static String fromWaveID(byte waveid)
    {
        switch (waveid)
        {
        case Context.WAVEID_SQUARE:
            return "Square";
        case Context.WAVEID_MAC:
            return "Mac";
        default:
            return "Ramp";
        }
    }
}