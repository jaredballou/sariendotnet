/*
 * Sound.java
 */

package com.sierra.agi.sound;

import  java.io.*;
import  java.util.Enumeration;
import  java.util.Vector;
import  com.sierra.agi.Context;

/**
 * This class is the base class of any AGI Sound
 * Clip. It contains code to detect the presence
 * of compatible sound devices. The most important
 * method is the "loadSound" method. It can be used
 * to detect the kind of Sound Clip and load the
 * appropriate Interpretor and Player for that specific
 * kind.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public abstract class Sound extends Object
{
    /** Sample Audio Clip. */
    protected static final byte TYPE_SAMPLE = 1;
    
    /** MIDI Audio Clip. */
    protected static final byte TYPE_MIDI = 2;
    
    /** AGI 4-Channel Audio Clip. */
    protected static final byte TYPE_4CHANNEL = 8;
    
    /** Probed Status. */
    protected static boolean probed = false;
    
    /** Audio Support Status. */
    protected static boolean audioSupport = true;

    /** Listeners */
    protected java.util.Vector listeners = new java.util.Vector();
    
    /** Creates a new Sound */
    protected Sound()
    {
    }
    
    /**
     * Used to detect the presence of the <CODE>javax.sound.sampled</CODE>
     * package.
     */
    protected static void init()
    {
        try
        {
            Class audioFormat  = Class.forName("javax.sound.sampled.AudioFormat");
            Class audioSystem  = Class.forName("javax.sound.sampled.AudioSystem");
            Class dataLineInfo = Class.forName("javax.sound.sampled.DataLine");
        }
        catch (ClassNotFoundException e)
        {
            /* Java 1.2 or Java 1.3 without Audio Support. */
            audioSupport = false;
        }
        finally
        {
            probed = true;
        }
    }
    
    public static void disableSound()
    {
        probed       = true;
        audioSupport = false;
    }

    public static Sound loadSound(com.sierra.agi.Context context, InputStream inputStream) throws IOException
    {
        int type;
     
        if (!probed)
            init();
        
        type = inputStream.read();

        switch (type)
        {
        case TYPE_4CHANNEL:
            {
                inputStream.skip(7);

                NoteMixer mix = new NoteMixer(new NoteSequencer(new NoteReader(inputStream)), context.getWaveform());

                try
                {
                    if (!audioSupport)
                        throw new Exception();

                    return new SoundNote(mix);
                }
                catch (Exception ex)
                {
                    return new SoundNoteDummy(mix);
                }
            }
        }
        
        return null;
    }

    public void play()
    {
        throw new UnsupportedOperationException();
    }

    public void playSync()
    {
        throw new UnsupportedOperationException();
    }

    public void stop()
    {
        throw new UnsupportedOperationException();
    }
    
    public void setVolume(boolean bMute)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if the current instance of the object
     * is a dummy. A dummy is a version of the <CODE>Sound</CODE>
     * that interprets sound data but does not play them,
     * but waits the exact durations of the audio clip.
     * (For computers without audio devices support)
     *
     * @return Returns <CODE>true</CODE> if this instance
     *         of <CODE>Sound</CODE> is a dummy.
     */
    public boolean isDummy()
    {
        return false;
    }
 
    public void addSoundListener(SoundListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeSoundListener(SoundListener listener)
    {
        listeners.remove(listener);
    }
    
    protected void raiseStartEvent()
    {
        Enumeration   enum = listeners.elements();
        SoundListener listen;
        
        while (enum.hasMoreElements())
        {
            listen = (SoundListener)enum.nextElement();
            
            try
            {
                listen.soundStarted(this);
            }
            catch (Throwable th)
            {
                th.printStackTrace();
            }
        }
    }

    protected void raiseStopEvent()
    {
        Enumeration   enum = listeners.elements();
        SoundListener listen;
        
        while (enum.hasMoreElements())
        {
            listen = (SoundListener)enum.nextElement();
            
            try
            {
                listen.soundStopped(this);
            }
            catch (Throwable th)
            {
                th.printStackTrace();
            }
        }
    }
    
    protected void raiseVolumeEvent(boolean noisy)
    {
        Enumeration   enum = listeners.elements();
        SoundListener listen;
        
        while (enum.hasMoreElements())
        {
            listen = (SoundListener)enum.nextElement();
            
            try
            {
                listen.soundVolumeChanged(this, noisy);
            }
            catch (Throwable th)
            {
                th.printStackTrace();
            }
        }
    }
}