/*
 * NoteSequencer.java
 */

package com.sierra.agi.sound;

/**
 * This class reads notes from a AgiNoteReader
 * object. It keeps tracks of when notes are
 * pressed and released.
 *
 * @see     com.sierra.agi.sound.NoteMixer
 * @see     com.sierra.agi.sound.NoteReader
 * @see     com.sierra.agi.sound.NoteChannel
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class NoteSequencer extends Object
{
    protected NoteChannel channels[];
    protected int         pos;
    
    /** Creates new AgiNoteSequencer */
    public NoteSequencer(NoteReader reader)
    {
        int i, j;
        
        j        = reader.getChannelCount();
        channels = new NoteChannel[j];
        pos      = 0;
        
        for (i = 0; i < j; i++)
        {
            channels[i] = new NoteChannel(reader, i);
        }
    }
    
    public void cycle()
    {
        int i;
        
        for (i = 0; i < channels.length; i++)
        {
            channels[i].cycle();
        }
        
        pos++;
    }

    public boolean hasMoreCycle()
    {
        int i;
        
        for (i = 0; i < channels.length; i++)
        {
            if (!channels[i].end)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public NoteChannel getChannel(int channel)
    {
        return channels[channel];
    }
    
    public NoteReader getReader()
    {
        return channels[0].reader;
    }
    
    public int getChannelCount()
    {
        return channels.length;
    }
    
    public int getDuration()
    {
        return channels[0].reader.getDuration();
    }
    
    public int getPosition()
    {
        return pos;
    }

    public void setPosition(int newPos)
    {
        int i, j;
        
        channels[0].reader.reset();
        
        for (j = 0; j < newPos; i++)
        {
            for (i = 0; i < channels.length; i++)
            {
                channels[i].cycle();
            }
        }
        
        pos = newPos;
    }
    
    public void reset()
    {
        channels[0].reader.reset();
    }
}