package com.jdp30.moodlighting2.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jdp30.moodlighting2.R;

/**
 * Created by jackp on 31/05/2018.
 */

public class ColorSelectorView extends View {
    private Bitmap picker = null;
    private int imgX, imgY;
    private int currentColor = Color.WHITE;
    private float colourRadius = 1.0f;

    private ColorListener listener;

    private int[] presetColors = new int[]{Color.RED,Color.GREEN,Color.BLUE,Color.CYAN,Color.YELLOW,Color.rgb(255,180,0),Color.rgb(255,0,255)};

    public ColorSelectorView(Context context) {
        super(context);
    }

    public ColorSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        picker = BitmapFactory.decodeResource(getResources(), R.mipmap.color_picker);
        int w = getWidth(), h = getHeight();
        if (w > h) {
            picker = Bitmap.createScaledBitmap(
                    picker, (int) (h - (((double) h) / 4.0)), (int) (h - (((double) h) / 4.0)), false);
            this.colourRadius =  (picker.getHeight() + 100);
        } else {
            picker = Bitmap.createScaledBitmap(
                    picker, (int) (w - (((double) w) / 4.0)), (int) (w - (((double) w) / 4.0)), false);
            this.colourRadius =  (picker.getWidth() + 100);
        }
        this.colourRadius /= 2;
    }

    Paint p = new Paint();
    Paint outline = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        if (picker == null) {
            init();
            invalidate();
            outline.setStrokeWidth(15);
            outline.setColor(Color.BLACK);
            outline.setStyle(Paint.Style.STROKE);
            return;
        }
        this.imgX = getWidth() / 2 - picker.getWidth() / 2;
        this.imgY = getHeight() / 2 - picker.getHeight() / 2;

        p.setColor(this.currentColor);
        canvas.drawCircle(this.imgX + (picker.getWidth() / 2), this.imgY + (picker.getHeight() / 2), colourRadius, p);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(5);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(this.imgX + (picker.getWidth() / 2), this.imgY + (picker.getHeight() / 2), colourRadius, p);
        p = new Paint();
        canvas.drawBitmap(picker, this.imgX, this.imgY, p);
/*
        int i = 0;
        int w = (getWidth() - 100) / presetColors.length;
        for(int c : presetColors){
            p.setColor(c);
            canvas.drawRect(50 + w * i, this.imgY + (picker.getHeight() / 2) + colourRadius + 30, (w*i) + w + 50,this.imgY + (picker.getHeight() / 2) + colourRadius + w/2 + 30,p);
            canvas.drawRect(50 + w * i, this.imgY + (picker.getHeight() / 2) + colourRadius + 30, (w*i) + w + 50,this.imgY + (picker.getHeight() / 2) + colourRadius + w/2 + 30,outline);
            i++;
        }*/
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN || (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            int pX = (int) (x - imgX);
            int pY = (int) (y - imgY);
            if (pX >= 0 && pY >= 0 && pX < picker.getWidth() && pY < picker.getHeight() ) {
                this.currentColor = picker.getPixel(pX,pY);
                if(listener != null){
                    listener.onColorChanged(currentColor);
                }
                Log.d("MoodLighting2",this.currentColor + "");
                invalidate();
            }
        }
        return true;
    }

    public void setListener(ColorListener listener){
        this.listener = listener;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public interface ColorListener{
        void onColorChanged(int color);
    }

}
