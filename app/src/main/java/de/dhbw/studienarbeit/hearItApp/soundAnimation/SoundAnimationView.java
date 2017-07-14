package de.dhbw.studienarbeit.hearItApp.soundAnimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.dhbw.studienarbeit.hearItApp.MainActivity;

/**
 * SoundAnimationView is the surface, that is used for the animation
 * drawn with Canvas-components
 *
 * created by Andreas
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

    /**
     * draw()
     * draws the background of the SurfaceView and calls the draw()-method of SoundAnimation
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.rgb(218, 227, 243));
        soundAnimation.draw(canvas);
    }

    /**
     * onDraw()
     * gets the SurfaceViews dimensions by using the Canvas-object
     */
    @Override
    public void onDraw(Canvas canvas){
        SURFACE_WIDTH = canvas.getWidth();
        SURFACE_HEIGTH = canvas.getHeight();
    }

    /**
     * update()
     * updates the SoundAnimations parameters dependently on the recorded sounds loudness
     */
    public void update(){
        if (!animationStartValueSet && SURFACE_WIDTH!=0){
            soundAnimation.setStartValue(SURFACE_WIDTH / 2, SURFACE_HEIGTH, 0);
            animationStartValueSet = true;
        }
        if(this.sound_animation_scaling_value > 5) {
            soundAnimation.setRadius((SURFACE_HEIGTH / 60) * (this.sound_animation_scaling_value
                    + this.sound_animation_scaling_value / 5));
        }
        else {
            soundAnimation.setRadius(0);
        }
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

    /**
     * startDrawingThread()
     * brings the SoundAnimationThread into being
     */
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
