/*
 * InstructionPlaySound.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import com.sierra.agi.sound.*;
import java.io.*;

/**
 * Play Sound Instruction.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionPlaySound extends InstructionBi
{
    /** 
     * Creates new Play Sound Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionPlaySound(Context context, InputStream stream, LogicReader reader, short bytecode) throws IOException
    {
        super(context, stream, reader, bytecode);
    }
    
    /**
     * Execute the Instruction.
     *
     * @param logic         Logic used to execute the instruction.
     * @param logicContext  Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext)
    {
        Sound sound;
        
        /* Stop the currently playing sound if any. */
        if (logicContext.currentSound != null)
        {
            logicContext.currentSound.stop();
        
            /* Wait until the Sound resource has cleaned all data. */
            while (logicContext.currentSound != null)
            {
                Thread.yield();
            }
        }
        
        /* Play the sound! */
        sound = logicContext.context.cache.getSound(p1);
        logicContext.currentSound = sound;
        sound.addSoundListener(new FlagUpdater(logicContext));
        sound.play();
        
        return 3;
    }

    public class FlagUpdater extends Object implements SoundListener
    {
        LogicContext logicContext;
        
        public FlagUpdater(LogicContext logicContext)
        {
            this.logicContext = logicContext;
        }
        
        /** Called when the <CODE>Sound</CODE> resource starts playing. */
        public void soundStarted(Sound sound)
        {
        }
        
        /** Called when the <CODE>Sound</CODE> resource has stopped (either ended, or stopped voluntary). */
        public void soundStopped(Sound sound)
        {
            logicContext.setFlag(p2, true);
            logicContext.currentSound = null;
        }
        
        /** Called when the <CODE>Sound</CODE> volume has been modified.  */
        public void soundVolumeChanged(Sound sound, boolean noisy)
        {
        }
    }
    
//#ifdef DEBUG
    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames()
    {
        return new String[] {"play.sound", Integer.toString(p1)};
    }
//#endif DEBUG
}