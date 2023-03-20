package com.fiskmods.gameboii.wrapper;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.fiskmods.gameboii.sound.ISoundInstance;

public class SoundInstance implements ISoundInstance
{
    private final Clip clip;
    private final float defVolume;

    public SoundInstance(Clip clip, float defVolume)
    {
        this.clip = clip;
        this.defVolume = defVolume;
    }

    public boolean isPlaying()
    {
        return clip.isRunning();
    }

    @Override
    public void stop()
    {
        if (clip.isRunning())
        {
            clip.stop();
        }

        clip.close();
    }

    @Override
    public void setVolume(float volume)
    {
        if (volume < 0 || volume > 1)
        {
            throw new IllegalArgumentException("Volume not valid: " + volume);
        }

        if (volume < 0.01F)
        {
            volume = 0;
        }

        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gain.setValue(20 * (float) Math.log10(volume * defVolume));
    }
}
