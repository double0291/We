package com.dreamland.util;

public class Constants {
	/**
	 * 私有构造函数，防止被初始化
	 */
	private Constants() {
	}

	// 应用名
	public static final String APP_NAME = "We";

	// 主页
	public static enum HOME_CARD {
		VIDEO, GAME, MINE
	}

	/*
	 * 网络请求命令
	 */
	public static enum HttpCmd {
        HOME_PAGE,
        GET_VIDEO_LIST,
        GET_VIDEO_INFO,
        GET_GAME_LIST,
        GET_GAME_INFO,
        NULL;
	}

    /*
     * 网络链接
     */
    public static final String URL_BASE = "http://112.74.83.194";
    // 主页
    public static final String URL_HOMEPAGE = URL_BASE + "/app/homepage/homepage_get";
    // 视频列表
    public static final String URL_GET_VIDEO_LIST = URL_BASE + "/app/video/video_list";
    // 游戏列表
    public static final String URL_GET_GAME_LIST = URL_BASE + "/app/game/game_list";
    // 视频详情
    public static final String URL_GET_VIDEO_INFO = URL_BASE + "/app/video/video_get";
    // 游戏详情
    public static final String URL_GET_GAME_INFO = URL_BASE + "/app/game/game_get";

}
