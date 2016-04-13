package edu.nuist.dateout.tab;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nuist.dateout.tab1.activity.AboutUsActivity;
import com.nuist.dateout.tab1.activity.ClearTrashActivity;
import com.nuist.dateout.tab1.activity.SetAppActivity;
import com.nuist.dateout.tab4.activity.EditVcardActivity;
import com.nuist.dateout.tab4.activity.ExitApplicationActivity;
import com.nuist.dateout.tab4.activity.GameSettingActivity;
import com.nuist.dateout.tab4.activity.PersonalCardActivity;
import com.nuist.dateout.tab4.activity.ShakeActivity;

import de.greenrobot.event.EventBus;
import com.nuist.dateout.R;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.DownloadResult;
import edu.nuist.dateout.task.DownloadAndCacheTask;
import edu.nuist.dateout.task.FetchFileNameTask;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.value.VariableHolder;

public class SettingManager extends Fragment implements OnClickListener
{
    
    private View exitView;
    
    private View personalInfo;
    
    private ImageView headImage;
    
    private DateoutApp app;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_4, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        app = DateoutApp.getInstance();
        
        headImage = (ImageView)getActivity().findViewById(R.id.iv_setting_head);
        headImage.setImageURI(app.getLoginUser().getHeadImageUri());// 设置用户头像
        // 从服务器检测是否有新的头像
        String loginId = app.getLoginUser().getUserId();
        new FetchFileNameTask(VariableHolder.FILE_PREFIX_IMAGE_HEAD + loginId, fileNameFetchHandler).execute();
        
        exitView = getActivity().findViewById(R.id.lo_exit);
        personalInfo = getActivity().findViewById(R.id.lo_PersonalInfo);
        personalInfo.setOnClickListener(this);
        exitView.setOnClickListener(this);
        
        View shakeView = getActivity().findViewById(R.id.lo_setting_shake);
        shakeView.setOnClickListener(this);
        
        View gameView = getActivity().findViewById(R.id.lo_setting_game);
        gameView.setOnClickListener(this);
        
        View aboutUsView = getActivity().findViewById(R.id.lo_setting_about_us);
        aboutUsView.setOnClickListener(this);
        
        View vcardView = getActivity().findViewById(R.id.lo_setting_vcard);
        vcardView.setOnClickListener(this);
        
        View setAppView = getActivity().findViewById(R.id.lo_setting_set);
        setAppView.setOnClickListener(this);
        
        View clearTrashView =  getActivity().findViewById(R.id.lo_setting_clear_trash);
        clearTrashView.setOnClickListener(this);
        
        //注册EventBus来监听从EditVcardActivity发来的新的头像Uri
        EventBus.getDefault().register(this);
    }
    
    // TODO 这样嵌套真的不会有问题么=.=发现安卓搞的UI线程好复杂
    private Handler fileDownHandler = new Handler()
    {
        @Override
        public void handleMessage(Message osMsg)
        {
            super.handleMessage(osMsg);
            switch (osMsg.what)
            {
                case 1:
                    final DownloadResult result = (DownloadResult)osMsg.obj;
                    if (result.isDownloadSuccess())
                    {
                        Log.v("Dateout", "SettingManager==>" + "头像下载成功");
                        // 下载成功,显示图像
                        // 通过文件路径获取用户Id
                        // String fileName = new File(result.getFilePath()).getName();
                        // refreshHeadImage(userId,Uri.parse(result.getFilePath()));
                        Uri headImageUri=Uri.parse(result.getFilePath());
                        headImage.setImageURI(headImageUri);
                        app.getLoginUser().setHeadImageUri(headImageUri);
                        Log.v("Dateout", "SettingManager==>" + "使用了服务端头像文件");
                        //缓存到本地
                    }
                    else
                    {
                        // 下载失败
                        Log.v("Dateout", "SettingManager==>" + "头像下载失败");
                    }
                    break;
                default:
                    break;
            }
        }
    };
    
    // 用于处理用户头像文件名的下载
    private Handler fileNameFetchHandler = new Handler()
    {
        @Override
        public void handleMessage(Message osMsg)
        {
            super.handleMessage(osMsg);
            switch (osMsg.what)
            {
                case 1:
                    String fileName = (String)osMsg.obj;
                    if (fileName == null || fileName.equals(""))
                    {
                        // 找不到头像
                        Log.v("Dateout", "SettingManager==>" + "服务器报告不存在用户头像");
                    }
                    else
                    {
                        Log.v("Dateout", "SettingManager==>" + "找到服务端用户最新头像文件名");
                        // 存在头像
                        // 检测本地缓存
                        File file = new File(FilePathTool.getHeadCachedPath(fileName));
                        // CacheAssit().getLoginHeadImageFromCache(userId);
                        if (file.exists() && file.canRead())
                        {
                            Log.v("Dateout", "SettingManager==>" + "服务端文件名与本地缓存文件名一致");
                            // 本地缓存已经存在最新头像
                            // 显示为本地缓存
                            // refreshHeadImage(userId,Uri.parse(file.getAbsolutePath()));
                            headImage.setImageURI(Uri.parse(file.getAbsolutePath()));
                            Log.v("Dateout", "SettingManager==>" + "使用了本地缓存头像");
                        }
                        else
                        {
                            Log.v("Dateout", "SettingManager==>" + "服务端文件名与本地缓存文件名不一致");
                            // 下载头像并缓存头像
                            new DownloadAndCacheTask(fileName, FilePathTool.getHeadCachedPath(fileName),
                                fileDownHandler).execute();
                        }
                    }
                    break;
                
                default:
                    break;
            }
        }
    };
    
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.lo_PersonalInfo:
                Intent intent5 = new Intent(getActivity(), PersonalCardActivity.class);
                startActivity(intent5);
                break;
            case R.id.lo_exit:
                Intent intent2 = new Intent(getActivity(), ExitApplicationActivity.class);
                startActivity(intent2);
                break;
            case R.id.lo_setting_shake:
                Intent intent3 = new Intent(getActivity(), ShakeActivity.class);
                startActivity(intent3);
                break;
            case R.id.lo_setting_game:
                Intent intent4 = new Intent(getActivity(), GameSettingActivity.class);
                startActivity(intent4);
                break;
            case R.id.lo_setting_vcard:
                Intent intent = new Intent(getActivity(), EditVcardActivity.class);
                startActivity(intent);
                break;
            case R.id.lo_setting_about_us:
            	startActivity(new Intent(getActivity(), AboutUsActivity.class));
            	break;
            case R.id.lo_setting_set:
            	startActivity(new Intent(getActivity(), SetAppActivity.class));
            	break;
            case R.id.lo_setting_clear_trash:
            	startActivity(new Intent(getActivity(), ClearTrashActivity.class));
            	break;
            default:
                break;
        }
    }
    

	/**
     * 使用onEvent来接收从EditVcardActivity类中传来的EventBus事件
     * @param msgPack 收到的消息包
     */
    public void onEvent(Uri uri)
    {
        if (uri!=null&&new File(uri.toString()).exists())
        {
            headImage.setImageURI(uri);
        }
    }
}
