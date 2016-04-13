package edu.nuist.dateout.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import edu.nuist.dateout.value.AppConfig;
import edu.nuist.dateout.value.VariableHolder;

/**
 * 用于处理文件相关的操作,如拷贝文件, 初始化程序目录
 * 
 * @author Veayo
 *
 */
public class FileAssit
{
    /**
     * 用于拷贝文件源文件srcFile到新的文件dstFile
     * 
     * @param srcFile 源文件
     * @param dstFile 目标文件
     * @return
     */
    public short copyFile(File srcFile, File dstFile)
    {
        // 如果文件已经存在
        if (dstFile != null && dstFile.exists() && dstFile.length() >= 0)
        {
            return VariableHolder.COPY_RESULT_EXIST;// 文件已经存在
        }
        else
        {
            FileChannel inChannel;
            FileChannel outChannel;
            try
            {
                inChannel = new FileInputStream(srcFile).getChannel();
                outChannel = new FileOutputStream(dstFile).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return VariableHolder.COPY_RESULT_FAIL;// 失败
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
                return VariableHolder.COPY_RESULT_FAIL;// 失败
            }
            
            if (dstFile != null && dstFile.exists() && dstFile.length() >= 0)
            {
                return VariableHolder.COPY_RESULT_SUCCESS;// 拷贝成功
            }
            else
            {
                return VariableHolder.COPY_RESULT_FAIL;// 失败
            }
        }
    }
    
    /**
     * 用于初始化程序SD卡中的缓存目录
     */
    public void checkAppDirs()
    {
        Log.v("Dateout", "CacheAssit==>" + "初始化程序目录");
        
        // 查找可以使用的文件存放路径
        String validAppBaseDir = getValidSdCardPath();
        
        if (validAppBaseDir != null)
        {
            // 设置全局
            // DateoutApp.getInstance().setAppBaseDir(validAppBaseDir+"/");//如:/mnt/emmc
            String dirPath[] =
                new String[] {AppConfig.DIR_APP, AppConfig.DIR_APP_CACHE, AppConfig.DIR_APP_CACHE_IMAGE,
                    AppConfig.DIR_APP_CACHE_IMAGE_HEAD, AppConfig.DIR_APP_DATA, AppConfig.DIR_APP_DATA_AUDIO,
                    AppConfig.DIR_APP_DATA_AUDIO_RECEIVED, AppConfig.DIR_APP_DATA_AUDIO_SENT,
                    AppConfig.DIR_APP_DATA_FILE, AppConfig.DIR_APP_DATA_FILE_RECEIVED,
                    AppConfig.DIR_APP_DATA_FILE_SENT, AppConfig.DIR_APP_DATA_IMAGE,
                    AppConfig.DIR_APP_DATA_IMAGE_RECEIVED, AppConfig.DIR_APP_DATA_IMAGE_SENT,
                    AppConfig.DIR_APP_CACHE_IMAGE_GAME, AppConfig.DIR_APP_DATA_IMAGE_VCARD};
            File fileDir = null;
            for (int i = 0; i < dirPath.length; i++)
            {
                fileDir = new File(validAppBaseDir, dirPath[i]);
                if (!fileDir.exists())
                {
                    fileDir.mkdirs();
                }
            }
        }
    }
    
    /**
     * 写入输入流baf到文件file
     * 
     * @param baf 输入流
     * @param file 文件
     * @return 写入后的文件,注意返回结果的非空检测
     */
    public File writeBufferToFile(ByteArrayBuffer baf, String filePath)
    {
        // 写入文件到指定目录
        File file = new File(filePath);
        
        // File file = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"zzzzzz.jpg");
        if (file.exists())
        {
            // 如果已经存在文件,就不覆盖了
            // return file;
        }
        else
        {
            try
            {
                boolean createSuccess = file.createNewFile();
                if (!createSuccess)
                {
                    // 文件创建成功
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        FileOutputStream fout;
        if (baf != null)
        {
            try
            {
                fout = new FileOutputStream(file);
                fout.write(baf.toByteArray());
                fout.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
            return file;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 传入图片的Uri，返回该图片的绝对路径，一般用于处理从相册选取的图片
     * 
     * @param uri 从相册选来的图片路径
     * @return 图片在系统中的绝对路径，可在文件浏览器中查找到
     */
    public String getImageAbsolutePath(Context context, Uri uri)
    {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);// 从系统表中查询指定Uri对应的照片
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String pictureAbsolutePath = cursor.getString(columnIndex); // 获取照片绝对路径
        cursor.close();
        return pictureAbsolutePath;
    }
    
    /**
     * 传入图片的Uri，返回该图片的绝对路径，一般用于处理从相册选取的图片
     * 
     * @param context
     * @param uri
     * @return 图片在系统中的绝对路径Uri
     */
    public Uri getImageAbsoluteUri(Context context, Uri uri)
    {
        String absiluteUri = getImageAbsolutePath(context, uri);
        return Uri.parse(absiluteUri);
    }
    
    /**
     * 用于获取sdcardx中路径relatedDir的系统绝对路径 如程序缓存目录Dateout/Cache,在不同机型里面会有不同的绝对路径 如/mnt/sdcard/Dateout/Cache,
     * 或者/mnt/sdcard0/Dateout/Cache 或者/external_sdcard/Dateout/Cache 该方法将会根据不同机型在相对路径前加上实际外部存储路径
     * 
     * @param relatedDir
     * @return
     */
    public String getAbsoluteDir(String relatedDir)
    {
        return new FileAssit().getValidSdCardPath() + "/" + relatedDir;
    }
    
    /**
     * 用于检测外置SD卡的状态,挂载信息
     * 
     * @return
     */
    // public short checkExternalSDState()
    // {
    // String state = Environment.getExternalStorageState();
    // if (state.equals(Environment.MEDIA_MOUNTED))
    // {
    // // SD卡正常挂载
    // return 0;
    // }
    // else if (state.equals(Environment.MEDIA_BAD_REMOVAL))
    // {
    // // 介质在挂载前被移除，直接取出SD卡
    // return 1;
    // }
    // else if (state.equals(Environment.MEDIA_CHECKING))
    // {
    // // 正在磁盘检查，刚装上SD卡时
    // return 2;
    // }
    // else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
    // {
    // // sd卡存在并且已挂载，但是挂载方式为只读
    // return 3;
    // }
    // else if (state.equals(Environment.MEDIA_NOFS))
    // {
    // // 介质存在但是为空白或用在不支持的文件系统
    // return 4;
    // }
    // else if (state.equals(Environment.MEDIA_REMOVED))
    // {
    // // 无介质
    // return 5;
    // }
    // else if (state.equals(Environment.MEDIA_SHARED))
    // {
    // // SD卡存在但没有挂载，并且通过USB大容量存储共享
    // return 6;
    // }
    // else if (state.equals(Environment.MEDIA_UNMOUNTABLE))
    // {
    // // 存在SD卡但是不能挂载，例如发生在介质损坏
    // return 7;
    // }
    // else if (state.equals(Environment.MEDIA_UNMOUNTED))
    // {
    // // 有介质，未挂载，在系统中卸载SD卡了
    // return 8;
    // }
    // else
    // {
    // // 其他
    // return 9;
    // }
    // }
    
    /**
     * 用于删除文件和目录
     * 
     * @param file 完整文件路径
     * @return 结果 0-删除文件成功,1-文件不存在,2-删除文件成功,3-删除目录失败
     */
    public short deleteFile(File file)
    {
        if (!file.exists())
        {
            return 1;// 目录不存在
        }
        else if (file.isFile())
        {// 是个文件
            if (file.delete())
            {
                return 0;// 删除文件成功
            }
            else
            {
                return 2;// 删除文件失败
            }
        }
        else if (file.isDirectory())
        {
            File[] childFiles = file.listFiles();
            // 是空目录
            if (childFiles == null || childFiles.length == 0)
            {
                if (file.delete())
                {
                    return 0;// 删除目录成功
                }
                else
                {
                    return 3;// 删除目录失败
                }
            }
            else
            // 非空目录
            {
                for (int i = 0; i < childFiles.length; i++)
                {
                    deleteFile(childFiles[i]);// 递归删除目录内所有文件和目录
                }
                return 0;
            }
        }
        else
        {
            return 4;
        }
    }
    
    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     * 
     * @return
     */
    private static ArrayList<String> getDevMountList()
    {
        String[] toSearch = readTxtFile("/etc/vold.fstab").split(" ");
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < toSearch.length; i++)
        {
            if (toSearch[i].contains("dev_mount"))
            {
                if (new File(toSearch[i + 2]).exists())
                {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }
    
    /**
     * 获取扩展SD卡存储目录
     * 
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录 否则：返回内置SD卡目录
     * 
     * @return 返回可用的内置SD卡目录,如/mnt/emmc
     */
    public String getValidSdCardPath()
    {
        // 检测SD卡状态
        String stat = Environment.getExternalStorageState();
        // SD卡正确挂载
        if (stat.equals(Environment.MEDIA_MOUNTED))
        {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        
        String path = null;
        
        File sdCardFile = null;
        
        ArrayList<String> devMountList = getDevMountList();
        
        for (String devMount : devMountList)
        {
            File file = new File(devMount);
            
            if (file.isDirectory() && file.canWrite())
            {
                path = file.getAbsolutePath();
                
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);
                
                if (testWritable.mkdirs())
                {
                    testWritable.delete();
                }
                else
                {
                    path = null;
                }
            }
        }
        
        if (path != null)
        {
            sdCardFile = new File(path);
            return sdCardFile.getAbsolutePath();
        }
        
        return null;
    }
    
    /**
     * 读取指定路径的文本文件,返回文件内容
     * 
     * @param strFilePath 文件路径
     * @return 文件内容
     */
    public static String readTxtFile(String strFilePath)
    {
        String path = strFilePath;
        String content = ""; // 文件内容字符串
        // 打开文件
        File file = new File(path);
        // 如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else
        {
            try
            {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    // 分行读取
                    while ((line = buffreader.readLine()) != null)
                    {
                        content += line + "\n";
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }
    
    /**
     * 获取目录dir下,所有以filePrefix开头的文件的文件名,作为返回值
     * 
     * @param dir 文件存放完整绝对目录
     * @param filePrefix 文件名前缀
     * @return 返回以filePrefix开头的文件的文件名
     */
    public String[] getFilePathsInDir(File dir, final String filePrefix)
    {
        // 列出目录dir下以filePrefix开头的所有文件名,并存储到filesPaths[]
        String[] fileNames = dir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.startsWith(filePrefix);// 找出以filePrefix开头的文件
            }
        });
        if (fileNames == null)
        {
            fileNames = new String[0];
        }
        else
        {
            for (int i = 0; i < fileNames.length; i++)
            {
                fileNames[i] = dir.getAbsolutePath() + "/" + fileNames[i];// 转为完整路径
            }
        }
        return fileNames;
    }
}
