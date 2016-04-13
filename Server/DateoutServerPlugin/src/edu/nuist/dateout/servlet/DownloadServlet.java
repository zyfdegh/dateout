package edu.nuist.dateout.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nuist.dateout.config.ServerConfig;
import edu.nuist.dateout.model.UploadedFileModel;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.util.FilePathAssit;

/**
 * 用于处理文件的下载
 * 
 * @author Veayo
 *
 */
public class DownloadServlet extends HttpServlet
{
    private static final long serialVersionUID = -4053023753826332864L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        System.out.println("\n");
        System.out.println("====收到下载文件请求====");
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("时间" + formatBuilder.format(new Date()));
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();
        
        String ip = request.getRemoteAddr();
        String port = String.valueOf(request.getRemotePort());
        System.out.println("请求来自" + ip + ":" + port);
        
        String paramFileName = new String(request.getParameter("filename").getBytes("UTF-8"), "UTF-8").toString();
        System.out.println("参数filename:" + paramFileName);
        
        
        //处理请求参数
        File fileToDownload=null;
        if (paramFileName.matches("IMAGEHEAD_*_DIRECTDOWN"))
        {
            System.out.println("请求直接下载用户头像");
            String userId = paramFileName.split("_")[1];
            System.out.println("userId:" + userId);
            fileToDownload = FilePathAssit.getHeadImage(userId);
            //fileSavePath = ServerConfig.DIR_DATAOUT_MAIN + fileRelativePath;
        }
        else
        {
            UploadedFileModel ufm=FileAssit.getFileSavePath(paramFileName);
            String fileRelativePath = ufm.getFileSavePath();
            fileToDownload=new File(ServerConfig.DIR_DATAOUT_MAIN + fileRelativePath);
        }
        
        
        //响应下载请求
        if (fileToDownload!=null)
        {
            String fileSavePath=fileToDownload.getAbsolutePath();
            System.out.println("将到这里查找文件:" + fileSavePath);
            
            if (!fileToDownload.exists())
            {
                out.print("File not found");
                System.out.println("文件不存在!");
            }else {
             // 读取文件流
                FileInputStream fis = new FileInputStream(fileToDownload);
                // 下载文件
                // 设置响应头和下载保存的文件名
                if (fileSavePath != null && fileSavePath.length() > 0)
                {
                    // 设置响应为下载文件
                    response.setContentType("application/x-msdownload");
                    // 设置响应文件名为路径
                    response.setHeader("Content-Disposition",
                        "attachment; filename=" + new String(paramFileName.getBytes("utf-8"), "utf-8") + "");
                    if (fis != null)
                    {
                        int filelen = fis.available();
                        // 文件太大时内存不能一次读出,要循环
                        byte a[] = new byte[filelen];
                        fis.read(a);
                        out.write(a);
                    }
                    fis.close();
                    out.close();
                    System.out.println("文件下载成功");
                }
            }
        }
        System.out.println("======完成请求======");
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC -//W3C//DTD HTML 4.01 Transitional//EN>");
        out.println("<HTML>");
        out.println(" <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        out.println(" <BODY>");
        out.print(" This is ");
        out.print(this.getClass().getName());
        out.println(", using the POST method");
        out.println(" </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }
}
