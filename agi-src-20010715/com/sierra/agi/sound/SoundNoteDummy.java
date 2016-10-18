/*
 * SoundNoteDummy.java
 */

package com.sierra.agi.sound;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class SoundNoteDummy extends Sound implements Runnable
{
    protected NoteMixer mix;
    protected Thread    th;
    protected boolean   noisy;
    
    public SoundNoteDummy(NoteMixer mix)
    {
        this.mix   = mix;
        this.noisy = true;
    }
    
    public void play()
    {
        th = new Thread(this, "Dummy Note Player");
        th.start();
    }
    
    public void playSync()
    {
        run();
    }
    
    public void stop()
    {
        th.interrupt();
    }
    
    public void setVolume(boolean noisy)
    {
        if (this.noisy != noisy)
        {
            this.noisy = noisy;
            raiseVolumeEvent(noisy);
        }
    }
    
    public void run()
    {
        int    dur = mix.getDuration();
        double d   = dur * 22.05;
        
        raiseStartEvent();
        try
        {
            Thread.sleep((int)d);
        }
        catch (InterruptedException ex)
        {
        }
        raiseStopEvent();
    }

    public boolean isDummy()
    {
        return true;
    }
}