/*
 * ViewViewer.java
 */

package com.sierra.agi.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.sierra.agi.*;
import com.sierra.agi.awt.*;
import com.sierra.agi.view.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ViewViewer extends Frame implements ActionListener, Runnable
{
    /** View. */
    protected View view;

    /** View Number. */
    protected int viewNumber;
    
    /** Loop. */
    protected Loop loop;
    
    /** Loop Number. */
    protected byte loopNumber;
    
    /** Cell. */
    protected Cell cell;
    
    /** Cell Number. */
    protected byte cellNumber;
    
    /** Animation Thread. */
    protected Thread th;
    
    /** View Canvas */
    protected BufferedCanvas canvas;
    
    /** Game Context */
    protected Context context;
    
    /** Autoexit */
    protected boolean autoexit = false;
    
    /** Creates new View Viewer */
    public ViewViewer(Context context, View view)
    {
        this.context = context;
        this.view    = view;

        initComponent();
        pack();

        view.preloadImages(canvas);
    }

    /** Creates new View Viewer */
    public ViewViewer(Context context, View view, int viewNumber)
    {
        this.context    = context;
        this.view       = view;
        this.viewNumber = viewNumber;

        initComponent();
        pack();

        view.preloadImages(canvas);
    }
    
    public void initComponent()
    {
        Panel  panel = new Panel();
        Button button;
        
        addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(WindowEvent ev)
                {
                    if (th != null)
                    {
                        th.interrupt();
                        th = null;
                    }
                    
                    setVisible(false);
                    canvas.dispose();
                    dispose();

                    if (autoexit)
                    {
                        System.exit(0);
                    }
                }
            }
        );

        setLayout(new BorderLayout());
        panel.setLayout(new BottomButtonBarLayout());
        
        button = new Button();
        button.addActionListener(this);
        button.setActionCommand("nloop");
        button.setLabel("Loop >");
        panel.add(button);

        button = new Button();
        button.addActionListener(this);
        button.setActionCommand("ncell");
        button.setLabel("Cell >");
        panel.add(button);

        button = new Button();
        button.addActionListener(this);
        button.setActionCommand("pcell");
        button.setLabel("< Cell");
        panel.add(button);

        button = new Button();
        button.addActionListener(this);
        button.setActionCommand("ploop");
        button.setLabel("< Loop");
        panel.add(button);

        button = new Button();
        button.addActionListener(this);
        button.setActionCommand("anim");
        button.setLabel("Animate");
        panel.add(button);
        
        canvas = new ViewCanvas();
        add(canvas, BorderLayout.CENTER);
        add(panel,  BorderLayout.SOUTH);

        setTitle();
    }

    public void setTitle()
    {
        StringBuffer s = new StringBuffer(48);
        
        loop = view.getLoop(loopNumber);
        cell = loop.getCell(cellNumber);
        canvas.regenerate();

        s.append("View ");
        s.append(viewNumber);
        s.append(" [loop: ");
        s.append(loopNumber);
        s.append("/");
        s.append(view.getLoopCount() - 1);
        s.append("] [cell: ");
        s.append(cellNumber);
        s.append("/");
        s.append(loop.getCellCount() - 1);
        s.append("]");

        setTitle(s.toString());
    }
    
    private void addAutoExit()
    {
        autoexit = true;
    }
    
    public static void main(String args[]) throws IOException, ViewException
    {
        File       file = new File(args[0]);
        View       view;
        ViewViewer viewer;
        
        view   = View.loadView(null, new FileInputStream(file), (int)file.length());
        viewer = new ViewViewer(new Context(), view);
        viewer.addAutoExit();
        viewer.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ev)
    {
        String s = ev.getActionCommand();
        
        if (s.equals("nloop"))
        {
            if ((loopNumber + 1) < view.getLoopCount())
            {
                loopNumber++;
                cellNumber = 0;
            }
        }
        else if (s.equals("ploop"))
        {
            if (loopNumber != 0)
            {
                loopNumber--;
                cellNumber = 0;
            }
        }
        else if (s.equals("ncell"))
        {
            if ((cellNumber + 1) < loop.getCellCount())
            {
                cellNumber++;
            }
        }
        else if (s.equals("pcell"))
        {
            if (cellNumber != 0)
            {
                cellNumber--;
            }
        }
        else if (s.equals("anim"))
        {
            if (th == null)
            {
                th = new Thread(this);
                th.start();
            }
            else
            {
                th.interrupt();
                th = null;
            }
        }
        
        setTitle();
    }
    
    public void run()
    {
        try
        {
            while (true)
            {
                Thread.sleep(96);
                
                if ((cellNumber + 1) < loop.getCellCount())
                {
                    cellNumber++;
                }
                else
                {
                    cellNumber = 0;
                }
                
                setTitle();
            }
        }
        catch (InterruptedException e)
        {
        }
    }
    
    public class ViewCanvas extends BufferedCanvas
    {
        public boolean generate(Graphics g)
        {
            Dimension d = offDimension;
            int       x, y;
            
            g.setColor(getBackground());
            g.fillRect(0, 0, d.width, d.height);
            
            x = 160;
            y = 100 - cell.height;
            return g.drawImage(cell.getImage(), x, y, this);
        }
        
        public Dimension getPreferredSize()
        {
            return new Dimension(320, 200);
        }

        public Dimension getMinimumSize()
        {
            return new Dimension(320, 200);
        }
    }
}