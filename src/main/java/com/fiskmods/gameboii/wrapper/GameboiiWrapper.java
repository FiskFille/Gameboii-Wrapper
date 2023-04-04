package com.fiskmods.gameboii.wrapper;

import com.fiskmods.gameboii.Cartridge;
import com.fiskmods.gameboii.Engine;
import com.fiskmods.gameboii.GameboiiSystem;
import com.fiskmods.gameboii.sound.ISoundDispatcher;

import java.io.*;

public class GameboiiWrapper extends GameboiiSystem
{
    private static final File SAVE_DIR = new File(System.getProperty("user.home"), "/AppData/Roaming/Gameboii");

    public static void onShutdown()
    {
        SoundPool.clear();
    }

    @Override
    public void quit()
    {
        System.exit(0);
    }

    @Override
    protected void onLoad(Cartridge cartridge)
    {
        try
        {
            byte[] data = GameboiiSave.loadData(new File(SAVE_DIR, cartridge.id + ".boii"));

            if (data != null)
            {
                Engine.get(cartridge).readSaveData(data);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSave(Cartridge cartridge, byte[] data) throws Exception
    {
        GameboiiSave.saveData(new File(SAVE_DIR, cartridge.id + ".boii"), data);
    }

    @Override
    public InputStream getInputStream(Cartridge cartridge, String path)
    {
        return Main.class.getResourceAsStream("/assets/" + cartridge.id + "/" + path);
    }

    @Override
    public ISoundDispatcher loadSoundData(Cartridge cartridge, String path) throws IOException
    {
        InputStream in = getInputStream(cartridge, "sounds/" + path + ".mp3");
        return in != null ? SoundDispatcher.read(new ByteArrayInputStream(copyInputStream(in))) : null;
    }

    @Override
    public void setPartialTicks(float f)
    {
        GameboiiWindow.getInstance().timer.renderPartialTicks = f;
    }

    @Override
    public float partialTicks()
    {
        return GameboiiWindow.getInstance().timer.renderPartialTicks;
    }

    public static byte[] copyInputStream(InputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) > -1)
        {
            out.write(buffer, 0, len);
        }

        in.close();
        out.flush();
        return out.toByteArray();
    }
}
