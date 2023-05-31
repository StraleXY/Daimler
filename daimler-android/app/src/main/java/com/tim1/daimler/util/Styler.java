package com.tim1.daimler.util;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tim1.daimler.R;

import java.util.Objects;

public class Styler {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void makeFullScreen(AppCompatActivity target) {
        Objects.requireNonNull(target.getSupportActionBar()).hide();
        WindowCompat.setDecorFitsSystemWindows(target.getWindow(), false);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void hideStatusBar(AppCompatActivity target, View root) {
        Objects.requireNonNull(target.getSupportActionBar()).hide();
        WindowCompat.setDecorFitsSystemWindows(target.getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });
    }
    public static void applySplash(AppCompatActivity target) {
        SplashScreen.installSplashScreen(target).setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
            @Override
            public boolean shouldKeepOnScreen() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
}
