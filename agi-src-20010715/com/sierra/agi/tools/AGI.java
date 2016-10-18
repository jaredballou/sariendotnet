/*
 * AGI.java
 */

package com.sierra.agi.tools;

import com.sierra.agi.*;
import com.sierra.agi.awt.*;
import com.sierra.agi.debug.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.res.*;
import com.sierra.agi.res.v2.*;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Adventure Game Interpreter. This is the main
 * class that starts the AGI Engine.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class AGI extends Object implements Runnable
{
    /** Game Context. */
    public Context context;

    /** Logic Game Context. */
    public LogicContext logicContext;
    
    /**
     * Initialize the Game Engine.
     *
     * @param args Arguments. (Optionnal, args.length may be equal to 0)
     */
    public AGI(String args[]) throws Exception
    {
        ResourceProvider provider = null;
        File             file     = null;
        InputStream      stream   = null;
        
        context = new Context();
        
        if (args.length == 0)
        {
            file = (new File("")).getAbsoluteFile();
        }
        else
        {
            file = (new File(args[0])).getAbsoluteFile();
        }

        if (!file.exists())
        {
            throw new FileNotFoundException();
        }
        
        if (file.isDirectory())
        {
            try
            {
                provider = new ResourceProviderFS(context, file);
            }
            catch (ResourceException e)
            {
                try
                {
                    provider = new com.sierra.agi.res.v3.ResourceProviderFS(context, file);
                }
                catch (ResourceException e2)
                {
                    file = new File(file, "agi.conf");
                }
            }
        }
        
        if (provider == null)
        {
            String p = file.getPath();

            if (p.endsWith(".conf"))
            {
                file = loadConfigFile(file, context);
                p    = file.getPath();
            }

            if (p.endsWith(".zip"))
            {
                provider = new ResourceProviderZip(context, file);
            }
            else
            {
                try
                {
                    provider = new ResourceProviderFS(context, file);
                }
                catch (ResourceException rex)
                {
                    provider = new com.sierra.agi.res.v3.ResourceProviderFS(context, file);
                }
            }
        }
        
        context.cache = new ResourceCache(context, provider);
        logicContext  = new LogicContext(context);
    }
    
    /**
     * @param context Initialize with a premade Game Context.
     */
    public AGI(Context context) throws Exception
    {
        this.context      = context;
        this.logicContext = new LogicContext(context);
    }

    protected File loadConfigFile(File configFile, Context context) throws IOException
    {
        Properties prop = new Properties();
        File       file;
        
        prop.load(new FileInputStream(configFile));
        context.loadConfig(prop);
        
        file = new File(prop.getProperty("respath", "."));
        return file.getAbsoluteFile();
    }
    
    /**
     * The main entry point of the AGI Engine.
     *
     * @param args Command line parameters
     */    
    public static void main(String args[])
    {
        AGI agi;
        
        try
        {
            agi = new AGI(args);
            agi.run();
        }
        catch (Throwable t)
        {
            (new ExceptionDialog(new java.awt.Frame(), true, t)).setVisible(true);
        }

        System.exit(0);
    }
    
    public void run()
    {
        try
        {
            MainWindow   mainwindow   = new MainWindow();
            MainScreen   screen       = mainwindow.screen;
            Logic        logic        = null;
            
            long m, o;
            int  fps = 10;
            int  d   = 1000 / fps;

            mainwindow.setVisible(true);
            
            /* Initialization */
            context.cache.loadLogic((short)0);
            logic = context.cache.getLogic((short)0);
            logicContext.addPendingJob(new AGI.FirstTimeRun());
            
            System.gc();
            System.runFinalization();
            Thread.yield();
            
            m = System.currentTimeMillis();
            while (true)
            {
                try
                {
                    logic.execute(logicContext);
                    logicContext.viewTable.tick();
                }
                catch (LogicExitAll lea)
                {
                }
                
                logicContext.runPendingJobs();
                //screen.regenerate();

                o  = d;
                o -= (System.currentTimeMillis() - m);
                m += d;

                if (o > 0)
                {
                    try
                    {
                        Thread.sleep(o);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
        }
        catch (Throwable t)
        {
            (new ExceptionDialog(new java.awt.Frame(), true, t)).setVisible(true);
        }
    }
    
    public class FirstTimeRun extends Object implements Runnable
    {
        public void run()
        {
            logicContext.setFlag(LogicContext.FLAG_LOGIC_ZERO_FIRSTTIME, false);
        }
    }
}