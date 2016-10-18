/*
 * Logic.java
 */

package com.sierra.agi.logic;

import com.sierra.agi.*;
import com.sierra.agi.logic.interpret.*;
import java.io.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public abstract class Logic extends Object
{
    /** Creates new Logic. */
    protected Logic()
    {
    }
    
    /** Execute Logic. */
    public abstract void execute(LogicContext context) throws LogicException;
    
    /** Get a String from the Logic resource. */
    public abstract String getString(int stringNumber);
    
    /** Load Logic resource. */
    public static Logic loadLogic(Context context, InputStream stream, int size) throws IOException, LogicException
    {
        return new LogicInterpreter(context, stream, size);
    }
}