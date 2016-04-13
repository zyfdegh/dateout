package edu.nuist.dateout.config;

public class ServerConfig
{
    /**
     * 存储访问Servlet的链接
     */
    public static final String SERVER_IP = "192.168.1.100";
    
    public static final String URL_SERVLET_BASE = "http://" + SERVER_IP + ":8080/UploadServlet";
    
    public static final String URL_DOWNLOAD_SERVLET = URL_SERVLET_BASE + "/downloadfile?filename=";
    
    // public static final String URL_UPLOAD_SERVLET = URL_SERVLET_BASE+"/uploadfile?";
    // public static final String URL_FILENAME_SERVLET = URL_SERVLET_BASE+"/getfilename?fileprefix=";
    // public static final String URL_GAMECONFIG_SAVE_SERVLET = URL_SERVLET_BASE+"/savegameconfig?";
    // public static final String URL_GAMECONFIG_FETCH_SERVLET = URL_SERVLET_BASE+"/getgameconfig?";
    
    public static final String DIR_DATAOUT_MAIN =
        "C:\\Users\\Veayo\\Desktop\\DateoutWrkSpc\\DateoutServerPlugin\\WebContent\\";
    
    /**
     * 用于配置服务端上传的文件存放路径
     */
    public static final String DIR_DATEOUT_HEADIMAGES = "dateout\\data\\images_head\\";
    
    public static final String DIR_DATEOUT_SENTIMAGES = "dateout\\data\\images_sent\\";
    
    public static final String DIR_DATEOUT_GAMEIMAGES = "dateout\\data\\images_game\\";
    
    public static final String DIR_DATEOUT_VCARDIMAGES = "dateout\\data\\images_vcard\\";
    
    public static final String DIR_DATEOUT_FILES = "dateout\\data\\files\\";
    
    public static final String DIR_DATEOUT_AUDIO = "dateout\\data\\audio\\";
    
    public static final String DIR_DATEOUT_MISC = "dateout\\data\\misc\\";
    
    /**
     * 用于标记文件名开头
     */
    public static final String FILE_PREFIX_IMAGE_SENT = "IMAGESENT_";// 如IMAGESENT_0f6bb4fc558ebd2cd414885873d2081a.jpg
    
    public static final String FILE_PREFIX_IMAGE_HEAD = "IMAGEHEAD_";// 如IMAGEHEAD_0f6bb4fc558ebd2cd414885873d2081a.jpg
    
    public static final String FILE_PREFIX_IMAGE_VCARD = "IMAGEVCARD_";// 如IMAGEVCARD_user2_1_0f6bb4fc558ebd2cd414885873d2081a.jpg
    
    public static final String FILE_PREFIX_AUDIO_SENT = "AUDIOSENT_";// 如AUDIOSENT_0f6bb4fc558ebd2cd414885873d2081a.amr
    
    public static final String FILE_PREFIX_IMAGE_GAME = "IMAGEGAME_";// 如IMAGEGAME_user2.jpg
    
    /**
     * 用于标记上传的不同文件类型
     */
    public static final short FILE_TYPE_IMAGE_SENT = 0;
    
    public static final short FILE_TYPE_IMAGE_HEAD = 1;
    
    public static final short FILE_TYPE_IMAGE_VCARD = 2;
    
    public static final short FILE_TYPE_AUDIO_SENT = 3;
    
    public static final short FILE_TYPE_IMAGE_GAME = 4;
    
    public static final short FILE_TYPE_MISC = 5;
    
    /**
     * 标记客户端发来的指令
     */
    public static final String CMD_DELETE_ALL_MYVCARD_IMAGES = "CMD_DELETE_ALL_MY_VCARD_IMAGES_";
    
    /**
     * 保存用户之间距离临界值,即要返回多少米以内附近的人
     */
    public static final double DISTANCE_NEAR = 1000;
}
