package de.dhbw.studienarbeit.hearItApp.soundAnimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;

/**
 * Created by Andi on 28.03.2017.
 */

public class SoundAnimationView extends SurfaceView implements SurfaceHolder.Callback {


    public static int SURFACE_WIDTH;
    public static int SURFACE_HEIGTH;

    private boolean animationStartValueSet = false;
    private int sound_animation_scaling_value = 0;

    private SoundAnimationThread soundAnimationThread;
    private SoundAnimation soundAnimation;

    public SoundAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);

       this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        soundAnimation = new SoundAnimation(0, 0, 0);
    }

    public SoundAnimationView(Context context) {
        super(context);}

    public SoundAnimationView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }
    public SoundAnimationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.rgb(218, 227, 243));
        soundAnimation.draw(canvas);
    }

    @Override
    public void onDraw(Canvas canvas){
        SURFACE_WIDTH = canvas.getWidth();
        SURFACE_HEIGTH = canvas.getHeight();
    }

    public void update(){
        if (!animationStartValueSet && SURFACE_WIDTH!=0){
          //  soundAnimation.setStartValue(SURFACE_WIDTH / 2, SURFACE_HEIGTH, 400);
            soundAnimation.setStartValue(SURFACE_WIDTH / 2, SURFACE_HEIGTH, 0);
            animationStartValueSet = true;
        }

        //newScalingValue = MainActivity.sound_animation_scaling_value;

        /*if(newScalingValue!=actualScalingValue){
            int difScalingValue = newScalingValue-actualScalingValue;
            if(difScalingValue>=0) {
                for (int j = 1; j < 5; j++) {   // In Vier Schleifenwiederholungen.. Flüssiger Übergang..
                    soundAnimation.setRadius((SURFACE_HEIGTH / 60) * newScalingValue - difScalingValue * (j / 4));
                }
            }
            else {
                for (int j = 1; j < 15; j++) {   // In Vier Schleifenwiederholungen.. Flüssiger Übergang..
                    soundAnimation.setRadius((SURFACE_HEIGTH / 60) * newScalingValue + difScalingValue * (j / 4));
                }
            }
        }*/
        if(this.sound_animation_scaling_value > 5) {
            soundAnimation.setRadius((SURFACE_HEIGTH / 60) * (this.sound_animation_scaling_value
                    + this.sound_animation_scaling_value / 5));
        }
        else {
            soundAnimation.setRadius(0);
        }

        //actualScalingValue = newScalingValue;
        /*if(!updateAddition) {
            soundAnimation.update(0, 0, -8);
            testCounter++;
            if(testCounter>58){
                testCounter=0;
                updateAddition = true;
            }
        }
        else {
            soundAnimation.update(0, 0, +8);
            testCounter++;
            if(testCounter>58){
                testCounter=0;
                updateAddition = false;
            }
        }*/
    }




    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public int getSurfaceHeight() {
        return SURFACE_HEIGTH;
    }

    public int getSurfaceWidth() {
        return SURFACE_WIDTH;
    }

    public void startDrawingThread() {
        this.soundAnimationThread = new SoundAnimationThread(getHolder(), this);
        this.soundAnimationThread.setRunning(true);
        this.soundAnimationThread.start();
    }
    public void stopDrawingThread() {
        this.soundAnimationThread.setRunning(false);
    }

    public void setScalingValue(int value) {
        this.sound_animation_scaling_value = value;
    }
}
