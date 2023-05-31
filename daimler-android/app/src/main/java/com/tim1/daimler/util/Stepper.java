package com.tim1.daimler.util;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tim1.daimler.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stepper {

    private boolean inProgress = false;
    private int stepsCount = 0;
    private int currentStep = 0;
    private final List<LinearLayout> stepsPlaceholders = new ArrayList<>();
    private final FragmentActivity activity;
    private ImageButton backButton = null;
    private final LinearLayout rootView;
    private LinearLayout navigationView;

    public Stepper(FragmentActivity activity, LinearLayout rootView) {
        this.activity = activity;
        this.rootView = rootView;
    }

    public Stepper(FragmentActivity activity, LinearLayout rootView,  LinearLayout navigationView, ImageButton backButton) {
        this.activity = activity;
        this.rootView = rootView;
        this.navigationView = navigationView;
        this.backButton = backButton;
    }

    public void addStep(Fragment stepView, LinearLayout placeholderView) {
        FragmentTransition.to(stepView, activity, false, placeholderView.getId());
        stepsPlaceholders.add(placeholderView);
        stepsCount++;
    }

    public void start() {
        start(0);
    }

    private void start(int position) {
        FragmentTransition.togglePopup(rootView, stepsPlaceholders.get(position), true);
        if (navigationView != null) FragmentTransition.toggleView(navigationView, true);
        currentStep = position;
        if (currentStep == stepsCount - 1) Objects.requireNonNull(backButton).setAlpha(0f);
        else Objects.requireNonNull(backButton).setAlpha(1f);
        if (backButton != null && currentStep == 0) backButton.setRotation(270);
        inProgress = true;
    }

    public boolean tryClose() {
        if(inProgress) {
            close();
            return true;
        }
        return false;
    }

    public void next() {
        if(currentStep == stepsCount - 1) return;
        FragmentTransition.slidePopupsForward(stepsPlaceholders.get(currentStep), stepsPlaceholders.get(++currentStep), true);
        if (backButton != null && currentStep == 1) backButton.setRotation(0);
        if (currentStep == stepsCount - 1) Objects.requireNonNull(backButton).setVisibility(View.GONE);
        else Objects.requireNonNull(backButton).setVisibility(View.VISIBLE);
    }

    public void previous() {
        if (currentStep == 0) {
            close();
            return;
        }
        FragmentTransition.slidePopupsBackwards(stepsPlaceholders.get(currentStep), stepsPlaceholders.get(--currentStep), true);
        if (backButton != null && currentStep == 0) backButton.setRotation(270);
    }

    public void jumpTo(int position) {
        if(!inProgress) {
            start(position);
            return;
        }
        if (backButton != null && position == 0) backButton.setRotation(270);
        if (position > currentStep) FragmentTransition.slidePopupsForward(stepsPlaceholders.get(currentStep), stepsPlaceholders.get(position), true);
        else if (position < currentStep) FragmentTransition.slidePopupsBackwards(stepsPlaceholders.get(currentStep), stepsPlaceholders.get(position), true);
        else return;
        currentStep = position;
    }

    private void close() {
        FragmentTransition.togglePopup(rootView, stepsPlaceholders.get(currentStep) , false);
        if (navigationView != null) FragmentTransition.toggleView(navigationView, false);
        currentStep = 0;
        inProgress = false;
    }

    public boolean isFinished() {
        return currentStep == stepsCount - 1;
    }

}
