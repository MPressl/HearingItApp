package de.dhbw.studienarbeit.hearItApp.soundAnimation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Andi on 28.03.2017.
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

    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        //paint.setShadowLayer(40, 5, -5, Color.BLACK);
        canvas.drawCircle(centerX-radius, centerY, radius-radius/2, paint);
        canvas.drawCircle(centerX, centerY, radius, paint);
        canvas.drawCircle(centerX+radius, centerY, radius-radius/2, paint);


    }

    public void setStartValue(int newCencterX, int newCenterY, int newRadius){
        this.centerX = newCencterX;
        this.centerY = newCenterY;
        this.radius = newRadius;
    }

    public void update(int centerXChange, int centerYChange, int radiusChange){
        this.centerX+=centerXChange;
        this.centerY+=centerYChange;
        this.radius+=radiusChange;
    }
}
