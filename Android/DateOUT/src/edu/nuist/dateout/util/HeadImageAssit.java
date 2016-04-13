package edu.nuist.dateout.util;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import edu.nuist.dateout.task.UploadFileTask;
import edu.nuist.dateout.util.CacheAssit;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.util.FileNameGenTool;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.value.AppConfig;
import edu.nuist.dateout.value.VariableHolder;

public class HeadImageAssit
{
    /**
     * 用于上传用户头像
     * 
     * @param uri
     */
    public void cacheAndUploadHeadImage(String userId, Context context, Uri headFileri, Handler uploadHandler)
    {
        // 原始图像文件
        String absolutePath = new FileAssit().getImageAbsolutePath(context, headFileri);
        File srcFile = new File(absolutePath);
        // 拷贝文件到缓存目录，并重命名
        String dstFileName = new FileNameGenTool().generateHeadImageName(srcFile, userId);
        String dstFilePath = FilePathTool.getHeadCachedPath(dstFileName);
        File dstFile = new File(dstFilePath);
        // 拷贝源文件到头像缓存目录
        short copyResult = new FileAssit().copyFile(srcFile, dstFile);
        if (copyResult == VariableHolder.COPY_RESULT_SUCCESS)
        {
            new UploadFileTask(dstFile, uploadHandler).execute();
        }
    }
    
    /**
     * 给定用户Id,从本地头像缓存中查找头像并返回
     * 
     * @param userId 用于Id
     * @return 用户头像,null表示没有找到
     */
    public File getHeadImageFromCache(String userId)
    {
        // 缓存目录
        File dir = new File(new FileAssit().getValidSdCardPath() + "/" + AppConfig.DIR_APP_CACHE_IMAGE_HEAD);
        String filePrefixStr = VariableHolder.FILE_PREFIX_IMAGE_HEAD + userId;
        File file = new CacheAssit().getLastestFile(dir, filePrefixStr);
        if (file != null && file.exists())
        {
            Log.v("Dateout", "CacheAssit==>" + "找到缓存头像:" + file.getAbsolutePath());
            return file;
        }
        else
        {
            Log.v("Dateout", "CacheAssit==>" + "没有找到缓存头像:" + filePrefixStr + "...");
            return null;
        }
    }
}
