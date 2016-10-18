/*
 * NoteMixer.java
 */

package com.sierra.agi.sound;
import  java.io.*;
import  java.util.*;

/**
 * The AGI Note mixer. Uses a Note Sequencer to
 * produce streaming wave of the AGI 4-channel
 * sound tracks.
 *
 * @see     com.sierra.agi.sound.NoteSequencer
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class NoteMixer extends Object
{
    /** The waveform buffer. */
    protected short buffer[];
    
    /** The flags. */
    protected short flags;
    
    /** The sequence position. */
    protected short position = 0;
    
    /** The AGI Note sequencer. */
    protected NoteSequencer seq;
    
    /** The Waveform to use. */
    protected short waveform[];
    
    /** Decay values. */
    protected final static short ENV_DECAY = 800;
    
    /** Sustain values. */
    protected final static short ENV_SUSTAIN = 160;
    
    /** Enveloppe Flags. (for the <CODE>flags</CODE> field) */
    public final static short FLAG_ENVELOPPE = 1;

    /** Interpolation Flags. (for the <CODE>flags</CODE> field) */
    public final static short FLAG_INTERPOLATION = 2;
    
    /**
     * Creates a new AgiNoteMixer.
     *
     * @param seq AGI Note Sequencer to use.
     */
    public NoteMixer(NoteSequencer seq, byte waveid)
    {
        this.seq      = seq;
        this.flags    = FLAG_INTERPOLATION | FLAG_ENVELOPPE;
        
        setWaveform(waveid);
    }
    
    /**
     * Mixs the AGI Note from the sequencer. Results
     * are stored in the <CODE>buffer</CODE> field.
     */
    protected void mix()
    {
        NoteChannel channel;
        int         i, j, phase, vol;
        int         b, n = 0, channelCount;
        
        seq.cycle();
        if (buffer == null)
        {
            buffer = new short[410];
        }
        else
        {
            Arrays.fill(buffer, (short)0);
        }
        
        channelCount = seq.getChannelCount();
        for (i = 0; i < channelCount; i++)
        {
            channel = seq.getChannel(i);
            
            if (channel.end)
                continue;
            
            n++;     
            
            if (channel.vol == 0)
                continue;
            
            phase = channel.phase;
            vol   = ((flags & FLAG_ENVELOPPE) == FLAG_ENVELOPPE)? channel.vol * channel.env >> 16: channel.vol;
            
            for (j = 0; j < 410; j++)
            {
                b = waveform[phase >> 8];
                
                if ((flags & FLAG_INTERPOLATION) == FLAG_INTERPOLATION)
                {
                    b += ((waveform[((phase >> 8) + 1) % waveform.length] - waveform[phase >> 8]) * (phase & 0xff)) >> 8;
                }
                
                buffer[j] += (b * vol) >> 8;
                phase     += 11860 * 4 / channel.freq;
                
                if ((channel.flags & NoteChannel.FLAG_LOOP) == NoteChannel.FLAG_LOOP)
                {
                    phase %= waveform.length << 8;
                }
                else
                {
                    if (phase >= waveform.length << 8)
                    {
                        phase       = 0;
                        channel.vol = 0;
                        channel.end = true;
                        break;
                    }
                }
            }
            
            if (channel.env > channel.vol * ENV_SUSTAIN)
                    channel.env -= ENV_DECAY;

            channel.phase = phase;
        }

        if (n == 0)
        {
            buffer = null;
            return;
        }
        
	for (i = 0; i < buffer.length; i++)
	    buffer[i] <<= 5;
    }

    /**
     * Generate the next sample.
     *
     * @param  b The buffer to store the result.
     * @return Returns <CODE>true</CODE> if result has been stored in the
     *         <CODE>b</CODE> parameter.
     */
    public synchronized boolean nextMix(byte[] b)
    {
        int i, j;
        
        mix();

        if (buffer == null)
            return false;
        
        for (i = 0, j = 0; i < 410; i++, j++)
        {
            b[j] = (byte)(buffer[i]  & 0xFF); j++;
            b[j] = (byte)((buffer[i] & 0xFF00) >> 8);
        }
        
        return true;
    }

    /**
     * Retreive the current position.
     *
     * @return Returns the current position.
     */    
    public int getPosition()
    {
        return seq.getPosition();
    }
    
    /**
     * Change the current position.
     *
     * @param newPos The new position.
     */    
    public synchronized void setPosition(int newPos)
    {
        seq.setPosition(newPos);
    }
    
    /**
     * Reset the Mixer.
     */
    public synchronized void reset()
    {
        seq.reset();
    }
    
    /**
     * Obtain the length of the audio clip in sample.
     *
     * @return The length audio clip.
     */
    public int getDuration()
    {
        return seq.getDuration();
    }
    
    /**
     * Retreive the <CODE>AgiNoteReader</CODE> used
     * to read the audio clip data.
     *
     * @see    com.sierra.agi.sound.NoteReader
     * @return Returns the <CODE>NoteReader</CODE> used.
     */
    public NoteReader getReader()
    {
        return seq.getReader();
    }
    
    /**
     * Retreive the <CODE>AgiNoteSequencer</CODE> used
     * to sequence the audio clip data.
     *
     * @see    com.sierra.agi.sound.NoteSequencer
     * @return Returns the <CODE>NoteSequencer</CODE> used.
     */
    public NoteSequencer getSequencer()
    {
        return seq;
    }
    
    public void setWaveform(byte waveid)
    {
        switch (waveid)
        {
        case 0:
        default:
            waveform = Note.waveformRamp;
            break;
        case 1:
            waveform = Note.waveformSquare;
            break;
        case 2:
            waveform = Note.waveformMac;
            break;
        }
    }
}