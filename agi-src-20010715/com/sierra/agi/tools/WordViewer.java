/*
 * ViewViewer.java
 */

package com.sierra.agi.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import com.sierra.agi.*;
import com.sierra.agi.word.*;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class WordViewer extends Frame
{
    /** Game Context */
    protected Context context;
    
    /** Words */
    protected Words words;
    
    /** Autoexit */
    protected boolean autoexit = false;
    
    /** Creates new Word Viewer */
    public WordViewer(Context context, Words words)
    {
        this.context = context;
        this.words   = words;

        initComponent();
        pack();
    }
    
    public void initComponent()
    {
        List     list;
        Word     word;
        Object[] wordList;
        int      i;
        
        addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(WindowEvent ev)
                {
                    setVisible(false);
                    dispose();

                    if (autoexit)
                    {
                        System.exit(0);
                    }
                }
            }
        );

        setLayout(new BorderLayout());
        
        list     = new List();
        wordList = words.getWordList();
        
        for (i = 0; i < wordList.length; i++)
        {
            word = (Word)wordList[i];
            list.add(word.text + " (" + word.number + ")");
        }
        
        add(list, BorderLayout.CENTER);
        setTitle("Word Viewer");
    }
    
    private void addAutoExit()
    {
        autoexit = true;
    }
}