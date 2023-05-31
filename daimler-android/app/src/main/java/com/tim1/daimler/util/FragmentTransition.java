package com.tim1.daimler.util;

import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Filter;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class FragmentTransition {

    public static void to(Fragment newFragment, FragmentActivity activity, boolean addToBackstack, int layoutViewID)
    {
        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(layoutViewID, newFragment);
        if(addToBackstack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public static final String REQUEST_NEXT_STEP = "next_step";
    public static final String REQUEST_PREVIOUS_STEP = "previous_step";
    public static final String REQUEST_CANCEL_STEP = "cancel_step";

    public static void togglePopup(LinearLayout parentPlaceholder, LinearLayout childPlaceholder, boolean toggle) {
        toggleView(parentPlaceholder, !toggle);
        toggleView(childPlaceholder, toggle);
    }

    public static void slidePopupsForward(LinearLayout parentPlaceholder, LinearLayout childPlaceholder, boolean toggle) {
        toggleView(parentPlaceholder, !toggle, new Slide(Gravity.LEFT));
        toggleView(childPlaceholder, toggle, new Slide(Gravity.RIGHT));
    }

    public static void slidePopupsBackwards(LinearLayout parentPlaceholder, LinearLayout childPlaceholder, boolean toggle) {
        toggleView(parentPlaceholder, !toggle, new Slide(Gravity.RIGHT));
        toggleView(childPlaceholder, toggle, new Slide(Gravity.LEFT));
    }

    public static void toggleView(LinearLayout placeholder, boolean toggle) {
        TransitionManager.beginDelayedTransition(placeholder, new Fade());
        for (int i = 0; i < placeholder.getChildCount(); i++) placeholder.getChildAt(i).setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    public static void toggleView(LinearLayout placeholder, boolean toggle, Transition transition) {
        TransitionManager.beginDelayedTransition(placeholder, transition);
        for (int i = 0; i < placeholder.getChildCount(); i++) placeholder.getChildAt(i).setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

}
