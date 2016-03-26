package solid.ren.mediarecorderdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.security.KeyStore;

/**
 * 正弦函数表达式：y=Asin(ωx+φ)+k
 * <p/>
 * Created by _SOLID
 * Date:2016/3/22
 * Time:11:04
 */
public class WaveView extends View implements Runnable {

    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 初相
     */
    private int mAngle;
    /**
     * 振幅
     */
    private int mAmplitude;
    /**
     * WaveView Height
     */
    private int mHeight;
    /**
     * WaveView Width
     */
    private int mHidth;

    private Thread mThread;

    private boolean mIsRun;
    /**
     * 相位变化速率
     */
    private int mDeltaAngale;
    /**
     * 连续两个定点之间的间隔
     * 单位：px
     */
    private int mVertexGapWidth;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAngle = 0;
        mDeltaAngale = 80;
        mAmplitude = 50;
        mVertexGapWidth = 380;
        mIsRun = true;
        mThread = new Thread(this);
        mThread.start();

        //初始化画笔相关
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#03C0AC"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHeight = getHeight();
        mHidth = getWidth();
        for (int i = 0; i < mHidth; i++) {
            int startX = i;
            int startY = (int) (mAmplitude * Math.sin((i + mAngle) * Math.PI / mVertexGapWidth)) + mHeight / 2;
            int stopX = i + 1;
            int stopY = (int) (mAmplitude * Math.sin((i + 1 + mAngle) * Math.PI / mVertexGapWidth)) + mHeight / 2;
            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
        }
    }

    @Override
    public void run() {
        while (mIsRun) {
            mAngle += mDeltaAngale;
            postInvalidate();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPaintColor(int color) {
        if (mPaint != null)
            mPaint.setColor(color);
    }

    /**
     * 设置连续两个定点之间的间隔（单位：px）
     *
     * @param vertexGapWidth
     */
    public void setVertexGap(int vertexGapWidth) {
        this.mVertexGapWidth = vertexGapWidth;
    }

    /**
     * 设置振幅
     *
     * @param amplitude
     */
    public void setAmplitude(int amplitude) {
        this.mAmplitude = amplitude;
    }

    /***
     * 设置相位变化的速率
     *
     * @param deltaAngale
     */
    public void setDeltaAngale(int deltaAngale) {
        this.mDeltaAngale = deltaAngale;
    }

    /**
     * 开始动画
     */
    public void startWave() {
        if (mThread == null || mThread.isAlive()) return;
        mIsRun = true;
        mThread = new Thread(this);
        mThread.start();
    }

    /**
     * 停止动画
     */
    public void stopWave() {
        if (mThread == null || mThread.isInterrupted()) return;
        mIsRun = false;
    }
}
