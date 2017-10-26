package com.mus.ruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mus.ruler.ruler.CustomView;
import com.mus.ruler.ruler.MusRulerView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    CustomView customView;
    TextView mScaleTv;
    DecimalFormat mDf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customView = (CustomView) findViewById(R.id.custom_view);
        mScaleTv = (TextView) findViewById(R.id.scale_tv);

        mDf = new DecimalFormat("0.0");

        customView.setmScaleCallBack(new MusRulerView.ScaleCallBack() {
            @Override
            public void scaleResult(String string) {
                mScaleTv.setText(mDf.format(Double.parseDouble(string)) + "");
            }
        });

    }
}
