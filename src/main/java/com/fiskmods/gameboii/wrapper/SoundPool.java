package com.fiskmods.gameboii.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.gameboii.sound.ISoundInstance;
import com.fiskmods.gameboii.sound.Sound.Category;

public class SoundPool
{
    private static final List<SoundEntry> SOUNDS = new ArrayList<>();
    
    public static void update()
    {
        for (SoundEntry e : new ArrayList<>(SOUNDS))
        {
            if (!e.instance.isPlaying())
            {
                e.instance.stop();
                SOUNDS.remove(e);
                continue;
            }
            
            e.instance.setVolume(e.category.getVolume());
        }
    }
    
    public static void clear()
    {
        new ArrayList<>(SOUNDS).forEach(t -> t.instance.stop());
        SOUNDS.clear();
    }

    public static ISoundInstance add(Category category, SoundInstance instance)
    {
        SOUNDS.add(new SoundEntry(category, instance));
        return instance;
    }
    
    private static class SoundEntry
    {
        private final Category category;
        private final SoundInstance instance;
        
        private SoundEntry(Category category, SoundInstance instance)
        {
            this.category = category;
            this.instance = instance;
        }
    }
}
