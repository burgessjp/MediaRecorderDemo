package solid.ren.mediarecorderdemo.helper;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by _SOLID
 * Date:2017/5/18
 * Time:14:58
 * Desc:demo版本（未完成）
 */

public class AudioTrackHelper {
    private final static int BUFFER_SIZE = 2048;
    private AudioTrack mAudioTrack;
    private byte[] mBuffer = new byte[BUFFER_SIZE];

    public AudioTrackHelper(File file) {
        int steamType = AudioManager.STREAM_MUSIC;
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mode = AudioTrack.MODE_STREAM;
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        mAudioTrack = new AudioTrack(steamType
                , sampleRate
                , channelConfig, audioFormat
                , Math.max(BUFFER_SIZE, minBufferSize), mode);
        mAudioTrack.play();
        FileInputStream inputStream = null;
        try {
            //循环读取数据，然后播放
            inputStream = new FileInputStream(file);
            int read;
            while ((read = inputStream.read(mBuffer)) > 0) {
                int ret = mAudioTrack.write(mBuffer, 0, read);
                switch (ret) {
                    case AudioTrack.ERROR_INVALID_OPERATION:
                        break;
                    case AudioTrack.ERROR_BAD_VALUE:
                        break;
                    case AudioTrack.ERROR_DEAD_OBJECT:
                        //播放失败
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                close(inputStream);
            }
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }

    }

    private void close(FileInputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
