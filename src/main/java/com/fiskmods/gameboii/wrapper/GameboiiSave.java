package com.fiskmods.gameboii.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameboiiSave
{
    public static void saveData(File file, byte[] data) throws IOException
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        FileOutputStream out = new FileOutputStream(file);

        try
        {
            out.write(data);
        }
        finally
        {
            out.close();
        }
    }

    public static byte[] loadData(File file) throws Exception
    {
        if (file.exists())
        {
            FileInputStream input = new FileInputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;

            while ((n = input.read(buffer)) != -1)
            {
                output.write(buffer, 0, n);
            }

            input.close();
            return output.toByteArray();
        }

        return null;
    }
}
