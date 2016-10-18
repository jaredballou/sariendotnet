/*
 * LogicDecompiler.java
 */

package com.sierra.agi.tools;

import com.sierra.agi.Context;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import com.sierra.agi.logic.interpret.instruction.*;
import java.io.*;
import java.util.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class LogicDecompiler extends Object implements Runnable
{
    protected LogicInterpreter logic;
    
    /** Creates new Logic Decompiler */
    public LogicDecompiler(LogicInterpreter logic)
    {
        this.logic = logic;
    }

    public LogicDecompiler(Context context, InputStream stream, int size) throws IOException, LogicException
    {
        this.logic = new LogicInterpreter(context, stream, size);
    }
    
    public static void main(String args[]) throws IOException, LogicException
    {
        Context         context = new Context();
        File            file    = new File(args[0]);
        LogicDecompiler decompiler;
        
        decompiler = new LogicDecompiler(context, new FileInputStream(file), (int)file.length());
        decompiler.run();
    }
    
    public void run()
    {
        PrintWriter writer = new PrintWriter(System.out);
        
        logic.dump(writer);
        writer.flush();
        writer.close();
    }
}