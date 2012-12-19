package com.simekadam.noteapp;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 12/18/12
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

/**
 * This animation class is animating the expanding and reducing the size of a view.
 * The animation toggles between the Expand and Reduce, depending on the current state of the view
 * @author Udinic
 *
 */
public class ExpandAnimation extends Animation {
    int mFromHeight;
    View mView;

    public ExpandAnimation(View view) {
        this.mView = view;
        this.mFromHeight = view.getHeight();
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        newHeight = 100;
        mView.getLayoutParams().height = newHeight;
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}

