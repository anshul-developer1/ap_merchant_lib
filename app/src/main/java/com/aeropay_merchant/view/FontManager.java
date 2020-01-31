package com.aeropay_merchant.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FontManager {
    private static FontManager manager;
    private final Context context;
    private final ArrayList<String> fontNames = new ArrayList<String>();
    private final HashMap<String, Typeface> typeFaceStore = new HashMap<String, Typeface>();

    private FontManager(Context context) {
        this.context = context;
    }

    public static FontManager getInstance(Context context) {
        if (manager == null) {
            manager = new FontManager(context);
        }
        return manager;
    }


    //Sets the typeface or font based on the view and name of typeface passed
    public void setTypeFace(View view, String fontName) {
        if (!(view instanceof android.widget.TextView)) {
            return;
        }
        int index = fontNames.indexOf(fontName);
        if (index == -1) {
            fontNames.add(fontName);
        } else {
            fontName = fontNames.get(index);
        }
        Typeface typeface = typeFaceStore.get(fontName);
        if (typeface == null) {
            typeface = FontManager.findTypeface(context, "font", fontName);
            typeFaceStore.put(fontName, typeface);
        }
        if (typeface != null) {
            ((android.widget.TextView) view).setTypeface(typeface);
        } else {
            ((android.widget.TextView) view).setTypeface(Typeface.DEFAULT);
        }
    }

    public static Typeface findTypeface(Context context, String initPath,
                                        String typeface) {
        AssetManager assets = context.getAssets();
        return Typeface.createFromAsset(assets, (initPath + File.separator)
                + typeface);
    }

}
