package edu.nuist.dateout.util;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import edu.nuist.dateout.value.AppConfig;

//TODO 解决Permission Denied错误
/**
 * @author Veayo 用于协助缓存用户头像图片
 */
public class CacheAssit
{
    public short clearPictureCache()
    {
        File imageLoaderCacheDir = new File(AppConfig.DIR_APP_CACHE_IMAGELOADER);
        if (imageLoaderCacheDir.exists() && imageLoaderCacheDir.canWrite())
        {
            return new FileAssit().deleteFile(imageLoaderCacheDir);
        }
        else
        {
            return -1;
        }
    }
    
    public short clearAudioCache()
    {
        return 0;
    }
    
    /**
     * 缓存用图片到本地,名字为userId_imageMd5.png
     * 
     * @param userId 用户ID
     * @param userHeadImage 用户头像
     * @return 缓存结果 成功或者失败
     * */
    // public short cacheImage(String userId, String imageMD5, Drawable userHeadImage)
    // {
    // return cacheImage(userId + "_" + imageMD5 + ".png", userHeadImage);
    // }
    
    /**
     * 缓存图片文件到程序指定目录
     * 
     * @param fileName 完整的文件名,如test.png
     * @param userHeadImage 图像数据
     * @return
     */
    // public short cacheImage(String fileName, Drawable image)
    // {
    // File file = new File(AppConfig.DIR_APP_CACHE_IMAGE_HEAD+ fileName + ".png");
    // // 如果存在文件,就不覆盖了
    // if (file.exists())
    // {
    // return VariableHolder.CACHE_FILE_EXIST;
    // }
    // else
    // {
    // // 输入流为传过来的Drawable头像
    // InputStream is = new FormatTools().Drawable2InputStream(image);
    // try
    // {
    // // 输出流为本地缓存目录中的文件流
    // // 文件不存在,则新建文件
    // FileOutputStream fos = new FileOutputStream(file);
    // byte[] buffer = new byte[1024];
    // int len = 0;
    // while ((len = is.read(buffer)) != -1)
    // {
    // fos.write(buffer, 0, len);
    // }
    // is.close();
    // fos.close();
    // Log.v("Dateout", "CacheAssit==>" + "缓存文件" + fileName + "成功");
    // return VariableHolder.CACHE_SUCCESS;
    // }
    // catch (FileNotFoundException e)
    // {
    // e.printStackTrace();
    // return VariableHolder.CACHE_ERR_FILE_CREATE;
    // }
    // catch (IOException e)
    // {
    // e.printStackTrace();
    // return VariableHolder.CACHE_ERR_FILE_WRITE;
    // }
    // }
    // }
    
    /**
     * 通过文件名从本地找到图片
     * 
     * @param fileName
     * @return
     */
    // public Uri getHeadImageFromCache(String fileName)
    // {
    // // 输入流为文件流
    // File file = new File(cacheDir, fileName + ".png");
    // return Uri.parse(cacheDir+fileName+"")
    // return new FormatTools().file2Drawable(file);
    // }
    
    /**
     * 用于拷贝文件file到新的目录newDir,并返回复制后的文件
     * 
     * @param file
     * @return
     */
    // public File copyAndRenameFile(File file, String newFilePath)
    // {
    // boolean result = file.renameTo(new File(newFilePath));
    // if (result)
    // {
    // return new File(newFilePath);
    // }
    // else
    // {
    // return null;
    // }
    // }
    
    /**
     * 获取照相机拍摄的缓存图片
     * 
     * @return 返回相机拍摄的照片,null表示文件不存在
     */
    public File getCameraCachedImage()
    {
        File imageCameraCached = new File(FilePathTool.getCameraCachedImagePath());
        if (imageCameraCached.exists() && imageCameraCached.length() > 0 && imageCameraCached.canRead())
        {
            return imageCameraCached;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 用于获取目录dir下以filePrefix开头的最新文件
     * 
     * @param dir 目录
     * @param filePrefix 文件前缀
     * @return 返回最新文件,null表示找不到
     */
    public File getLastestFile(File dir, final String filePrefix)
    {
        // 对当前目录下的文件进行过滤
        String[] filesPaths = new FileAssit().getFilePathsInDir(dir, filePrefix);
        if (filesPaths != null)
        {
            // 取出最新的文件
            if (filesPaths.length > 0)
            {
                Arrays.sort(filesPaths, new FileComparator<String>());
                return new File(filesPaths[0]);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 用于取出一批String[] filesNames文件中的最新文件(按最后修改日期排序)
     * 
     * @author Veayo
     *
     * @param <T> File
     */
    class FileComparator<T> implements Comparator<T>
    {
        public int compare(T o1, T o2)
        {
            File file1 = new File((String)o1);
            File file2 = new File((String)o2);
            return (int)(file2.lastModified() - file1.lastModified());
        }
    }
}