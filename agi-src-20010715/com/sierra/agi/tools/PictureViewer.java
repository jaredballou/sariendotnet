/*
 * PictureViewer.java
 */

package com.sierra.agi.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.sierra.agi.*;
import com.sierra.agi.awt.*;
import com.sierra.agi.pic.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class PictureViewer extends Frame implements Runnable
{
    /** Picture. */
    protected Picture pic;
    
    /** Picture Canvas. */
    protected BufferedCanvas canvas;
    
    /** Picture Image. */
    protected Image img;
    
    /** Autoexit. */
    protected boolean autoexit = false;
    
    /** Creates new Picture Viewer */
    public PictureViewer(Picture pic) throws PictureException
    {
        this.pic = pic;
        this.img = pic.getImage();

        initComponent();
        pack();

        setTitle("Picture Viewer");
    }

    /** Creates new Picture Viewer */
    public PictureViewer(Picture pic, int picNumber) throws PictureException
    {
        this.pic = pic;
        this.img = pic.getImage();

        initComponent();
        pack();

        setTitle("Picture Viewer [" + picNumber + "]");
    }
    
    public void initComponent()
    {
        addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(WindowEvent ev)
                {
                    if (thr != null)
                    {
                        thr.interrupt();
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
        canvas = new PictureCanvas();
        add(canvas, BorderLayout.CENTER);

        thr = new Thread(this, "picture-animator");
        thr.start();
    }

    private void addAutoExit()
    {
        autoexit = true;
    }
    
    public static void main(String args[]) throws IOException, PictureException
    {
        File          file = new File(args[0]);
        Picture       pic;
        PictureViewer viewer;
        
        pic    = Picture.loadPicture(null, new FileInputStream(file));
        viewer = new PictureViewer(pic);
        viewer.addAutoExit();
        viewer.setVisible(true);
    }
    
    /** Update Thread */
    protected Thread thr;
    
    public void run()
    {
        try
        {
            Thread.sleep(128);
            
            while (pic.next())
            {
                Thread.sleep(128);
                canvas.regenerate();
            }
        }
        catch (InterruptedException e)
        {
        }
        catch (PictureException e)
        {
            e.printStackTrace();
        }
        
        thr = null;
    }
    
    public class PictureCanvas extends BufferedCanvas
    {
        public boolean generate(Graphics g)
        {
            Dimension  d = offDimension;
            int        x, y;

            prepareImage(img, this);
            
            g.setColor(Color.black);
            g.fillRect(0, 0, d.width, d.height);
            x = (d.width  - img.getWidth(this))  / 2;
            y = (d.height - img.getHeight(this)) / 2;
            
            return g.drawImage(img, x, y, this);
        }
        
        public Dimension getPreferredSize()
        {
            return new Dimension(640, 400);
        }

        public Dimension getMinimumSize()
        {
            return new Dimension(640, 400);
        }
    }
}