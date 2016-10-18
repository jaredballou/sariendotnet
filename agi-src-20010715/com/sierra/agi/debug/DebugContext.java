/*
 * DebugContext.java
 */

package com.sierra.agi.debug;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class DebugContext extends Object implements Cloneable
{
    /** Input Stream */
    public BufferedReader in;
    
    /** Output Stream */
    public PrintStream    out;
    
    /** Error Stream */
    public PrintStream err;
    
    /** Object */
    public Object obj;
    
    /** Interactive */
    public boolean interactive;
    
    /**
     * Creates new DebugContext based on the System object.
     */
    public DebugContext()
    {
        in  = new BufferedReader(new InputStreamReader(System.in));
        out = System.out;
        err = System.err;
        interactive = true;
    }
    
    /**
     * Creates new DebugContext identical from DebugContext passed
     * in parameters.
     */
    public DebugContext(DebugContext clone)
    {
        in  = clone.in;
        out = clone.out;
        err = clone.err;
        obj = clone.obj;
        interactive = clone.interactive;
    }
    
    public Object clone()
    {
        return new DebugContext(this);
    }
    
    /**
     * Reads a single line of text and make a array of parameter.
     *
     * @param  line Line of text.
     * @return Returns a array of parameter.
     */
    public static String[] toArgArray(String line)
    {
        StringTokenizer tokens = new StringTokenizer(line, " ");
        String          args[];
        int             i;
        
        i = tokens.countTokens();
        
        if (i == 0)
            return null;
        
        args = new String[i];
        
        i = 0;
        while (tokens.hasMoreTokens())
        {
            args[i] = tokens.nextToken();
            i++;
        }
        
        for (i = 0; i < args.length; i++)
        {
            if (args[i].startsWith("%") && args[i].endsWith("%"))
            {
                args[i] = resolve(args[i]);
            }
        }
        
        return args;
    }

    public static String resolve(String var)
    {
        String varname = var.substring(1, var.length() - 1);
        String prop    = System.getProperty(varname);
        
        if (prop == null)
        {
            return var;
        }
        
        return prop;
    }
    
    /** Parameter Types. (For Reflexive Method calling.) */    
    protected static final Class paramsTypes[] = new Class[] {DebugContext.class, String[].class};

    /**
     * This methods analyse a line of text and call the specified method with
     * the parameter from the line.
     *
     * @param line  Line of text.
     * @throws ExitPromptException The execution of the command want to stop
     *                             the prompt.
     */
    public void callMethod(String line) throws ExitPromptException
    {
        Method meth;
        Object params[] = new Object[2];
        String args[]   = toArgArray(line);

        if (args == null)
            return;
        
        params[0] = this;
        params[1] = args;
        
        try
        {
            try
            {
                meth = obj.getClass().getDeclaredMethod(args[0], paramsTypes);
                meth.invoke(obj, params);
            }
            catch (NoSuchMethodException e)
            {
                meth = getClass().getDeclaredMethod(args[0], paramsTypes);
                meth.invoke(this, params);
            }
        }
        catch (InvocationTargetException e)
        {
            if (e.getTargetException() instanceof ExitPromptException)
            {
                throw (ExitPromptException)e.getTargetException();
            }
            else
            {
                e.getTargetException().printStackTrace(err);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace(err);
        }
    }

    /**
     * Reads all the lines from a Reader instance and calls the appropriate
     * method with the appropriate parameter specified in the Reader.
     *
     * @param prompt  Prompt to show on-screen. (null if reader is file-based.)
     */
    public void execute(String prompt)
    {
        String line;

        try
        {
            while (true)
            {
                try
                {
                    if (interactive)
                    {
                        if (prompt != null)
                        {
                            out.print(prompt);
                        }
                        
                        out.print("$ ");
                        out.flush();
                    }

                    line = in.readLine();
                    
                    if (line == null)
                    {
                        break;
                    }
                    
                    callMethod(line);
                }
                catch (IOException e)
                {
                }
            }
        }
        catch (ExitPromptException e)
        {
        }
    }

    /**
     * Shows Memory Statistics.
     *
     * @param context Context.
     * @param args    Arguments.
     */
    public void mem(DebugContext context, String[] args)
    {
        Runtime run;
        long    free;
        long    total;

        if (isHelp(args))
        {
            out.println("Prints Memory Statistics.");
            out.println();
            out.println("Syntax:");
            out.println("  mem");
            return;
        }
        
        run   = Runtime.getRuntime();
        free  = run.freeMemory();
        total = run.totalMemory();
        out.print("Free Memory:  ");
        out.print(free);
        out.print(" (");
        out.print((free * 100) / total);
        out.println("%)");
        out.print("Total Memory: ");
        out.println(total);
    }

    /**
     * Force the execution of the Java's Garbage Collector.
     *
     * @param context Context.
     * @param args    Arguments.
     */
    public void gc(DebugContext context, String[] args)
    {
        if (isHelp(args))
        {
            out.println("Force the execution of the Java's Garbage Collector.");
            out.println();
            out.println("Syntax:");
            out.println("  gc");
            return;
        }

        out.println("Running Garbage Collector...");
        System.runFinalization();
        System.gc();
    }
    
    /**
     * Exit the current application.
     *
     * @param context Context.
     * @param args    Arguments.
     */
    public void exit(DebugContext context, String args[])
    {
        if (isHelp(args))
        {
            out.println("Exit the current application.");
            out.println();
            out.println("Syntax:");
            out.println("  exit");
            return;
        }
        
        System.exit(0);
    }
    
    public void run(DebugContext context, String args[]) throws IOException
    {
        DebugContext newContext;
        
        if (isHelp(args) || (args.length == 1))
        {
            out.println("Run a \"MS-DOS Batch-like\" script.");
            out.println();
            out.println("Syntax:");
            out.println("  run file");
            return;
        }
        
        newContext = new DebugContext(context);
        newContext.in = new BufferedReader(new FileReader(new File(args[1])));
        newContext.interactive = false;
        
        newContext.execute(null);
    }

    public void echo(DebugContext context, String args[])
    {
        int i;
        
        for (i = 1; i < args.length; i++)
        {
            out.print(args[i]);
            out.print(" ");
        }
        
        out.println();
    }

    public void ts(DebugContext context, String args[])
    {
        Thread thr[] = new Thread[64];
        int    thrn, i;
        
        thrn = Thread.enumerate(thr);
        
        for (i = 0; i < thrn; i++)
        {
            out.print(i);
            out.print(" ");
            out.print(thr[i].getName());
            
            if (thr[i].isAlive())
            {
                out.print(" [Alive]");
            }

            if (thr[i].isDaemon())
            {
                out.print(" [Daemon]");
            }
            
            out.print(" [Priority=");
            out.print(thr[i].getPriority());
            out.print("]");
            
            out.println();
        }
    }
    
    public static final boolean isHelp(String[] args)
    {
        int i;
        
        if (args == null)
            return false;
        
        for (i = 1; i < args.length; i++)
        {
            if (args[i].equalsIgnoreCase("-h") ||
                args[i].equalsIgnoreCase("-?") ||
                args[i].equalsIgnoreCase("-help"))
            {
                return true;
            }
        }
        
        return false;
    }
}