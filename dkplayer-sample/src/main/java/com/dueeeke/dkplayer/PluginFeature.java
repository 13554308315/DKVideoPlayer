package com.dueeeke.dkplayer;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dueeeke.dkplayer.activity.api.PlayerActivity;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.videoplayer.BuildConfig;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;

import org.json.JSONArray;

import java.lang.reflect.Field;

import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;
public class PluginFeature extends StandardFeature {
    private static JSONArray tempArray;
    private static IWebview iWebview;

    /**
     *
     * @param iWebview  uniapp传过来的webview参数
     * @param array  uniapp传过来的CallBackID和参数
     */
    public void startLoginActivity(IWebview iWebview, JSONArray array) {
        this.tempArray = array;
        this.iWebview = iWebview;
        String id = tempArray.optString(0);
        String url = tempArray.optString(1);
        String isBuy = tempArray.optString(2);
        Log.e("startLoginActivity",url);

        //播放器配置，注意：此为全局配置，按需开启
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setLogEnabled(BuildConfig.DEBUG)//调试的时候请打开日志，方便排错
                .setPlayerFactory(IjkPlayerFactory.create())
//                .setPlayerFactory(ExoMediaPlayerFactory.create())
//                .setRenderViewFactory(SurfaceRenderViewFactory.create())
//                .setEnableOrientation(true)
//                .setEnableAudioFocus(false)
//                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
//                .setAdaptCutout(false)
//                .setPlayOnMobileNetwork(true)
//                .setProgressManager(new ProgressManagerImpl())
                .build());

        //切换播放核心，不推荐这么做，我这么写只是为了方便测试
        VideoViewConfig config = VideoViewManager.getConfig();
        try {
            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            PlayerFactory playerFactory = IjkPlayerFactory.create();
//            setTitle(getResources().getString(R.string.app_name) + " (IjkPlayer)");

//                    playerFactory = ExoMediaPlayerFactory.create();
//                    setTitle(getResources().getString(R.string.app_name) + " (ExoPlayer)");
//
//                    playerFactory = AndroidMediaPlayerFactory.create();
//                    setTitle(getResources().getString(R.string.app_name) + " (MediaPlayer)");
            mPlayerFactoryField.set(config, playerFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }



        //页面跳转
        Context context = getDPluginContext();
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IntentKeys.URL, url);
        intent.putExtra(IntentKeys.IS_LIVE, false);
        intent.putExtra(IntentKeys.TITLE, "点播");
        intent.putExtra("isBuy",isBuy);
        context.startActivity(intent);

//            PlayerActivity.start(context, url, "点播", false);



//        //给uniapp传值
//        JSUtil.execCallback(iWebview,CallBackID,"",JSUtil.OK,false);
    }
}
