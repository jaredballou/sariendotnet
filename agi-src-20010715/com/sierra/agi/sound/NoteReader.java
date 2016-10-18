/*
 * NoteReader.java
 */

package com.sierra.agi.sound;
import  java.io.*;
import  java.util.*;

/**
 * Reads AGI Notes structures from any specified input
 * stream. Once constructor has finished, the InputStream is
 * closed because it is no longer needed. All notes are
 * transferred to memory and remain in memory until the
 * AgiNoteReader is finalized.
 *
 * @see com.sierra.agi.sound.Note
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class NoteReader
{
    /** The channel data. */
    protected Vector channels[] = new Vector[4];
    
    /** The channel position. */
    protected int channelx[] = new int[4];
    
    /** The channels length. */
    protected int length;
    
    /**
     * Creates a new AgiNoteReader. When the constructor
     * finishs, the input stream specified should be discarted.
     * (It calls it's close method)
     *
     * @param inputStream The stream to read.
     */
    public NoteReader(InputStream inputStream)
    {
        Note            note;
        DataInputStream dataStream = new DataInputStream(inputStream);
	int             durLo, durHi, freq0, freq1, vol, channel = 0;
        int             length = 0;
        
        channels[0] = new Vector();
        channelx[0] = 0;
        channels[1] = new Vector();
        channelx[1] = 0;
        channels[2] = new Vector();
        channelx[2] = 0;
        channels[3] = new Vector();
        channelx[3] = 0;
        
        try
        {
            while (true)
            {
                durLo = dataStream.readUnsignedByte();
                durHi = dataStream.readUnsignedByte();
                
                if ((durLo == 255) && (durHi == 255))
                {
                    /* End of channel detected */
                    if (length > this.length)
                    {
                        /* Find the longest channel */
                        this.length = length;
                    }
                    
                    length = 0;
                    channel++;
                    
                    if (channel == 4)
                        break;
                    
                    continue;
                }
                
                freq0 = dataStream.readUnsignedByte();
                freq1 = dataStream.readUnsignedByte();
                vol   = dataStream.readUnsignedByte();
                
                /* Once read, creates a new AgiNote structure 
                 * containing pre-analyzed sound data. */
                note      = new Note();
		note.dur  = (durHi << 8) | durLo;
		note.freq = ((freq0 & 0x3f) << 4) | (freq1 & 0x0f);
                note.vol  = (short)(vol & 0xf);
                
                length += note.dur;
                channels[channel].add(note);
            }
        }
        catch (IOException ex)
        {
        }
        finally
        {
            /* Close the stream. */
            try
            {
                inputStream.close();
            }
            catch (IOException ex)
            {
            }
        }
    }
    
    /**
     * Determine if the specified channel has
     * more notes in his buffer.
     *
     * @param channel Channel ID
     * @return Returns <CODE>true</CODE> if the channel has more notes.
     */
    public boolean hasMoreNotes(int channel)
    {
        if (channels[channel].size() > channelx[channel])
            return true;
        
        return false;
    }

    /**
     * Determine if one of the channels has more
     * notes in their buffer.
     *
     * @return Returns <CODE>true</CODE> if a channel has more notes.
     */
    public boolean hasMoreNotes()
    {
        int i;

        for (i = 0; i < channels.length; i++)
        {
            if (channels[i].size() > channelx[i])
                return true;
        }

        return false;
    }
    
    /**
     * Retreive the next note of the buffer.
     *
     * @param channel Channel ID
     * @return Returns a <CODE>AgiNote</CODE> structure or <CODE>null</CODE>
     *         if no more notes are available.
     */
    public Note nextNote(int channel)
    {
        Note pNote;
        
        if (channels[channel].size() <= channelx[channel])
        {
            return null;
        }
        
        pNote = (Note)(channels[channel].elementAt(channelx[channel]));
        channelx[channel]++;
        
        return pNote;
    }
    
    /**
     * Resets all the channels.
     */    
    public void reset()
    {
        int i;
        
        for (i = 0; i < channelx.length; i++)
        {
            channelx[i] = 0;
        }
    }
    
    /**
     * Resets the specified channel.
     *
     * @param channel Channel ID
     */
    public void reset(int channel)
    {
        channelx[channel] = 0;
    }
    
    /**
     * Find the numbers of channel available. (Always four
     * in the current implentation, but here because it could
     * be changed in future releases.)
     *
     * @return Returns the channel count.
     */    
    public int getChannelCount()
    {
        return channels.length;
    }
    
    /**
     * Find the length of all the notes of the longest
     * channel.
     *
     * @return Returns the length of all the notes.
     */    
    public int getDuration()
    {
        return length;
    }
}