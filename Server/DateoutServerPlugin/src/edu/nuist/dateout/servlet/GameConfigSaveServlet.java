package edu.nuist.dateout.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nuist.dateout.config.ServerConfig;
import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.model.GameConfig;

/**
 * 用于存储用户游戏配置信息,如图片下载链接,难度,游戏超时
 * @author Veayo
 *
 */
public class GameConfigSaveServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 5841798248500440096L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        System.out.println("\n");
        System.out.println("====收到游戏设置保存请求====");
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("时间" + formatBuilder.format(new Date()));
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ip = request.getRemoteAddr();
        String port = String.valueOf(request.getRemotePort());
        System.out.println("请求来自" + ip + ":" + port);
        
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        String paramUserId = new String(request.getParameter("userId").getBytes("UTF-8"), "UTF-8").toString();
        String paramPicName = new String(request.getParameter("picName").getBytes("UTF-8"), "UTF-8").toString();
        String paramTime = new String(request.getParameter("time").getBytes("UTF-8"), "UTF-8").toString();
        String paramDifficulty = new String(request.getParameter("difficulty").getBytes("UTF-8"), "UTF-8").toString();
        System.out.println("请求参数:" + paramUserId + "," + paramPicName + "," + paramTime + "," + paramDifficulty);
        
        // 入库
        String picDownUrl = ServerConfig.URL_DOWNLOAD_SERVLET + paramPicName;
        GameConfig config =
            new GameConfig(paramUserId, picDownUrl, Integer.valueOf(paramTime), Integer.valueOf(paramDifficulty));
        boolean insertOk = new DBAssit().insertIntoTableGame(config);
        if (insertOk)
        {
            System.out.println("已保存用户" + paramUserId + "的游戏配置信息");
        }
        else
        {
            System.out.println("保存用户" + paramUserId + "的游戏配置信息失败=.=");
        }
        out.print("Done!");
        System.out.println("======完成请求======");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doGet(request, response);
    }
}
