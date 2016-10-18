/*
 * ExceptionDialog.java
 */

package com.sierra.agi.debug;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.sierra.agi.Context;
import com.sierra.agi.awt.BottomButtonBarLayout;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ExceptionDialog extends Dialog implements ActionListener, WindowListener
{
    /** Abort Button */
    protected Button abortButton;
    
    /** Ignore Button */
    protected Button ignoreButton;
    
    /** Dump Button */
    protected Button dumpButton;
    
    /** Exception Label */
    protected Label exceptionLabel;
    
    /** Bottom Panel */
    protected Panel bottomPanel;
    
    /** Text Area */
    protected TextArea exceptionText;
    
    /** Exception Context */
    protected Dumpable dumpable;

    /** Exception */
    protected Throwable throwable;
    
    /** Creates new ExceptionDialog */
    public ExceptionDialog(Frame parent, boolean modal, Throwable t)
    {
        super(parent, modal);
        this.throwable = t;
        initComponent();
        pack();
    }

    /** Creates new ExceptionDialog */
    public ExceptionDialog(Frame parent, boolean modal, Dumpable dumpable, Throwable t)
    {
        super(parent, modal);
        this.dumpable  = dumpable;
        this.throwable = t;
        initComponent();
        pack();
    }
    
    public void initComponent()
    {
        StringWriter sw = new StringWriter();
        
        setTitle("Exception");
        setLayout(new BorderLayout());

        bottomPanel = new Panel();
        bottomPanel.setLayout(new BottomButtonBarLayout());

        exceptionLabel = new Label();
        exceptionLabel.setText("An error occured during the execution");
        
        throwable.printStackTrace(new PrintWriter(sw));
        
        dumpButton = new Button();
        dumpButton.addActionListener(this);
        dumpButton.setActionCommand("dump");
        dumpButton.setLabel("Dump");

        bottomPanel.add(dumpButton, null);
        
        exceptionText = new TextArea();
        exceptionText.setText(sw.toString());
        exceptionText.setEditable(false);
        exceptionText.setFont(new Font("DialogInput", 0, 11));
        
        abortButton = new Button();
        abortButton.addActionListener(this);
        abortButton.setActionCommand("abort");
        abortButton.setLabel("Abort");

        ignoreButton = new Button();
        ignoreButton.addActionListener(this);
        ignoreButton.setActionCommand("ignore");
        ignoreButton.setLabel("Ignore");
        
        add(bottomPanel, BorderLayout.SOUTH);
        add(exceptionLabel, BorderLayout.NORTH);
        add(exceptionText, BorderLayout.CENTER);
        bottomPanel.add(ignoreButton, null);
        bottomPanel.add(abortButton,  null);
        addWindowListener(this);
    }
    
    public void actionPerformed(ActionEvent ev)
    {
        String action = ev.getActionCommand();
        
        if (action.equals("abort"))
        {
            System.exit(0);
        }
        else if (action.equals("ignore"))
        {
            setVisible(false);
            dispose();
        }
        else if (action.equals("dump"))
        {
            dump();
        }
    }
    
    public void windowDeactivated(WindowEvent ev)
    {
    }
    
    public void windowClosed(WindowEvent ev)
    {
    }
    
    public void windowDeiconified(WindowEvent ev)
    {
    }
    
    public void windowOpened(WindowEvent ev)
    {
    }
    
    public void windowIconified(WindowEvent ev)
    {
    }
    
    public void windowClosing(WindowEvent ev)
    {
        actionPerformed(new ActionEvent(ignoreButton, 0, "ignore"));
    }
    
    public void windowActivated(WindowEvent ev)
    {
    }
    
    protected void dump()
    {
        FileDialog  dlg = new FileDialog(new Frame(), "Select Filename for Dumping", FileDialog.SAVE);
        File        f;
        FileWriter  fw;
        PrintWriter writer;
        
        dlg.setModal(true);
        dlg.show();
        
        try
        {
            f = new File(dlg.getDirectory(), dlg.getFile());
            fw = new FileWriter(f);
            writer = new PrintWriter(fw);
            
            writer.println("** Exception Stack Dump **");
            throwable.printStackTrace(writer);

            if (throwable instanceof Dumpable)
            {
                writer.println();
                writer.println("** Exception Dump **");
                ((Dumpable)throwable).dump(writer);
            }

            dumpProperties(writer);
            
            if (dumpable != null)
            {
                writer.println();
                dumpable.dump(writer);
            }
            
            fw.close();
        }
        catch (IOException ioex)
        {
        }
    }
    
    protected void dumpProperties(PrintWriter writer)
    {
        java.util.Properties  prop = System.getProperties();
        java.util.Enumeration enum;
        String                s;
        
        enum = prop.keys();
        writer.println();
        writer.println("** Java VM Configuration Dump **");
        
        while (enum.hasMoreElements())
        {
            s = (String)enum.nextElement();
            
            writer.print(s);
            writer.print(": ");
            writer.println(prop.getProperty(s));
        }
    }
}