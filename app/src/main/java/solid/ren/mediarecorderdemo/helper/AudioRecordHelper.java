package solid.ren.mediarecorderdemo.helper;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by _SOLID
 * Date:2017/5/18
 * Time:13:35
 * Desc:demo版（未完成）
 */

public class AudioRecordHelper {

    private final static int BUFFER_SIZE = 2048;
    private volatile boolean mIsRecording = false;
    private byte[] mBuffer = new byte[BUFFER_SIZE];
    private File mAudioFile;
    private FileOutputStream mFileOutputStream;
    private AudioRecord mAudioRecord;
    private final ExecutorService mExecutorService;
    private final Handler mMainHandler;


    public AudioRecordHelper(String savePath) throws IOException {
        mExecutorService = Executors.newSingleThreadExecutor();
        mMainHandler = new Handler(Looper.getMainLooper());
        mAudioFile = new File(savePath + File.separator + System.currentTimeMillis() + ".pcm");
        if (!mAudioFile.exists()) {
            mAudioFile.getParentFile().mkdirs();
        }
        mAudioFile.createNewFile();
        mFileOutputStream = new FileOutputStream(mAudioFile);
        int audioSource = MediaRecorder.AudioSource.MIC;
        int sampleRate = 44100;
        //单声道录制
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        mAudioRecord = new AudioRecord(audioSource
                , sampleRate
                , channelConfig
                , audioFormat
                , Math.max(minBufferSize, BUFFER_SIZE));
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    private boolean startRecord() {
        mAudioRecord.startRecording();

        try {
            while (mIsRecording) {
                int read = mAudioRecord.read(mBuffer, 0, BUFFER_SIZE);
                if (read > 0) {
                    mFileOutputStream.write(mBuffer, 0, read);
                } else {
                    return false;
                }
            }
            return stopRecord();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (mAudioRecord != null) {
                mAudioRecord.release();
            }
        }
    }

    private boolean stopRecord() {
        try {
            mIsRecording = false;
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
            mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void start() {
        mIsRecording = true;
        startRecord();
    }

    public void stop() {
        mIsRecording = false;
    }


}
