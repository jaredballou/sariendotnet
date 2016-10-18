/*
 * NoteChannel.java
 */

package com.sierra.agi.sound;

/**
 * AGI Channel. Contains variables
 * specific to a note channel.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class NoteChannel extends Object
{
    /** Channel ID */
    public int channel;
    
    /** Note Remaining Duration */
    public int dur;
    
    /** End Flag */
    public boolean end;
    
    /** Env persistant variable */
    public int env;
    
    /** Flags */
    public short flags;
    
    /** Note Frequency */
    public int freq;
    
    /** Note Phase Status */
    public int phase;
    
    /** Note Reader */
    public NoteReader reader;
    
    /** Note Volume */
    public int vol;
    
    /** Loop Flag */
    public final static short FLAG_LOOP = 1;
    
    /**
     * Creates a new AgiNoteChannel
     *
     * @param reader  <CODE>AgiNoteReader</CODE> object to reads notes.
     * @param channel Channel ID
     */
    public NoteChannel(NoteReader reader, int channel)
    {
        this.channel = channel;
        this.reader  = reader;

        dur   = 0;
        end   = false;
        freq  = 0;
        vol   = 0;
        flags = FLAG_LOOP;
    }
    
    /**
     * Do a note cycle. Decrements the duration. When
     * the duration is zero, it loads another note.
     */
    public void cycle()
    {
        if (end)
            return;
        
        dur--;
        
        if (dur <= 0)
        {
            Note note = reader.nextNote(channel);
            
            if (note == null)
            {
                end = true;
                vol = 0;
            }
            else
            {
                dur  = note.dur;
                freq = note.freq;
            
                if (freq != 0)
                {
                    vol   = (note.vol == 0xf? 0: 0xff - (note.vol << 1)); 
                    env   = 0x10000;
                    phase = 0;
                }
            }
        }
    }
}