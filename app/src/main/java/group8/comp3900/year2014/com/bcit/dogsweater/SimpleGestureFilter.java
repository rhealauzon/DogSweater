package group8.comp3900.year2014.com.bcit.dogsweater;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Rhea on 27/10/2014.
 * Gets gestures from device for opening the menus
 */

    class MyGestureDetector extends SimpleOnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 80;
    private static final int SWIPE_MAX_OFF_PATH = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 150;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    private SlideMenu sm;

    MyGestureDetector(SlideMenu menu)
    {
        sm = menu;
    }

    @Override
    /**********************************************
     * OnFling()
     * When the screen is swiped left to right the menu
     * is pulled out.
     *
     * Right to left causes the menu to be hidden again.
     *
     * NOTE:: ONLY works on non-menu screen portions.
     ************************************************/
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }
            // right to left swipe; close the menu
            else if ( Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (e1.getX() > e2.getX()) {
                    if (sm.getShown()) {
                        sm.hide();
                    }
                }
                //Left to right; show the menu
                else {
                    sm.show();
                }
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
