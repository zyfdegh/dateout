package edu.nuist.dateout.util;

import java.io.File;

import edu.nuist.dateout.value.AppConfig;

/**
 * 用于组合成语音图片等文件完整路径
 * 
 * @author Veayo
 *
 */
public class FilePathTool
{
    private static String sdcardDir = new FileAssit().getValidSdCardPath() + "/";
    
    /**
     * 输入发送图片的文件名,返回该文件的完整存放路径
     * 
     * @param fileName 发送图片的文件名,带后缀
     * @return 文件在系统中的完整路径
     */
    public static String getImageSentPath(String fileName)
    {
        return sdcardDir + AppConfig.DIR_APP_DATA_IMAGE_SENT + fileName;
    }
    
    /**
     * 输入接收的图片的文件名,返回该文件的完整存放路径
     * 
     * @param fileName 发送图片的文件名,带后缀
     * @return 文件在系统中的完整路径
     */
    public static String getImageReceivedPath(String fileName)
    {
        return sdcardDir + AppConfig.DIR_APP_DATA_IMAGE_SENT + fileName;
    }
    
    /**
     * 输入发送的语音的文件名,返回该文件的完整存放路径
     * 
     * @param fileName 发送图片的文件名,带后缀
     * @return 文件在系统中的完整路径
     */
    public static String getAudioSentPath(String fileName)
    {
        return sdcardDir + AppConfig.DIR_APP_DATA_AUDIO_SENT + fileName;
    }
    
    /**
     * 输入接收的语音的文件名,返回该文件的完整存放路径
     * 
     * @param fileName 发送图片的文件名,带后缀
     * @return 文件在系统中的完整路径
     */
    public static String getAudioReceivedPath(String fileName)
    {
        return sdcardDir + AppConfig.DIR_APP_DATA_AUDIO_RECEIVED + fileName;
    }
    
    /**
     * 输入头像文件名,返回该文件的完整存放路径
     * 
     * @param fileName 发送图片的文件名,带后缀,如IMAGEHEAD_user2_eb186aee966fd2513a6108f28df24ff5.jpg
     * @return 文件在系统中的完整路径,如/mnt/.../Dateout/.../IMAGEHEAD_user2_eb186aee966fd2513a6108f28df24ff5.jpg
     */
    public static String getHeadCachedPath(String fileName)
    {
        return sdcardDir + AppConfig.DIR_APP_CACHE_IMAGE_HEAD + fileName;
    }
    
    /**
     * 输入用户资料卡照片文件名,返回该文件的完整存放路径
     * 
     * @param fileName 资料卡照片的文件名,带后缀,如IMAGEVCARD_user2_eb186aee966fd2513a6108f28df24ff5.jpg
     * @return 文件在系统中的完整路径,如/mnt/.../Dateout/.../IMAGEVCARD_user2_eb186aee966fd2513a6108f28df24ff5.jpg
     */
    public static String getVCardImagePath(String fileName)
    {
        return sdcardDir + AppConfig.DIR_APP_DATA_IMAGE_VCARD + fileName;
    }
    
    /**
     * 输游戏背景图片文件名,返回该文件的完整存放路径
     * 
     * @param fileName 游戏背景图片文件名,带后缀,如IMAGEGAME_user2_eb186aee966fd2513a6108f28df24ff5.jpg
     * @return 文件在系统中的完整路径,如/mnt/.../Dateout/.../IMAGEGAME_user2_eb186aee966fd2513a6108f28df24ff5.jpg
     */
    public static String getGameImagePath(String fileName)
    {
        return sdcardDir + AppConfig.DIR_APP_CACHE_IMAGE_GAME + fileName;
    }
    
    /**
     * 获取相机拍照缓存文件位置
     * 
     * @return
     */
    public static String getCameraCachedImagePath()
    {
        File file = new File(new FileAssit().getValidSdCardPath() + "/" + AppConfig.FILE_CAMERA_CACHE_IMAGE);
        return file.getAbsolutePath();
    }
    
    /**
     * 获取照片裁剪缓存文件位置
     * 
     * @return
     */
    public static String getCropCachedImagePath()
    {
        File file = new File(new FileAssit().getValidSdCardPath() + "/" + AppConfig.FILE_CROP_CACHE_IMAGE);
        return file.getAbsolutePath();
    }
    
    /**
     * 获取下载图片缓存位置
     * 
     * @return
     */
    public static String getDownloadCachedImagePath()
    {
        File file = new File(new FileAssit().getValidSdCardPath() + "/" + AppConfig.FILE_DOWNLOAD_CACHE_IMAGE);
        return file.getAbsolutePath();
    }
}
