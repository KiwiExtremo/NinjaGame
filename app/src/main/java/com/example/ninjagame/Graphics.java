package com.example.ninjagame;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Graphics {
    private Drawable drawable;
    private double posX, posY;
    private double incX, incY;
    private int angle, rotation;
    private int width, height;
    private int collisionRadius;
    private View view;
    public static final int MAX_SPEED = 20;

    public Graphics(View view, Drawable drawable) {
        this.view = view;
        this.drawable = drawable;
        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();
        collisionRadius = (height + width) / 4;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getIncX() {
        return incX;
    }

    public void setIncX(double incX) {
        this.incX = incX;
    }

    public double getIncY() {
        return incY;
    }

    public void setIncY(double incY) {
        this.incY = incY;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void drawGraphic(Canvas canvas) {
        canvas.save();

        int x = (int) (posX + width / 2);
        int y = (int) (posY + height / 2);

        canvas.rotate((float) angle, (float) x, (float) y);

        drawable.setBounds((int) posX, (int) posY, (int) posX + width, (int) posY + height);
        drawable.draw(canvas);

        canvas.restore();

        int rInval = (int) Math.hypot(width, height) / 2 + MAX_SPEED;
        view.invalidate(x - rInval, y - rInval, x + rInval, y + rInval);
    }
    public void increasePosition(double factor) {
        posX += incX * factor;

        // Si sortim de la pantalla, corregim posició
        if (posX < (float) -width / 2) {
            posX = view.getWidth() - (float) width / 2;
        }

        if (posX > view.getWidth() - (float) width / 2) {
            posX = (float) -width / 2;
        }

        posY += incY * factor;

        if (posY < (float) -height / 2) {
            posY = view.getHeight() - (float) height / 2;
        }

        if (posY > view.getHeight() - (float) height / 2) {
            posY = (float) -height / 2;
        }

        angle += rotation * factor; //Actualitzem angle
    }
    public double distance(Graphics g) {
        return Math.hypot(posX - g.posX, posY - g.posY);
    }
    public boolean isColliding(Graphics g) {
        return (distance(g) < (collisionRadius + g.collisionRadius));
    }
}
