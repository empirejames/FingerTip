package com.james.fingertip;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by 101716 on 2017/7/18.
 */

public class MyView extends View {
    Bitmap back;
    Bitmap bubble;
    int bubbleX, bubbleY;
    Animation am;
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setViewImg(R.mipmap.taigi);
    }

    public void setViewImg(int img_id){
        back = BitmapFactory.decodeResource(getResources(), img_id);
        bubble = BitmapFactory.decodeResource(getResources(), R.drawable.bubble);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(back, 0, 0, null);
        canvas.drawBitmap(bubble, bubbleX, bubbleY, null);
    }
}
