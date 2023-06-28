package cist.cmc.nfc.sdk.demo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * NFCScrollView extends ScrollView is a class that allows to catch scroll events
 */
public class NFCScrollView extends ScrollView {
    private Runnable scrollerTask;
    private int initialPosition;
    private int newCheck = 100;
    private static final String TAG = "MyScrollView";

    private OnScrollStoppedListener onScrollStoppedListener;

    public NFCScrollView(Context context) {
        super(context);
    }

    public NFCScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollerTask = new Runnable() {
            @Override
            public void run() {
                int newPosition = getScrollY();
                if (initialPosition - newPosition == 0) {
                    if (onScrollStoppedListener != null) {
                        onScrollStoppedListener.onScrollStopped();
                    }
                } else {
                    initialPosition = getScrollY();
                    NFCScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public NFCScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NFCScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface OnScrollStoppedListener {
        void onScrollStopped();
    }

    public void setOnScrollStoppedListener(OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask() {
        initialPosition = getScrollY();
        NFCScrollView.this.postDelayed(scrollerTask, newCheck);
    }
}
