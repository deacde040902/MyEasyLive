package com.easylive.base.entity.constants;

public class Constants {

    public static final String REDIS_KEY_DOWNLOAD_CODE = "easylive:download:";

    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT= 6;

    public static final Integer REDIS_TIME_1MIN = 60;
    // Token 请求头
    public static final String REQUEST_HEADER_TOKEN = "token";
    // Redis Token Key
    public static final String REDIS_KEY_WS_TOKEN = "ws_token:";
    // 登录Token Redis前缀
    public static final String REDIS_KEY_TOKEN = "easylive:login:token:";
    // 5分钟过期时间（用于邮箱验证码）
    public static final Integer REDIS_TIME_5MIN = REDIS_TIME_1MIN * 5;
    // 邮箱验证码Redis前缀
    public static final String REDIS_KEY_EMAIL_CODE = "easylive:email:code:";
    // 邮箱发送频率限制Redis前缀（防刷）
    public static final String REDIS_KEY_EMAIL_SEND_LIMIT = "easylive:email:limit:";

    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_TIME_1MIN * 60 * 24;

    public static final Integer LENGTH_11 = 11;

    public static final Integer LENGTH_20 = 20;

    public static final Integer REDIS_KEY_TOKEN_EXPIRES = REDIS_KEY_EXPIRES_DAY * 2;

    public static final String REDIS_KEY_CHECK_CODE = "easylive:checkcode:";

    public static final String REDIS_KEY_WS_HEART_BEAT = "easylive:ws:user:heartbeat:";

    public static final String REDIS_KEY_WS_TOKEN_USERID = "easylive:ws:token:userid:";

    public static final String REDIS_KEY_SYS_SETTING = "easylive:syssetting:";

    public static final String FILE_FOLDER_FILE = "/file";

    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";

    public static final String COVER_IMAGE_SUFFIX = "_cover.png";

    public static final String IMAGE_SUFFIX = ".png";

    public static final String REGEX_PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,18}$";

    public static final String APPLY_INFO_TEMPLATE = "我是%s";

    public static final String REDIS_KEY_CONTACT_USER = "contact:user:";

    public static final String REDIS_KEY_CONTACT_LIST = "contact:list:";

    public static final String APP_UPDATE_FOLDER = "/app/";

    public static final String APP_EXE_SUFFIX = "/.exe";

    public static final String APP_NAME = "EasyChatSetup";

    public static final String REDIS_KEY_USER_CONTACT = "easylive:ws:user:contact:";

    public static final Long MillisSECONDS_3DAY_AGO = 3 * 24 * 60 * 1000L;

    public static final String[] IMAGE_SUFFIX_LIST = new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".webp"};

    public static final String[] VIDEO_SUFFIX_LIST = new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"};

    public static final Long FILE_SIZE_MB = 1024 * 1024L;

    public static final Long FILE_SIZE_GB = 1024 * 1024 * 1024L;

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final int EMAIL_SEND_LIMIT_SECONDS = 60;

    public static final int RECALL_MESSAGE_TIMEOUT = 300; // 撤回时效（秒） 5分钟


}