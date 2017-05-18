package solid.ren.mediarecorderdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import solid.ren.mediarecorderdemo.R;
import solid.ren.mediarecorderdemo.view.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveView mWaveView;
    private Button mBtnStop;
    private Button mBtnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWaveView = (WaveView) findViewById(R.id.waveView);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mWaveView.setAmplitude(100);
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWaveView.stopWave();
            }
        });
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWaveView.startWave();
            }
        });
    }
}
