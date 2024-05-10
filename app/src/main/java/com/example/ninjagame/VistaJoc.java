package com.example.ninjagame;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Vector;

public class VistaJoc extends View {
    public VistaJoc(Context context, AttributeSet attrs) {
        super(context, attrs);

        Drawable drwNinja, drwGanivet, drwEnemic;

        drwNinja = context.getResources().getIdentifier()
        drwEnemic = context.getResources().getDrawable(R.drawable.ninja_enemic);

        objectius = new Vector<Grafics>();
    }
}
