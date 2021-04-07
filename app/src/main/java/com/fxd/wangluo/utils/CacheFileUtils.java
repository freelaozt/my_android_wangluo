package com.fxd.wangluo.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LaoZhang on 2018/3/12.
 */
public class CacheFileUtils {

    //保存文件
    public void saveLocalFileUtils(Context context, String json,String fileName) {
        String fileListName[] = context.fileList();
        try {
            for (int i = 0; i < fileListName.length; i++) {
                if (fileListName[i].equals(fileName)) {
                    context.deleteFile(fileName);
                }
                FileOutputStream os = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                //写数据
                os.write(json.getBytes());
                os.close();//关闭文件
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //打开文件
    public String openLocalFileUtils(Context context, String fileName) {
        byte buffer[] = new byte[4096];
        int len = 0;
        try {
            FileInputStream in = context.openFileInput(fileName);
            //将数据读到buffer.
            len = in.read(buffer);
            in.close();//关闭文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer, 0, len);
    }
}
