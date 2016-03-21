package com.natavit.cooklycook.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.view.WaveProgressView;

public class AnimationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        WaveProgressView waveProgressView = (WaveProgressView) findViewById(R.id.wave_progress_view);
        waveProgressView.setMax(100);
        animWave(waveProgressView, 10 * 600);
    }

    private void animWave(WaveProgressView waveProgressView, long duration){
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(waveProgressView, "progress", 0, waveProgressView.getMax());
        progressAnim.setDuration(duration);
        progressAnim.setRepeatCount(ObjectAnimator.INFINITE);
        progressAnim.setRepeatMode(ObjectAnimator.RESTART);
        progressAnim.start();
    }
}
