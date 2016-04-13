package edu.nuist.dateout.value;

/**
 * @author Veayo
 * @date 2015/03/29 静态变量托管器。轻量级类,用于存储一些类之间传递的变量和常量
 */
public class VariableHolder
{
    
    // ===========================常量======================
    /**
     * 用于标记登录结果信息
     */
    public static final int REGIST_SUCCESS = 0;// 注册成功
    
    public static final int REGIST_ERR_ACCOUNT_EXIST = 1;// 账号已存在
    
    public static final int REGIST_ERR_CONNECT_FAILURE = 2;// 连接失败
    
    public static final int REGIST_ERR_UNKNOWN_FAILURE = 3;// 未知错误
    
    public static final int REGIST_ERR_SERVER_NO_RESPONSE = 4;// 未知错误
    
    /**
     * 标记登录错误信息
     * */
    public static final int LOGIN_SECCESS = 0;// 成功
    
    public static final int LOGIN_ERROR_ACCOUNT_NOTPASS = 1;// 账号或者密码错误
    
    public static final int LOGIN_ERROR_SERVER_UNAVAILABLE = 2;// 无法连接到服务器
    
    public static final int LOGIN_ERROR_OTHER = 3;// 其他原因错误
    
    public static final int LOGIN_TIME_OUT = 4;// 其他原因错误
    
    /**
     * 用于标记用户在线状态
     */
    public static final short STAT_OFFLINE = 0;
    
    public static final short STAT_ONLINE = 1;
    
    public static final short STAT_NOT_EXIST = 2;
    
    /**
     * 用于标记图片缓存结果
     */
    public static final short CACHE_SUCCESS = 0;
    
    public static final short CACHE_FILE_EXIST = 1;
    
    public static final short CACHE_ERR_FILE_CREATE = 2;
    
    public static final short CACHE_ERR_FILE_WRITE = 3;
    
    /**
     * 用于标识VCard中的自定义字段名
     */
    public static final String VCARD_FIELD_GENDER = "GENDER";// // 性别
    
    public static final String VCARD_FIELD_MOODIE = "MOODIE";// 个性签名
    
    public static final String VCARD_FIELD_BIRTHDAY = "BTHDAY";// 出生年月日
    
    public static final String VCARD_FIELD_INTEREST = "HOBBY";// 兴趣爱好
    
    public static final String VCARD_FIELD_SINGLESTAT = "SINGLE";// 情感状态
    
    public static final String VCARD_FIELD_JOB = "JOB";// 情感状态
    
    public static final String VCARD_FIELD_WEIGHT = "WEIGHT";// 情感状态
    
    public static final String VCARD_FIELD_HEIGHT = "HEIGHT";// 情感状态
    
    public static final String VCARD_FIELD_NICKNM = "NICKNM";// 情感状态
    
    public static final String VCARD_FIELD_CITY = "CITY";// 情感状态
    
    public static final String VCARD_FIELD_EMAIL = "EMAIL";// 情感状态
    
    /**
     * 用于标记Roster事件类型
     */
    public static final short ROSTER_PRESENCE_CHANGED = 0;
    
    public static final short ROSTER_ENTRIES_UPDATED = 1;
    
    public static final short ROSTER_ENTRIES_ADDED = 2;
    
    public static final short ROSTER_ENTRIES_DELETED = 3;
    
    public static final short ROSTER_PRESENCE_SUBSCRIBE = 4;
    
    /**
     * 用于标记照片选取onActivityResult的requestCode值
     */
    public static final short REQUEST_CODE_PHOTO_FROM_CAMERA = 0;
    
    public static final short REQUEST_CODE_PHOTO_FROM_GALLERY = 1;
    
    public static final short REQUEST_CODE_PHOTO_ZOOM_FINISH_SMALL = 2;
    
    public static final short REQUEST_CODE_PHOTO_ZOOM_FINISH_LARGE = 3;
    
    /**
     * 用于标识图片 语音 文件信息的信息头 注意:图片 语音 文件传送的方式和普通文本传送方式不同,他们是用HTTP协议发送到服务器的,但有个文本消息通过传统方式告知收方有人发来了文件
     * 后面加个变量名的MD5作为随机数,主要用于防止用户偶然输入标识导致消息类型识别错误
     */
    public static final String MSG_FLAG_AUDIO = "AUDIO_E8A103B7EA52E603";
    
    public static final String MSG_FLAG_IMAGE = "IMAGE_05898E661036E433";
    
    public static final String MSF_FLAG_LOCATION = "LOCATION_3DD675D5FBE1EF03";
    
    public static final String MSG_FLAG_FILE = "FILES_8F7AADF602DCBF35";
    
    /**
     * 用于标识文件类型的文件名前缀
     */
    public static final String FILE_PREFIX_IMAGE_SENT = "IMAGESENT_";// 如IMAGESENT_0f6bb4fc558ebd2cd414885873d2081a.jpg
    
    public static final String FILE_PREFIX_IMAGE_HEAD = "IMAGEHEAD_";// 如IMAGEHEAD_0f6bb4fc558ebd2cd414885873d2081a.jpg
    
    public static final String FILE_PREFIX_AUDIO_SENT = "AUDIOSENT_";// 如AUDIOSENT_0f6bb4fc558ebd2cd414885873d2081a.amr
    
    public static final String FILE_PREFIX_IMAGE_VCARD = "IMAGEVCARD_";// 如IMAGEVCARD_user2_1_0f6bb4fc558ebd2cd414885873d2081a.jpg
    
    public static final String FILE_PREFIX_IMAGE_GAME = "IMAGEGAME_";// 如IMAGEGAME_user2.jpg
    
    /**
     * 用于标记消息类型
     */
    public static final short MSG_TYPE_NOTSET = 0;
    
    public static final short MSG_TYPE_TEXT = 1;
    
    public static final short MSG_TYPE_AUDIO = 2;
    
    public static final short MSG_TYPE_IMAGE = 3;
    
    public static final short MSG_TYPE_FILE = 4;
    
    public static final short MSF_TYPE_EMOJI=5;
    
    /**
     * 用于标记文件拷贝结果
     */
    public static final short COPY_RESULT_SUCCESS = 0;// 拷贝完成
    
    public static final short COPY_RESULT_EXIST = 1;// 文件已经存在
    
    public static final short COPY_RESULT_FAIL = 2;// 文件已经存在
    
    /**
     * 用于标记SD卡挂载状态
     */
    // public static final short SDCARD_STAT_OK=;
    
    /**
     * 标记客户端发到服务端的指令
     */
    public static final String CMD_DELETE_ALL_MYVCARD_IMAGES = "CMD_DELETE_ALL_MY_VCARD_IMAGES_";
}
