/*
 * SoundListener.java
 */

package com.sierra.agi.sound;

/**
 * Interface used by <CODE>Sound</CODE> derivated classes
 * to be notified of specific events.
 *
 * @see     com.sierra.agi.sound.Sound
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public interface SoundListener
{
    /** Called when the <CODE>Sound</CODE> starts playing. */
    public void soundStarted(Sound sound);

    /** Called when the <CODE>Sound</CODE> has stopped. */
    public void soundStopped(Sound sound);

    /** Called when the <CODE>Sound</CODE> volume has been modified. */
    public void soundVolumeChanged(Sound sound, boolean noisy);
}