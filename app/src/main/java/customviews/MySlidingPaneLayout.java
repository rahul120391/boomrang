package customviews;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by rahul on 4/3/2015.
 */
public class MySlidingPaneLayout extends SlidingPaneLayout {
    public MySlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return false; // here it returns false so that another event's listener should be called, in your case the MapFragment listener
    }
}
