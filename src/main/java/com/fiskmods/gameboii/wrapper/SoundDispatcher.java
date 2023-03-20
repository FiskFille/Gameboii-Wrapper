package com.fiskmods.gameboii.wrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.fiskmods.gameboii.sound.ISoundDispatcher;
import com.fiskmods.gameboii.sound.ISoundInstance;
import com.fiskmods.gameboii.sound.Sound.Category;

public class SoundDispatcher implements ISoundDispatcher
{
    private final AudioFormat format;
    private final long frameLength;
    private final byte[] audioData;

    private SoundDispatcher(AudioInputStream decoded) throws IOException
    {
        format = decoded.getFormat();
        frameLength = decoded.getFrameLength();
        audioData = GameboiiWrapper.copyInputStream(decoded);
    }

    private AudioInputStream stream()
    {
        return new AudioInputStream(new ByteArrayInputStream(audioData), format, frameLength);
    }

    @Override
    public ISoundInstance dispatch(Category category, float volume, float pitch)
    {
        try
        {
            Clip clip = AudioSystem.getClip();
            SoundInstance instance = new SoundInstance(clip, volume);
            clip.open(stream());
            instance.setVolume(category.getVolume());
            clip.setFramePosition(0);
            clip.start();

            return SoundPool.add(category, instance);
        }
        catch (LineUnavailableException | IOException e)
        {
            e.printStackTrace();
        }

        return ISoundInstance.NULL;
    }

    public static ISoundDispatcher read(InputStream in) throws IOException
    {
        try
        {
            AudioInputStream input = AudioSystem.getAudioInputStream(in);
            AudioFormat baseFormat = input.getFormat();
            AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            return new SoundDispatcher(AudioSystem.getAudioInputStream(decodeFormat, input));
        }
        catch (UnsupportedAudioFileException e)
        {
            throw new IOException(e);
        }
    }
}
