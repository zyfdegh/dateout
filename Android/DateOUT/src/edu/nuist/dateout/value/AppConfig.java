package edu.nuist.dateout.value;

import edu.nuist.dateout.app.DateoutApp;


public class AppConfig
{
    /**
     * 存储服务器链接配置
     */
    public static final String DEFAULT_SERVER_IP = "192.168.1.108";//运行Openfire的服务机IP
    public static final int DEFAULT_OPENFIRE_PORT = 5222;//Openfire端口号
//    public static final String DEFAULT_OPENFIRE_HOST = "lenovo";//Openfire主机名称
    
    
    /**
     * 用于配置程序本地文件目录,一般位于SD卡中.需要添加到CacheAssit初始化代码中才能创建目录
     * */
    //目录结构如下:
    //    ├─Cache
    //    │  └─Image
    //    │      └─Head
    //    └─Data
    //        ├─Audio
    //        │  ├─Received
    //        │  └─Sent
    //        ├─File
    //        │  ├─Received
    //        │  └─Sent
    //        └─Image
    //            ├─Receive
    //            ├─Sent
    //            └─VCard
    public static final String DIR_APP="Dateout/";
    public static final String DIR_APP_CACHE="Dateout/Cache/";
    public static final String DIR_APP_CACHE_IMAGE="Dateout/Cache/Image/";
    public static final String DIR_APP_CACHE_IMAGE_HEAD="Dateout/Cache/Image/Head/";
    public static final String DIR_APP_CACHE_IMAGE_GAME="Dateout/Cache/Image/Game/";
    public static final String DIR_APP_CACHE_IMAGELOADER="Dateout/Cache/ImageLoader";//这个末尾不要加'/'
    
    public static final String DIR_APP_DATA="Dateout/Data/";
    public static final String DIR_APP_DATA_AUDIO="Dateout/Data/Audio/";
    public static final String DIR_APP_DATA_AUDIO_RECEIVED="Dateout/Data/Audio/Received/";
    public static final String DIR_APP_DATA_AUDIO_SENT="Dateout/Data/Audio/Sent/";
    public static final String DIR_APP_DATA_FILE="Dateout/Data/File/";
    public static final String DIR_APP_DATA_FILE_RECEIVED="Dateout/Data/File/Received/";
    public static final String DIR_APP_DATA_FILE_SENT="Dateout/Data/File/Sent/";
    public static final String DIR_APP_DATA_IMAGE="Dateout/Data/Image/";
    public static final String DIR_APP_DATA_IMAGE_RECEIVED="Dateout/Data/Image/Received/";
    public static final String DIR_APP_DATA_IMAGE_SENT="Dateout/Data/Image/Sent/";
    public static final String DIR_APP_DATA_IMAGE_VCARD="Dateout/Data/Image/VCard/";
    
    /**
     * 相机拍摄的图片缓存路径
     */
     public static final String FILE_CAMERA_CACHE_IMAGE="Dateout/Cache/Image/camera_cache.jpg";//缓存相机拍摄的照片
     public static final String FILE_DOWNLOAD_CACHE_IMAGE="Dateout/Cache/Image/download_cache.jpg";//缓存下载到的照片
     public static final String FILE_SERIALIZE_CACHE_IMAGE="Dateout/Cache/Image/serialize_cache.png";//缓存由内存写入磁盘的图片
     public static final String FILE_GAME_CACHE_IMAGE="Dateout/Cache/Image/game_cache.png";//缓存由内存写入磁盘的图片
     public static final String FILE_CROP_CACHE_IMAGE="Dateout/Cache/Image/crop_cache.png";//缓存图片裁剪工具保存的裁剪结果图片
     
     
     /**
      * 用于存储服务器Servlet路径配置
      */
     public static final String URL_BASE="http://"+DateoutApp.getInstance().getServerIpInUse()+":8080/UploadServlet";
     public static final String URL_UPLOAD_SERVLET = URL_BASE+"/uploadfile?";
     public static final String URL_DOWNLOAD_SERVLET = URL_BASE+"/downloadfile?";
     public static final String URL_FILENAME_SERVLET = URL_BASE+"/getfilename?";
     public static final String URL_GETLINK_SERVLET = URL_BASE+"/getdownloadlink?";
     public static final String URL_USERNEAR_SERVLET = URL_BASE+"/getpeoplenear?";
     public static final String URL_RANDOMUSER_SERVLET = URL_BASE+"/getrandomusers?";
     public static final String URL_GAMECONFIG_FETCH_SERVLET = URL_BASE+"/getgameconfig?";
     public static final String URL_GAMECONFIG_SAVE_SERVLET = URL_BASE+"/savegameconfig?";
     
     /**
      * 保存用户之间距离临界值,即要返回多少米以内附近的人
      */
     public static final double DISTANCE_NEAR = 1000;
}
