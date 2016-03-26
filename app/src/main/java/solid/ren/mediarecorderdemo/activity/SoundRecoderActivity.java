package solid.ren.mediarecorderdemo.activity;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import solid.ren.mediarecorderdemo.R;
import solid.ren.mediarecorderdemo.helper.MediaPlayerHelper;
import solid.ren.mediarecorderdemo.helper.MediaRecorderHelper;
import solid.ren.mediarecorderdemo.view.WaveView;

/**
 * Created by _SOLID
 * Date:2016/3/22
 * Time:14:32
 */
public class SoundRecoderActivity extends AppCompatActivity implements View.OnClickListener, Runnable {


    private static final int ACTION_RECORDING = 1;
    private static final int ACTION_NORMAL = 0;
    private static final int ACTION_COMMPLETE = 2;
    private static final int ACTION_PLAYING = 3;
    private static final int ACTION_PAUSE = 4;


    private TextView mTvRecorderTime;
    private Typeface mTypeFace;
    private ImageView mIvAction;
    private WaveView mWaveView;
    private RelativeLayout mRlBottom;
    private ImageView mIvSave;
    private ImageView mIvDelete;

    private int mCurrentActionState = ACTION_NORMAL;
    private MediaRecorderHelper mMediaRecorderHelper;
    private Thread mCountTimeThread;
    private int mTotalSecond = 0;
    private boolean mIsRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recoder);
        mMediaRecorderHelper = new MediaRecorderHelper(getRecorderFilePath());

        mIsRecorder = false;
        initView();
    }

    public String getRecorderFilePath() {
        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = getExternalCacheDir().getAbsolutePath();
        } else {
            path = getCacheDir().getAbsolutePath();

        }
        return path + File.separator + "Recorder";
    }

    private void initView() {
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/AGENCYR.TTF");
        mTvRecorderTime = (TextView) findViewById(R.id.tv_recoder_time);
        mTvRecorderTime.setTypeface(mTypeFace);

        mIvAction = (ImageView) findViewById(R.id.iv_action);
        mIvAction.setOnClickListener(this);
        mWaveView = (WaveView) findViewById(R.id.waveView);
        mWaveView.setVisibility(View.INVISIBLE);
        mRlBottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        mRlBottom.setVisibility(View.GONE);
        mIvDelete = (ImageView) findViewById(R.id.iv_delete);
        mIvSave = (ImageView) findViewById(R.id.iv_save);
        mIvDelete.setOnClickListener(this);
        mIvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_action:
                switchActionState();
                break;
            case R.id.iv_delete:
                changeToNormalState();
                File file = new File(mMediaRecorderHelper.getCurrentFilePath());
                file.delete();
                Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();


                break;
            case R.id.iv_save:
                changeToNormalState();
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 切换ACTION状态
     */
    private void switchActionState() {
        mIsRecorder = false;
        if (mCurrentActionState == ACTION_NORMAL) {
            mCurrentActionState = ACTION_RECORDING;
            mIvAction.setImageResource(R.drawable.pause);

            //开始录音
            mMediaRecorderHelper.startRecord();
            mWaveView.setVisibility(View.VISIBLE);
            mIsRecorder = true;
            //开启计时线程
            mCountTimeThread = new Thread(this);
            mCountTimeThread.start();

        } else if (mCurrentActionState == ACTION_RECORDING) {//录制中
            mCurrentActionState = ACTION_COMMPLETE;
            mIvAction.setImageResource(R.drawable.icon_audio_state_uploaded);
            //停止录音
            mMediaRecorderHelper.stopAndRelease();
            mRlBottom.setVisibility(View.VISIBLE);
            mWaveView.setVisibility(View.INVISIBLE);

        } else if (mCurrentActionState == ACTION_COMMPLETE) {//录制完成
            mCurrentActionState = ACTION_PLAYING;
            mIvAction.setImageResource(R.drawable.icon_audio_state_uploaded_play);

            //播放录音
            MediaPlayerHelper.playSound(mMediaRecorderHelper.getCurrentFilePath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //当播放完了之后切换到录制完成的状态
                    mCurrentActionState = ACTION_COMMPLETE;
                    mIvAction.setImageResource(R.drawable.icon_audio_state_uploaded);
                }
            });

        } else if (mCurrentActionState == ACTION_PLAYING) {//播放中
            mCurrentActionState = ACTION_PAUSE;
            mIvAction.setImageResource(R.drawable.icon_audio_state_uploaded);
            //暂停播放
            MediaPlayerHelper.pause();
        } else if (mCurrentActionState == ACTION_PAUSE) {//暂停
            mCurrentActionState = ACTION_PLAYING;
            mIvAction.setImageResource(R.drawable.icon_audio_state_uploaded_play);
            //继续播放
            MediaPlayerHelper.resume();
        }
    }


    //恢复成未录制状态
    public void changeToNormalState() {

        MediaPlayerHelper.realese();
        mCurrentActionState = ACTION_NORMAL;
        mIvAction.setImageResource(R.drawable.btn_clue_audio);
        mTotalSecond = 0;
        mTvRecorderTime.setText("00:00");
        mRlBottom.setVisibility(View.GONE);
    }

    public String getShowTime(int countTime) {

        String result = "";
        if (countTime < 10)
            result = "00:0" + countTime;
        else if (countTime < 60)
            result = "00:" + countTime;
        else {
            int minute = countTime / 60;
            int mod = countTime % 60;
            if (minute < 10) result += "0" + minute + ":";
            else {
                result += minute + ":";
            }
            if (mod < 10) result += "0" + mod;
            else {
                result += mod;
            }

        }
        return result;
    }

    @Override
    public void run() {
        while (mIsRecorder) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mTotalSecond++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvRecorderTime.setText(getShowTime(mTotalSecond));
                }
            });
        }
    }
}
