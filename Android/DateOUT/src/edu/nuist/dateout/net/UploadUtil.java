package edu.nuist.dateout.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import edu.nuist.dateout.util.NetworkAssit;
import android.util.Log;

public class UploadUtil
{
    private final int TIME_OUT = 10 * 1000; // 超时时间
    
    private final String CHARSET = "utf-8"; // 设置编码
    
    /**
     * android上传文件到服务器
     * 
     * @param file 需要上传的文件
     * @param RequestURL 请求的url
     * @return 返回响应的内容
     * @throws IOException
     */
    public int uploadFile(File file, String RequestURL)
        throws IOException
    {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        
        URL url = new URL(RequestURL);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout(TIME_OUT);
        conn.setConnectTimeout(TIME_OUT);
        conn.setDoInput(true); // 允许输入流
        conn.setDoOutput(true); // 允许输出流
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST"); // 请求方式
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
        
        if (file != null)
        {
            /**
             * 当文件不为空，把文件包装并且上传
             */
            Log.v("Dateout", "UploadUtil==>" + "开始上传文件" + file.getName());
            
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());
            InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1)
            {
                dos.write(bytes, 0, len);
            }
            is.close();
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int responseCode = conn.getResponseCode();
            Log.i("Dateout", "response code:" + responseCode);
            if (responseCode == 200)
            {
                Log.i("Dateout", "上传成功");
                return 0;// 上传成功
            }
            else
            {
                Log.i("Dateout", "完成失败");
                return 3;// 上传失败,出错
            }
        }
        else
        {
            return 4;// 上传失败,文件为空
        }
    }
}