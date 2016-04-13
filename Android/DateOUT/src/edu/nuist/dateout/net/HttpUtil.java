package edu.nuist.dateout.net;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.model.UserRandom;
import edu.nuist.dateout.util.JsonAssit;
import edu.nuist.dateout.value.AppConfig;

/**
 * 用于处理HTTP请求
 * 
 * @author Veayo
 *
 */
public class HttpUtil
{
    /**
     * 给定URL,用POST方式获取链接的返回值
     * 
     * @param url 有效的Http链接
     * @return 响应内容
     * @throws IOException,IllegalArgumentException
     * @throws ClientProtocolExceptio
     */
    public String getHttpPostResult(String url)
        throws ClientProtocolException, IOException, IllegalArgumentException, TimeoutException
    {
        // TODO 检测网络
        HttpPost httpPost = new HttpPost(url);
        String resultString = null;
        HttpClient httpClient = new DefaultHttpClient();
        // 请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 1000);
        // 读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20 * 1000);
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200)
        {
            resultString = EntityUtils.toString(response.getEntity());
        }
        
        if (resultString != null)
        {
            // resultString末尾会带有\r或\n这样的字符
            resultString = resultString.replaceAll("\r|\n", ""); // 去掉末尾的\r\n字符
        }
        return resultString;
    }
    
    /**
     * 输入文件名前缀,从服务端获取文件下载链接
     * 
     * @param filePrefix 文件名前缀,如IMAGEVCARD_user2
     * @return 返回带有文件名前缀的头像文件名称,如IMAGEVCARD_user2_2ab5839cc0b5f4bf21e5b00f71bca3fa.jpg,并且是最后修改过的 返回null表示服务端没有找到匹配到文件
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws ClientProtocolException
     * @throws TimeoutException
     */
    public String fetchDownoadLink(String filePrefix)
        throws ClientProtocolException, IllegalArgumentException, IOException, TimeoutException
    {
        String requestParam = "fileprefix=" + filePrefix;
        String url = AppConfig.URL_GETLINK_SERVLET + requestParam;
        String resultString = new HttpUtil().getHttpPostResult(url);
        return resultString;
    }
    
    /**
     * 输入文件名前缀,从服务端获取完整的带MD5的头像名
     * 
     * @param filePrefix 文件名前缀,如IMAGEHEAD_user2
     * @return 返回带有文件名前缀的头像文件名称,如IMAGEHEAD_user2_2ab5839cc0b5f4bf21e5b00f71bca3fa.jpg,并且是最后修改过的 返回null表示服务端没有找到匹配到文件
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws ClientProtocolException
     * @throws TimeoutException
     */
    public String fetchImageHeadFullName(String filePrefix)
        throws ClientProtocolException, IllegalArgumentException, IOException, TimeoutException
    {
        String requestParam = "fileprefix=" + filePrefix;
        String url = AppConfig.URL_FILENAME_SERVLET + requestParam;
        return new HttpUtil().getHttpPostResult(url);
    }
    
    /**
     * 给定UserLoc用户对象,联系服务端获取附近的人列表,并处理列表返回一组UserNear对象
     * 
     * @param user UserLoc对象,包含用户ID,经纬度
     * @return 返回一组附近的人UserNear对象,包含用户ID,经纬度,最近一次上传位置的时间,距离
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws ClientProtocolException
     * @throws TimeoutException
     */
    public List<UserNear> fetchUsersNear(UserLoc user)
        throws ClientProtocolException, IllegalArgumentException, IOException, TimeoutException
    {
        String resultString = fetchUsersNearAsJson(user);
        // 用JSON对结果字符串进行解析
        return new JsonAssit().jsonStr2UserNearList(resultString);
    }
    
    /**
     * 读取服务端的游戏配置
     * 
     * @param userId 用户id
     * @return 返回一个游戏配置对象
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws ClientProtocolException
     * @throws TimeoutException
     */
    public GameConfig fetchGameConfig(String userId)
        throws ClientProtocolException, IllegalArgumentException, IOException, TimeoutException
    {
        String requestParam = "userId=" + userId;
        String url = AppConfig.URL_GAMECONFIG_FETCH_SERVLET + requestParam;
        String resultString = new HttpUtil().getHttpPostResult(url);
        // 转换JSON为GameConfig对象
        return new JsonAssit().jsonStr2GameConfig(resultString);
    }
    
    /**
     * 以字符串形式从服务端读取附近的人列表
     * 
     * @param user UserLoc对象,包含用户位置信息
     * @return 返回Json字符串,需要解析
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws ClientProtocolException
     * @throws TimeoutException
     */
    private String fetchUsersNearAsJson(UserLoc user)
        throws ClientProtocolException, IllegalArgumentException, IOException, TimeoutException
    {
        String userId = user.getUserId();
        double locJingdu = user.getLocJingdu();
        double locWeidu = user.getLocWeidu();
        // 请求参数如userId=user2&locJingdu=115.55211&locWeidu=34.2623
        String requestParam = "userId=" + userId + "&locJingdu=" + locJingdu + "&locWeidu=" + locWeidu;
        
        String url = AppConfig.URL_USERNEAR_SERVLET + requestParam;
        
        return new HttpUtil().getHttpPostResult(url);
    }
    
    public List<UserRandom> fetchUsersRandom(UserLoc user)
        throws ClientProtocolException, IllegalArgumentException, IOException, TimeoutException
    {
        String resultString = fetchUsersRandomAsJson(user);
        // 用JSON对结果字符串进行解析
        return new JsonAssit().jsonStr2UsersRandomList(resultString);
    }
    
    /**
     * 以字符串形式从服务端读取随机选取的用户
     * 
     * @param user UserLoc对象,包含用户位置信息
     * @return 返回Json字符串,需要解析
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws ClientProtocolException
     * @throws TimeoutException
     */
    private String fetchUsersRandomAsJson(UserLoc user)
        throws ClientProtocolException, IllegalArgumentException, IOException, TimeoutException
    {
        String userId = user.getUserId();
        double locJingdu = user.getLocJingdu();
        double locWeidu = user.getLocWeidu();
        // 请求参数如userId=user2&locJingdu=115.55211&locWeidu=34.2623
        String requestParam = "userId=" + userId + "&locJingdu=" + locJingdu + "&locWeidu=" + locWeidu;
        String url = AppConfig.URL_RANDOMUSER_SERVLET + requestParam;
        return new HttpUtil().getHttpPostResult(url);
    }
}
