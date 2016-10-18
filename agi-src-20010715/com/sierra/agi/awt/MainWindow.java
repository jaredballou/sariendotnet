/*
 * MainWindow.java
 */

package com.sierra.agi.awt;

import java.awt.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class MainWindow extends Frame
{
    public MainScreen screen = null;
    
    /** Creates new MainWindow */
    public MainWindow()
    {
        initComponent();
        pack();
    }

    public void initComponent()
    {
        setTitle("Adventure Game Interpreter");
        setLayout(new BorderLayout());
        setIconImage(getToolkit().createImage(getClass().getResource("icon.gif")));

        screen = new MainScreen();
        add(screen, BorderLayout.CENTER);
    }
}