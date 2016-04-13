package edu.nuist.dateout.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.regexp.internal.recompile;

import edu.nuist.dateout.config.ServerConfig;

public class FilePathAssit
{
    
    /**
     * 给定用户Id,从本地头像缓存中查找最新头像并返回
     * 
     * @param userId 用于Id
     * @return 用户头像,null表示没有找到
     */
    public static File getHeadImage(String userId)
    {
        // 缓存目录
        File dir = new File(ServerConfig.DIR_DATAOUT_MAIN + ServerConfig.DIR_DATEOUT_HEADIMAGES);
        if (dir.exists())
        {
            String filePrefixStr = ServerConfig.FILE_PREFIX_IMAGE_HEAD + userId;
            File file = FileAssit.getLastestFile(dir, filePrefixStr);
            if (file != null && file.exists())
            {
                return file;
            }
            else
            {
                System.out.println("文件不存在" + filePrefixStr + "...");
                return null;
            }
        }
        else
        {
            System.out.println("目录不存在" + dir.getAbsolutePath());
            dir.mkdirs();
            return null;
        }
    }
    
    /**
     * 取出用户userId的所有VCard照片的下载链接
     * 
     * @param userId 用户ID
     * @return 返回以IMAGEVCARD_userId开头的所有
     */
    public static List<String> getVCardImageDownLinks(String userId)
    {
        List<String> resultList = new ArrayList<String>();
        File dir = new File(ServerConfig.DIR_DATAOUT_MAIN + ServerConfig.DIR_DATEOUT_VCARDIMAGES);
        final String filePrefix = ServerConfig.FILE_PREFIX_IMAGE_VCARD + userId;
        // 取出dir目录下以filePrefix开头的文件名
        String[] filesPaths = FileAssit.getFilePathsInDir(dir, filePrefix);
        for (int i = 0; i < filesPaths.length; i++)
        {
            String downloadLink = ServerConfig.URL_DOWNLOAD_SERVLET + FileAssit.filePath2FileName(filesPaths[i]);
            resultList.add(downloadLink);
        }
        return resultList;
    }
    
    /**
     * 取出用户userId的最新头像照片的下载链接
     * 
     * @param userId 用户ID
     * @return 返回以IMAGEVCARD_userId开头的所有
     */
    public static String getHeadImageDownLink(String userId)
    {
        String downloadLink;
        File dir = new File(ServerConfig.DIR_DATAOUT_MAIN + ServerConfig.DIR_DATEOUT_HEADIMAGES);
        String filePrefix = ServerConfig.FILE_PREFIX_IMAGE_HEAD + userId;
        File lastestHead = FileAssit.getLastestFile(dir, filePrefix);
        if (lastestHead!=null&&lastestHead.exists()&&lastestHead.length()>0&&lastestHead.canRead())
        {
            downloadLink = ServerConfig.URL_DOWNLOAD_SERVLET + lastestHead.getName();
            return downloadLink;
        }else {
            return null;
        }
    }
}
