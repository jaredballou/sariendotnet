/*
 * SoundNote.java
 */

package com.sierra.agi.sound;
import  javax.sound.sampled.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class SoundNote extends Sound implements Runnable
{
    protected NoteMixer      mix;
    protected Thread         th;
    protected boolean        playing;
    protected boolean        noisy;
    protected SourceDataLine clip;
    
    /** Creates a new SoundNote */
    public SoundNote(NoteMixer mix)
    {
        this.mix   = mix;
        this.noisy = true;
    }
    
    public synchronized void play()
    {
        th = new Thread(this, "Note Player");
        th.start();
    }
    
    public void playSync()
    {
        run();
    }
    
    public synchronized void stop()
    {
        if (clip != null)
        {
            playing = false;
        }
    }

    public synchronized void setVolume(boolean noisy)
    {
        if (this.noisy != noisy)
        {
            this.noisy = noisy;
            raiseVolumeEvent(noisy);
        }
        
        try
        {
            if (clip != null)
            {
                FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(noisy? (float)0: (float)-80);
            }
        }
        catch (Throwable t)
        {
        }
    }

    public void run()
    {
        byte[]      b      = new byte[820];
        AudioFormat format = new AudioFormat(22050, 16, 1, true, false);
        
        try
        {
            clip = (SourceDataLine)AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }
        
        raiseStartEvent();
        if (!mix.nextMix(b))
        {
            raiseStopEvent();
            return;
        }
        
        try
        {
            clip.open(format);
            clip.write(b, 0, 820);
            
            if (!noisy)
            {
                setVolume(false);
            }
            
            clip.start();
            
            playing = true;
            while (playing)
            {
                if (!mix.nextMix(b))
                    break;

                clip.write(b, 0, 820);
            }
            playing = false;

            clip.drain();
            clip.stop();
            clip.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        synchronized (this)
        {
            clip = null;
        }

        raiseStopEvent();
    }
}