package edu.nuist.dateout.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.util.JsonAssit;

public class GameConfigFetchServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -761246603196742715L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        System.out.println("\n");
        System.out.println("====收到查询游戏配置请求====");
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("时间"+formatBuilder.format(new Date()));
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ip = request.getRemoteAddr();
        String port = String.valueOf(request.getRemotePort());
        System.out.println("请求来自"+ip+":"+port);
        
        response.setContentType("text/plain");
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        String paramUserId = new String(request.getParameter("userId").getBytes("UTF-8"), "UTF-8").toString();
        System.out.println("请求参数:" + paramUserId);
        
        //查表
        GameConfig config=new DBAssit().selectGameConfigFromTableGame(paramUserId);
        //转为Json字符串
        String jsonStr=new JsonAssit().gameConfig2JsonStr(config);
        out.print(jsonStr);
        System.out.println("响应内容:"+jsonStr);
        System.out.println("======完成请求======");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doGet(request, response);
    }
}
