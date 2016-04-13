package edu.nuist.dateout.servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nuist.dateout.config.ServerConfig;
import edu.nuist.dateout.util.FilePathAssit;

/**
 * 
 * @author Veayo
 *
 */
public class FileNameServlet extends HttpServlet
{
    private static final long serialVersionUID = 5719260326793618829L;
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        System.out.println("\n");
        System.out.println("====收到查询文件名请求====");
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("时间"+formatBuilder.format(new Date()));
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ip = request.getRemoteAddr();
        String port = String.valueOf(request.getRemotePort());
        System.out.println("请求来自"+ip+":"+port);
        
        response.setContentType("text/plain");
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        String paramFilePrefix = new String(request.getParameter("fileprefix").getBytes("UTF-8"), "UTF-8").toString();
        System.out.println("请求参数:" + paramFilePrefix);
        String userId=paramFilePrefix.split("_")[1];
        
        if (paramFilePrefix.startsWith(ServerConfig.FILE_PREFIX_IMAGE_HEAD))
        {
            //格式为IMAGEHEAD_userId这样的
            System.out.println("请求缓存头像名");
            //得到用户名
            System.out.println("userId:"+userId);
            File headImageLastCached= FilePathAssit.getHeadImage(userId);
            if (headImageLastCached==null)
            {
                System.out.println("找不到缓存头像 "+paramFilePrefix+"...");
                return;
            }else {
                System.out.println("找到缓存头像\n路径:"+headImageLastCached.getAbsolutePath());
                //返回文件名
                out.println(headImageLastCached.getName());
                System.out.println("响应内容:"+headImageLastCached.getName());
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
