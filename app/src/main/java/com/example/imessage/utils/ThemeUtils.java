package com.example.imessage.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;

import com.example.imessage.R;

public class ThemeUtils {

    public static void SetStatusBar(Activity activity, int statusBarColour){
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setStatusBarColor(activity.getResources().getColor(statusBarColour, activity.getTheme()));

            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            window.setStatusBarColor(activity.getResources().getColor(statusBarColour));

            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    public static void setDynamicTheme(Context context){
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(nightModeFlags==Configuration.UI_MODE_NIGHT_YES){
            context.setTheme(R.style.AppTheme_Dark);
        }else {
            context.setTheme(R.style.AppTheme_Light);
        }
    }




}
