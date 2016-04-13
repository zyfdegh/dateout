package edu.nuist.dateout.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import edu.nuist.dateout.config.ServerConfig;
import edu.nuist.dateout.model.UploadedFileModel;

public class FileAssit
{
    /**
     * 删除目录dir下,所有以filePrefix开头的文件
     * @param dir 文件的存放路径
     * @param filePrefix 文件的开头,如IMAGEVCARD_user2
     * @return 如果文件全部删除成功返回true
     */
    public static boolean deleteAllFilesWithPrefix(File dir,final String filePrefix){
        boolean result=true;
        // 列出目录dir下以filePrefix开头的所有文件名,并存储到filesPaths[]
        String filePaths[]=getFilePathsInDir(dir,filePrefix);
        
        for (int i = 0; i < filePaths.length; i++)
        {
            File file=new File(dir+"\\"+filePath2FileName(filePaths[i]));
            if (file.exists()&&file.canWrite())
            {
                //执行文件删除,只要有一个文件删除失败,返回false
                if(!file.delete()){
                    result=false;
                }
            }else {
                result=false;
            }
        }
        return result;
    }
    /**
     * 获取目录dir下,所有以filePrefix开头的文件的完整路径,作为返回值
     * @param dir 文件存放完整绝对目录
     * @param filePrefix 文件名前缀
     * @return 返回以filePrefix开头的文件的完整路径
     */
    public static String[] getFilePathsInDir(File dir,final String filePrefix){
        // 列出目录dir下以filePrefix开头的所有文件名,并存储到filesPaths[]
        String[] fileNames = dir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.startsWith(filePrefix);// 找出以filePrefix开头的文件
            }
        });
        if (fileNames==null)
        {
            fileNames=new String[0];
        }else {
            for (int i = 0; i < fileNames.length; i++)
            {
                fileNames[i]=dir.getAbsolutePath()+"\\"+fileNames[i];//转为完整路径
            }
        }
        return fileNames;
    }
    
    public static String filePath2FileName(String filePath){
        return new File(filePath).getName();
    }
    
    /**
     * 输入文件名,返回文件存放的相对路径
     * 
     * @param fileName 带有标准前缀的文件的完整名,如IMAGESENT_f1ff6bd5b16af69d43e3880766c4ba90.jpg
     * @return 返回文件存放的相对路径,如\\dateout\\data\\images_sent\\IMAGESENT_f1ff6bd5b16af69d43e3880766c4ba90.jpg
     */
    public static UploadedFileModel getFileSavePath(String fileName)
    {
        UploadedFileModel ufm = new UploadedFileModel();
        ufm.setFileName(fileName);
        if (fileName.indexOf(ServerConfig.FILE_PREFIX_AUDIO_SENT) == 0)
        {
            ufm.setFileType(ServerConfig.FILE_TYPE_AUDIO_SENT);
            ufm.setFileSavePath(ServerConfig.DIR_DATEOUT_AUDIO + fileName);
        }
        else if (fileName.indexOf(ServerConfig.FILE_PREFIX_IMAGE_HEAD) == 0)
        {
            ufm.setFileType(ServerConfig.FILE_TYPE_IMAGE_HEAD);
            ufm.setFileSavePath(ServerConfig.DIR_DATEOUT_HEADIMAGES + fileName);
        }
        else if (fileName.indexOf(ServerConfig.FILE_PREFIX_IMAGE_SENT) == 0)
        {
            ufm.setFileType(ServerConfig.FILE_TYPE_IMAGE_SENT);
            ufm.setFileSavePath(ServerConfig.DIR_DATEOUT_SENTIMAGES + fileName);
        }
        else if (fileName.indexOf(ServerConfig.FILE_PREFIX_IMAGE_GAME) == 0)
        {
            ufm.setFileType(ServerConfig.FILE_TYPE_IMAGE_GAME);
            ufm.setFileSavePath(ServerConfig.DIR_DATEOUT_GAMEIMAGES + fileName);
        }
        else if (fileName.indexOf(ServerConfig.FILE_PREFIX_IMAGE_VCARD) == 0)
        {
            ufm.setFileType(ServerConfig.FILE_TYPE_IMAGE_VCARD);
            ufm.setFileSavePath(ServerConfig.DIR_DATEOUT_VCARDIMAGES + fileName);
        }
        else
        {
            System.out.println("无法获取文件名,或者文件名不合规则");
            ufm.setFileType(ServerConfig.FILE_TYPE_MISC);
            ufm.setFileSavePath(ServerConfig.DIR_DATEOUT_MISC + fileName);
        }
        return ufm;
    }
    
    /**
     * 用于获取目录dir下以filePrefix开头的最新文件
     * 
     * @param dir 目录
     * @param filePrefix 文件前缀
     * @return 返回最新文件,null表示找不到
     */
    public static File getLastestFile(File dir, final String filePrefix)
    {
        // 列出目录dir下以filePrefix开头的所有文件名,并存储到filesPaths[]
        // String[] filesNames = new FileAssit().getFileNamesInDir(dir, filePrefix);
        String[] filesPaths = FileAssit.getFilePathsInDir(dir, filePrefix);
        // 取出最新的文件
        if (filesPaths.length > 0)
        {
            // 按文件修改日期进行排序,最新的在filesNames[0]
            Arrays.sort(filesPaths, new FileComparator<String>());
            if (filesPaths.length > 1)
            {
                // 只保留最新的一张,删除旧的缓存文件
                for (int i = 1; i < filesPaths.length; i++)
                {
                    // 删除文件
                    File file = new File(filesPaths[i]);
                    if (file.exists() && file.canWrite())
                    {
//                        file.delete();
                    }
                }
            }
            return new File(filesPaths[0]);
        }
        else
        {
            return null;
        }
    }
}
