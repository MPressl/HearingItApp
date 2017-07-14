package de.dhbw.studienarbeit.hearItApp.soundAnimation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * SoundAnimation contains all informations about the shape, that is drawn
 * on the SoundAnimationView and moved by the SoundAnimationThread
 *
 * created by Andreas
 */

public class SoundAnimation {

    private int centerX;
    private int centerY;
    private int radius;

    public SoundAnimation(int centerX, int centerY, int radius){
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    /**
     * draw()
     * uses the parameter Canvas to paint circles on the SoundAnimationView with
     * specified paint-styles and radius
     */
    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#335696"));
        paint.setStyle(Paint.Style.FILL);
        //paint.setShadowLayer(40, 5, -5, Color.BLACK);
        canvas.drawCircle(centerX-radius, centerY, radius-radius/2, paint);
        canvas.drawCircle(centerX, centerY, radius, paint);
        canvas.drawCircle(centerX+radius, centerY, radius-radius/2, paint);


    }

    /**
     * setStartValue()
     * initializes the values of the SoundAnimation-Object for the first time
     */
    public void setStartValue(int newCencterX, int newCenterY, int newRadius){
        this.centerX = newCencterX;
        this.centerY = newCenterY;
        this.radius = newRadius;
    }

    /**
     * update()
     * moves the circles center & updates its radius
     */
    public void update(int centerXChange, int centerYChange, int radiusChange){
        this.centerX+=centerXChange;
        this.centerY+=centerYChange;
        this.radius+=radiusChange;
    }

    /**
     * setRadius()
     * gets an int-value newRadius and calculates the new circles radius.
     * creation the animation
     */
    public void setRadius(int newRadius){
        radius+=(newRadius-radius)/5;
    }
}
