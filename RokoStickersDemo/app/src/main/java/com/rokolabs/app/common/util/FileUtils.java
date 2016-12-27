package com.rokolabs.app.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils
{

    /**
     * 将String数据存为文件
     */
    public static File saveStringToFile(String name, String path)
    {
        byte[] b = name.getBytes();
        BufferedOutputStream stream = null;
        File file = null;
        try
        {
            file = new File(path);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

}
