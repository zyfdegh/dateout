package edu.nuist.dateout.servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nuist.dateout.config.ServerConfig;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.util.FilePathAssit;

public class DownloadLinkServlet extends HttpServlet
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3911104093878031500L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        System.out.println("\n");
        System.out.println("====收到查询下载链接的请求====");
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("时间" + formatBuilder.format(new Date()));
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ip = request.getRemoteAddr();
        String port = String.valueOf(request.getRemotePort());
        System.out.println("请求来自" + ip + ":" + port);
        
        response.setContentType("text/plain");
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        String paramFilePrefix = new String(request.getParameter("fileprefix").getBytes("UTF-8"), "UTF-8").toString();
        System.out.println("请求参数:" + paramFilePrefix);
        String userId = paramFilePrefix.split("_")[1];
        
        if (paramFilePrefix.startsWith(ServerConfig.FILE_PREFIX_IMAGE_VCARD))
        {
            // 返回一组用逗号","隔开的VCARD照片下载链接
            List<String> downloadLinks = FilePathAssit.getVCardImageDownLinks(userId);
            String responeStr = "";
            for (int i = 0; i < downloadLinks.size(); i++)
            {
                responeStr += downloadLinks.get(i) + ",";
                System.out.println("VCard第" + i + 1 + "张图片下载连接" + downloadLinks.get(i));
            }
            out.println(responeStr);
            System.out.println("响应内容:" + responeStr);
        }
        else if(paramFilePrefix.startsWith(ServerConfig.FILE_PREFIX_IMAGE_HEAD)){
            //返回用户最新的头像下载链接
            String downloadLink = FilePathAssit.getHeadImageDownLink(userId);
            out.println(downloadLink);
            System.out.println("响应内容:" + downloadLink);
        }
        else if (paramFilePrefix.startsWith(ServerConfig.CMD_DELETE_ALL_MYVCARD_IMAGES))
        {
            // TODO 这里与获取文件下载链接无关,有时间的话,建立一个功能独立的Servlet用于处理此类请求
            // 删除某个用户的所有编辑资料页面的照片,即IMAGEVCARD_userId开头的图片
            String userIdWhoSentCmd = paramFilePrefix.split("_")[6];//得到用户ID
            System.out.println("请求删除用户"+userIdWhoSentCmd+"的所有VCARD照片");
            String filePrefix = ServerConfig.FILE_PREFIX_IMAGE_VCARD + userIdWhoSentCmd;
            String vcardImagePath = ServerConfig.DIR_DATAOUT_MAIN + ServerConfig.DIR_DATEOUT_VCARDIMAGES;
            boolean resultOk = FileAssit.deleteAllFilesWithPrefix(new File(vcardImagePath), filePrefix);
            if (resultOk)
            {
                System.out.println("已成功删除所有以IMAGEVCARD_" + userId + "开头的文件");
            }
            else
            {
                System.out.println("没有成功删除所有以IMAGEVCARD_" + userId + "开头的文件");
            }
            
        }
        System.out.println("======完成请求======");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doGet(request, response);
    }
    
}
