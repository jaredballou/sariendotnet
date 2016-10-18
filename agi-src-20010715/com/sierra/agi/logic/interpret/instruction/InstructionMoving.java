/*
 * InstructionMoving.java
 */

package com.sierra.agi.logic.interpret.instruction;

/**
 * Base Class for Instruction that have the ability to control the logics flows.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public abstract class InstructionMoving extends Instruction
{
    /**
     * Creates new Moving Instruction. Does absolutly nothing. Serve only has a
     * formal declaration.
     */
    protected InstructionMoving()
    {
    }
    
    /**
     * Retreive the Address which is referenced by this instruction.
     *
     * @return Returns the Address referenced by this instruction.
     */
    public abstract int getAddress();
}