/*
 * ResourceCache.java
 */

package com.sierra.agi.res;

import java.io.*;

import com.sierra.agi.Context;
import com.sierra.agi.object.InventoryObjects;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.pic.Picture;
import com.sierra.agi.pic.PictureException;
import com.sierra.agi.sound.Sound;
import com.sierra.agi.view.View;
import com.sierra.agi.view.ViewException;
import com.sierra.agi.word.Words;

/**
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class ResourceCache extends Object
{
    /** Resource Provider. */
    protected ResourceProvider provider;

    /** Context. */
    protected Context context;
        
    /** Sound Resources. */
    protected Sound[] sounds = new Sound[256];
    
    /** View Resources. */
    protected View[] views = new View[256];

    /** Picture Resources. */
    protected Picture[] pics = new Picture[256];
    
    /** Logic Resources. */
    protected Logic[] logics = new Logic[256];

    /** Objects. */
    protected InventoryObjects objects;
    
    /** Words. */
    protected Words words;
    
    /** Creates new ResourceCache */
    public ResourceCache(Context c, ResourceProvider p) throws IOException, ResourceException
    {
        context  = c;
        provider = p;
    }
    
    public void loadView(short resNumber) throws IOException, ResourceException, ViewException
    {
        int         size   = provider.getSize(ResourceProvider.TYPE_VIEW, resNumber);
        InputStream stream = provider.open(ResourceProvider.TYPE_VIEW, resNumber);
        
        views[resNumber] = View.loadView(context, stream, size);
    }
    
    public void unloadView(short resNumber)
    {
        if (views[resNumber] != null)
        {
            views[resNumber].flushImages();
            views[resNumber] = null;
        }
    }
    
    public View getView(short resNumber)
    {
        if (views[resNumber] == null)
        {
            try
            {
                loadView(resNumber);
            }
            catch (Exception e)
            {
                return null;
            }
        }
        
        return views[resNumber];
    }
    
    public void loadSound(short resNumber) throws IOException, ResourceException
    {
        InputStream stream = provider.open(ResourceProvider.TYPE_SOUND, resNumber);
        
        sounds[resNumber] = Sound.loadSound(context, stream);
    }
    
    public void unloadSound(short resNumber)
    {
        sounds[resNumber] = null;
    }
    
    public Sound getSound(short resNumber)
    {
        if (sounds[resNumber] == null)
        {
            try
            {
                loadSound(resNumber);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        return sounds[resNumber];
    }

    public void loadPicture(short resNumber) throws IOException, ResourceException, PictureException
    {
        InputStream stream = provider.open(ResourceProvider.TYPE_PICTURE, resNumber);
        
        pics[resNumber] = Picture.loadPicture(context, stream);
    }
    
    public void unloadPicture(short resNumber)
    {
        pics[resNumber] = null;
    }
    
    public Picture getPicture(short resNumber)
    {
        if (pics[resNumber] == null)
        {
            try
            {
                loadPicture(resNumber);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        return pics[resNumber];
    }

    public void loadLogic(short resNumber) throws IOException, ResourceException, LogicException
    {
        InputStream stream = provider.open(ResourceProvider.TYPE_LOGIC, resNumber);
        int         size   = provider.getSize(ResourceProvider.TYPE_LOGIC, resNumber);
        
        logics[resNumber] = Logic.loadLogic(context, stream, size);
    }
    
    public void unloadLogic(short resNumber)
    {
        logics[resNumber] = null;
    }
    
    public Logic getLogic(short resNumber) throws LogicException
    {
        if (logics[resNumber] == null)
        {
            try
            {
                loadLogic(resNumber);
            }
            catch (IOException ioex)
            {
            }
            catch (ResourceException rex)
            {
            }
        }

        return logics[resNumber];
    }
    
    public Words getWords() throws ResourceException, IOException
    {
        if (words == null)
        {
            words = Words.loadWords(context, provider.open(ResourceProvider.TYPE_WORD, (short)0));
        }
        
        return words;
    }
    
    public InventoryObjects getObjects() throws ResourceException, IOException
    {
        if (objects == null)
        {
            objects = InventoryObjects.loadObjects(context, provider.open(ResourceProvider.TYPE_OBJECT, (short)0));
        }
        
        return objects;
    }

    public ResourceProvider getProvider()
    {
        return provider;
    }
}