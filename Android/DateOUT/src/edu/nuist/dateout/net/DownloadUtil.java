package edu.nuist.dateout.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.value.AppConfig;

/**
 * 文件下载工具类
 * */
public class DownloadUtil
{
    
    /**
     * 用于从服务端下载指定的文件 http://192.168.1.100:8080/UploadServlet/downloadfile?
     * 
     * @param fileName 要下载的文件在服务端的文件名
     * @param filePath 要保存的文件路径, 如"/mnt/sdcard/Dateout/Cache/test.png"
     * @return 返回下载好后filePath的文件
     * @throws IOException
     */
    // TODO 显示下载进度与速度
    public File downloadFileByName(String fileName, String fileSavePath)
        throws IOException
    {
        String requestParam = "filename=" + fileName;
        URL url;
        try
        {
            url = new URL(AppConfig.URL_DOWNLOAD_SERVLET + requestParam);
            return downloadFileByUrl(url, fileSavePath);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public File downloadFileByUrl(URL url, String fileSavePath)
        throws IOException
    {
        HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
        // 设置连接超时
        urlCon.setConnectTimeout(10 * 1000);
        urlCon.setReadTimeout(60 * 1000);
        
        Log.v("Dateout", "DownloadUtil==>" + "开始下载文件");
        Log.v("Dateout", "DownloadUtil==>" + "下载URL:" + url);
        
        /* Open a connection to that URL. */
        URLConnection ucon = url.openConnection();
        /*
         * * Define InputStreams to read from the URLConnection.
         */
        InputStream is = ucon.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayBuffer baf = new ByteArrayBuffer(1024);
        int current = 0;
        while ((current = bis.read()) != -1)
        {
            baf.append((byte)current);
        }
        // 写入流到文件
        File file = new FileAssit().writeBufferToFile(baf, fileSavePath);
        Log.v("Dateout", "DownloadUtil==>" + "文件下载成功:" + file.getAbsolutePath());
        return file;
    }
}
