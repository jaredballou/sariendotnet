/*
 * AGId.java
 */

package com.sierra.agi.tools;

import java.io.*;
import com.sierra.agi.*;
import com.sierra.agi.debug.*;
import com.sierra.agi.res.*;
import com.sierra.agi.res.v2.*;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.pic.PictureException;
import com.sierra.agi.view.ViewException;

/**
 * Adventure Game Interpreter with Run-time debugging. This is the main
 * class that starts the AGI Engine.
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class AGId extends Object implements Runnable
{
    /** AGI Game. */
    protected AGI agi;
    
    /** Debugging Context. */
    protected DebugContext context;
    
    /**
     * Creates new AGI with Run-time debugging.
     *
     * @param args Arguments. Passed without modification to the AGI class
     *             initialized.
     */
    public AGId(String args[])
    {
        context = new DebugContext();
        
        if (args.length != 0)
        {
            if (args[0].equalsIgnoreCase("-manual"))
            {
                return;
            }
        }
        
        try
        {
            agi = new AGI(args);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     * Main entry point.
     *
     * @param args The command line arguments
     */
    public static void main(String args[])
    {
        System.out.println();
        System.out.println("Adventure Game Interpreter v0.00.01.01");
        System.out.println("Java Edition with Run-Time Debugger");
        System.out.println();
        
        (new AGId(args)).run();
    }
    
    /**
     * Run the Command Line Interpreter.
     */
    public void run()
    {
        System.gc();
        System.runFinalization();
        
        if (agi == null)
        {
            context.obj = new ManualInit();
            context.execute("manual");
        }
        
        context.obj = this;
        context.execute(null);
    }

    public void load(DebugContext dcontext, String args[]) throws IOException, ResourceException, ViewException, LogicException, PictureException
    {
        short resNumber = Short.parseShort(args[2]);

        switch (getType(args[1]))
        {
        case ResourceProvider.TYPE_PICTURE:
            agi.context.cache.loadPicture(resNumber);
            break;
        case ResourceProvider.TYPE_SOUND:
            agi.context.cache.loadSound(resNumber);
            break;
        case ResourceProvider.TYPE_VIEW:
            agi.context.cache.loadView(resNumber);
            break;
        case ResourceProvider.TYPE_LOGIC:
            agi.context.cache.loadLogic(resNumber);
            break;
        }
    }

    public void unload(DebugContext dcontext, String args[]) throws IOException, ResourceException, ViewException
    {
        short resNumber = Short.parseShort(args[2]);
        
        switch (getType(args[1]))
        {
        case ResourceProvider.TYPE_PICTURE:
            agi.context.cache.unloadPicture(resNumber);
            break;
        case ResourceProvider.TYPE_SOUND:
            agi.context.cache.unloadSound(resNumber);
            break;
        case ResourceProvider.TYPE_VIEW:
            agi.context.cache.unloadView(resNumber);
            break;
        }
    }

    public void go(DebugContext dcontext, String[] args)
    {
        Thread t = new Thread(agi, "main-agi");
        t.start();
    }
    
    public void dump(DebugContext dcontext, String[] args) throws LogicException
    {
        short resNumber = Short.parseShort(args[2]);
        
        switch (getType(args[1]))
        {
        case ResourceProvider.TYPE_LOGIC:
            {
                Logic    logic = agi.context.cache.getLogic(resNumber);
                Dumpable dmp;
                
                if (logic instanceof Dumpable)
                {
                    dmp = (Dumpable)logic;
                    dmp.dump(new PrintWriter(dcontext.out, true));
                    dcontext.out.println();
                }
                else
                {
                    dcontext.out.println("Error: Does not support dumping.");
                }
            }
            break;
        }
    }
 
    public void dumpbin(DebugContext dcontext, String[] args) throws LogicException, ResourceException, IOException
    {
        short        resNumber = Short.parseShort(args[2]);
        File         file      = new File(args[3]);
        byte         b[]       = new byte[64];
        InputStream  in        = null;
        OutputStream out       = null;
        int          l;
        
        in  = agi.context.cache.getProvider().open(getType(args[1]), resNumber);
        out = new FileOutputStream(file);
        while (true)
        {
            l = in.read(b, 0, 64);
            
            if (l < 0)
            {
                break;
            }
            
            out.write(b, 0, l);
        }
        
        in.close();
        out.close();
    }
    
    public void show(DebugContext dcontext, String[] args) throws Exception
    {
        short resNumber = Short.parseShort(args[2]);
        
        switch (getType(args[1]))
        {
        case ResourceProvider.TYPE_PICTURE:
            (new PictureViewer(agi.context.cache.getPicture(resNumber), resNumber)).setVisible(true);
            break;
        case ResourceProvider.TYPE_VIEW:
            (new ViewViewer(agi.context, agi.context.cache.getView(resNumber), resNumber)).setVisible(true);
            break;
        case ResourceProvider.TYPE_WORD:
            (new WordViewer(agi.context, agi.context.cache.getWords())).setVisible(true);
            break;
        }
    }
    
    public byte getType(String type)
    {
        if (type.equalsIgnoreCase("view"))
        {
            return ResourceProvider.TYPE_VIEW;
        }
        else if (type.equalsIgnoreCase("sound"))
        {
            return ResourceProvider.TYPE_SOUND;
        }
        else if (type.equalsIgnoreCase("picture"))
        {
            return ResourceProvider.TYPE_PICTURE;
        }
        else if (type.equalsIgnoreCase("logic"))
        {
            return ResourceProvider.TYPE_LOGIC;
        }
        else if (type.equalsIgnoreCase("word"))
        {
            return ResourceProvider.TYPE_WORD;
        }
        
        return (byte)-1;
    }
    
    public class ManualInit extends Object
    {
        protected Context          context = new Context();
        protected ResourceProvider provider;
        
        public void init(DebugContext dcontext, String args[]) throws ExitPromptException, Exception
        {
            if (provider == null)
            {
                dcontext.err.println("Resources not loaded. Use the \"loadres\" function.");
                return;
            }
            
            context.cache = new ResourceCache(context, provider);
            agi           = new AGI(context);
            throw new ExitPromptException();
        }
        
        public void loadconfig(DebugContext dcontext, String args[]) throws IOException
        {
            if (DebugContext.isHelp(args) || args.length <= 1)
            {
                dcontext.out.println("Load a configuration file");
                dcontext.out.println();
                dcontext.out.println("Syntax:");
                dcontext.out.println("  loadconfig (file)");
                return;
            }
            
            context.loadConfig(new FileInputStream(new File(args[1])));
        }
        
        public void saveconfig(DebugContext dcontext, String args[]) throws IOException
        {
            if (DebugContext.isHelp(args) || args.length <= 1)
            {
                dcontext.out.println("Save a configuration file");
                dcontext.out.println();
                dcontext.out.println("Syntax:");
                dcontext.out.println("  saveconfig (file)");
                return;
            }
            
            context.saveConfig(new FileOutputStream(new File(args[1])));
        }
        
        public void loadres(DebugContext dcontext, String args[]) throws IOException, ResourceException
        {
            if (DebugContext.isHelp(args) || (args.length < 2))
            {
                dcontext.out.println("Load Resource.");
                dcontext.out.println();
                dcontext.out.println("Syntax:");
                dcontext.out.println("  loadres (file | folder)");
            }
            
            provider = new ResourceProviderFS(context, new File(args[1]));
        }
        
        public void feature(DebugContext dcontext, String args[])
        {
            if (DebugContext.isHelp(args) || (args.length < 2))
            {
                dcontext.out.println("Enable, Disable, Set or Print a feature.");
                dcontext.out.println();
                dcontext.out.println("Syntax:");
                dcontext.out.println("  feature (enable | disable) (flag)");
                dcontext.out.println("  feature set (parameter) (value)");
                dcontext.out.println("  feature print [flag | parameter]");
                dcontext.out.println();
                dcontext.out.println("Features:");
                dcontext.out.println("  - agds     (flag)");
                dcontext.out.println("  - amiga    (flag)");
                dcontext.out.println("  - engine   (parameter)");
                dcontext.out.println("  - key      (parameter)");
                dcontext.out.println("  - waveform (parameter)");
                return;
            }
            
            if (args[1].equalsIgnoreCase("enable"))
            {
                if (args[2].equalsIgnoreCase("agds"))
                {
                    context.setAGDS(true);
                }
                else if (args[2].equalsIgnoreCase("amiga"))
                {
                    context.setAmiga(true);
                }
            }
            else if (args[1].equalsIgnoreCase("disable"))
            {
                if (args[2].equalsIgnoreCase("agds"))
                {
                    context.setAGDS(false);
                }
                else if (args[2].equalsIgnoreCase("amiga"))
                {
                    context.setAmiga(false);
                }
                else
                {
                    dcontext.out.println("Invalid flag");
                }
            }
            else if (args[1].equalsIgnoreCase("set"))
            {
                if (args[2].equalsIgnoreCase("engine"))
                {
                    context.setEngineVersion(Integer.valueOf(args[3], 16).intValue());
                }
                else if (args[2].equalsIgnoreCase("key"))
                {
                    context.setKey(args[3]);
                }
                else if (args[2].equalsIgnoreCase("waveform"))
                {
                    context.setWaveform(Context.toWaveID(args[3]));
                }
                else
                {
                    dcontext.out.println("Invalid parameter");
                }
            }
            else if (args[1].equalsIgnoreCase("print"))
            {
                if ((args.length == 2) || args[2].equalsIgnoreCase("agds"))
                {
                    if (context.getAGDS())
                    {
                        dcontext.out.println("AGDS: enabled");
                    }
                }

                if ((args.length == 2) || args[2].equalsIgnoreCase("amiga"))
                {
                    if (context.getAmiga())
                    {
                        dcontext.out.println("Amiga: enabled");
                    }
                }

                if ((args.length == 2) || args[2].equalsIgnoreCase("engine"))
                {
                    dcontext.out.print("Engine: 0x");
                    dcontext.out.println(Integer.toString(context.getEngineVersion(), 16));
                }

                if ((args.length == 2) || args[2].equalsIgnoreCase("key"))
                {
                    dcontext.out.print("Key: ");
                    dcontext.out.println(context.getKey());
                }

                if ((args.length == 2) || args[2].equalsIgnoreCase("waveform"))
                {
                    dcontext.out.print("Waveform: ");
                    dcontext.out.println(context.fromWaveID(context.getWaveform()));
                }
            }
        }
    }
}