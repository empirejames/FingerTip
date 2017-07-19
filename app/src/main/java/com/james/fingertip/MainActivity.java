package com.james.fingertip;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity implements SensorEventListener, Animation.AnimationListener {
    MyView show;
    int MAX_ANGLE = 30;
    SensorManager mSensorManager;
    ImageView imgTaigi;
    String TAG = MainActivity.class.getSimpleName();
    int stopturnnum = (int) (Math.random() * 3600);
    Animation am;
    int speed_x;
    int speed_y;
    int value = 800;
    int intScreenWidth, intScreenHeight;
    String speedStatus;
    int randomPic;
    int randomString;
    private Long startTime;
    private Handler handler = new Handler();
    TextView tv;
    TextView tvContant;
    private InterstitialAd mInterstitialAd;
    int speedControl = 10;
    private int[] resId01 = {R.drawable.taigi1, R.drawable.taigi2, R.drawable.taigi3, R.drawable.taigi4, R.drawable.taigi5, R.drawable.taigi6, R.drawable.taigi7, R.drawable.taigi8, R.drawable.taigi9, R.drawable.taigi10};
    private String[] listString = {"「陰」則起著完成、接受、 被動和服從的作用", "「陽」是在萬物生成中起著創始、施與、主動和領導作用", "仔細觀查是否能體悟出一些道理", "靜下心 聽其聲 察其色", " 動如猛虎，靜如帝鳄", "太極生兩儀 兩儀生四象 四象生八卦 八卦演萬物", "宇宙的終極我們稱之為無極", "兩種力量在不斷作用、循環往復，於是他稱之為「陰陽」"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        tv = (TextView) findViewById(R.id.taigi_text);
        tvContant = (TextView) findViewById(R.id.taigi_conten);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        randomPic = (int) (Math.random() * resId01.length+ 1);
        imgTaigi = (ImageView) findViewById(R.id.img_taigi);
        String drawableName = "taigi" + randomPic;
        int resID = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        imgTaigi.setImageResource(resID);
        am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setAnimationListener(MainActivity.this);
        am.setInterpolator(new LinearInterpolator());
        getScrrenSize();
        myimageviewsize(imgTaigi, intScreenWidth, intScreenHeight);
        show = (MyView) findViewById(R.id.show);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        startRotation(value, false);
        imgTaigi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        startTime = System.currentTimeMillis();
        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);
        handler.postDelayed(updateString, 1000);
    }

    public void startRotation(int stop, boolean triggle) {
        // Log.e(TAG,"" + stop);
        if (stop > 0 && triggle == false) {
            am.setDuration(stop);
            am.setRepeatCount(Animation.INFINITE);
            am.setRepeatMode(Animation.INFINITE);
            imgTaigi.setAnimation(am);
            am.startNow();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ORIENTATION:
                float yAngle = values[1];
                float zAngle = values[2];
                int x = (show.back.getWidth() - show.bubble.getWidth()) / 2;
                int y = (show.back.getHeight() - show.bubble.getHeight()) / 2;
                if (Math.abs(zAngle) <= MAX_ANGLE) {
                    int deltaX = (int) ((show.back.getWidth() - show.bubble
                            .getWidth()) / 2 * zAngle / MAX_ANGLE);
                    x += deltaX;
                } else if (zAngle > MAX_ANGLE) {
                    x = 0;
                } else {
                    x = show.back.getWidth() - show.bubble.getWidth();
                }
                if (Math.abs(yAngle) <= MAX_ANGLE) {
                    int deltaY = (int) ((show.back.getHeight() - show.bubble
                            .getHeight()) / 2 * yAngle / MAX_ANGLE);
                    y += deltaY;
                } else if (yAngle > MAX_ANGLE) {
                    y = show.back.getHeight() - show.bubble.getHeight();
                } else {
                    y = 0;
                }
                if (isContain(x, y)) {
                    show.bubbleX = x;
                    show.bubbleY = y;
                }
                speed_x = x;
                speed_y = y;
                show.postInvalidate();

                break;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        imgTaigi = null;
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    private boolean isContain(int x, int y) {
        int bubbleCx = x + show.bubble.getWidth() / 2;
        int bubbleCy = y + show.bubble.getWidth() / 2;
        int backCx = show.back.getWidth() / 2;
        int backCy = show.back.getWidth() / 2;
        double distance = Math.sqrt((bubbleCx - backCx) * (bubbleCx - backCx)
                + (bubbleCy - backCy) * (bubbleCy - backCy));
        if (distance < (show.back.getWidth() - show.bubble.getWidth()) / 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        //Log.e(TAG, " onAnimationStart");

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.e(TAG, " onAnimationEnd");
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // Log.e(TAG, " onAnimationRepeat");
        //Log.e(TAG, speed_x - speed_y + "  =  " + speedStatus);
        if (subXY().equals("fast")) {
            startRotation(300, false);
        } else if (subXY().equals("middle")) {

            startRotation(500, false);
        } else if (subXY().equals("slow")) {
            startRotation(1000, false);
        } else if (subXY().equals("slow1")) {
            startRotation(1500, false);
        } else {
            startRotation(2000, false);
            //am.cancel();
            //am.reset();
        }
        if (speedControl >= 5 && speedControl < 10) {
            startRotation(200, false);
        } else if (speedControl >= 1 && speedControl < 5) {
            startRotation(50, false);
        } else if (speedControl == 0) {
            startRotation(20, false);
        }
    }

    private Runnable updateString = new Runnable() {
        public void run() {
            handler.postDelayed(this, 5000);
            randomString = (int) (Math.random() * listString.length - 1 + 1);
            //Log.e(TAG, "speedControl: " + speedControl);
            if (speedControl > 8 && speedControl < 10) {
                tvContant.setText("太極者，無極而生 陰陽之母也 動之則分 靜之則合 ");
            } else if (speedControl > 5 && speedControl <= 8) {
                tvContant.setText("兩儀立焉，陽變陰合，而水火木金土五氣順布");
            } else if (speedControl > 0 && speedControl <= 5) {
                tvContant.setText("四時行焉 五行一陰陽也 陰陽一太極也 太極本無極也 ");
            } else if (speedControl == 0) {
                tvContant.setText("仔細觀查是否能體悟出一些道理");
                tvContant.setText(listString[randomString]);
            } else if (speedControl == 10) {
                tvContant.setText("嘗試著維持住太極的平衡 將有意想不到的結果");
            }
        }
    };

    private Runnable updateTimer = new Runnable() {
        public void run() {
            Long spentTime = System.currentTimeMillis() - startTime;
            Long minius = (spentTime / 1000) / 60;
            Long seconds = (spentTime / 1000) % 60;
            tv.setText(minius + " 分 " + seconds + "秒");
            handler.postDelayed(this, 1000);
            if (speedStatus != null) {
                if (speedStatus.equals("fast")) {
                    speedControl -= 1;
                } else {
                    speedControl += 1;
                }
                if (speedControl > 10 && speedControl != 0) {
                    speedControl = 10;
                } else if (speedControl < 0) {
                    speedControl = 0;
                }
            }

        }
    };

    public String subXY() {
        if (Math.abs(speed_x - speed_y) <= 30) {
            speedStatus = "fast";
            //Log.e(TAG,"Status :: Good speed" );
            //startRotation(200);
        } else if (Math.abs(speed_x - speed_y) > 30 && Math.abs(speed_x - speed_y) <= 50) {
            speedStatus = "middle";
            //Log.e(TAG,"Status :: middle speed" );
            //startRotation(500);
        } else if (Math.abs(speed_x - speed_y) > 50 && Math.abs(speed_x - speed_y) <= 80) {
            speedStatus = "slow";
            //Log.e(TAG,"Status :: slow speed" );
            //startRotation(3000);
        } else if (speed_x - speed_y > 80 && speed_x - speed_y <= 100) {
            speedStatus = "slow1";
            //Log.e(TAG,"Status :: STOP" + speed_x + " :: " + speed_y );
        } else {
            speedStatus = "Stop";
        }
        return speedStatus;
    }

    public void getScrrenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        intScreenWidth = dm.widthPixels;   //螢幕的寬
        intScreenHeight = dm.heightPixels;  //螢幕的高
        Log.e(TAG, "" + intScreenWidth + " :: " + intScreenHeight);
    }

    private void myimageviewsize(ImageView imgid, int evenWidth, int evenHight) {
        ViewGroup.LayoutParams params = imgid.getLayoutParams();
        params.width = evenWidth;
        params.height = evenHight;
        imgid.setLayoutParams(params);
    }
}
