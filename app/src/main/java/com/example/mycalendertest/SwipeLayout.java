package com.example.mycalendertest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

public class SwipeLayout extends RelativeLayout {

    private int maxLength; // 最大滑动距离（菜单宽度）
    private int startX, startY; // 触摸起点
    private boolean isOpen = false; // 菜单是否打开
    private boolean isDragging = false; // 是否正在拖动
    private final int touchSlop; // 滑动阈值
    private OnSwipeListener swipeListener; // 滑动监听器
    private int currentScrollX = 0; // 记录当前滑动距离

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取菜单宽度作为最大滑动距离
        View menuView = findViewById(R.id.layout_menu);
        if (menuView != null) {
            maxLength = menuView.getMeasuredWidth();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View menuView = findViewById(R.id.layout_menu);
        View contentView = findViewById(R.id.layout_content);
        if (menuView == null || contentView == null) {
            super.onLayout(changed, l, t, r, b);
            return;
        }

        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredHeight();
        int menuWidth = menuView.getMeasuredWidth();

        // menuView靠右，宽度为自身宽度
        menuView.layout(parentWidth - menuWidth, 0, parentWidth, parentHeight);

        // 内容布局根据currentScrollX滑动
        contentView.layout(-currentScrollX, 0, parentWidth - currentScrollX, parentHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                // 不直接return true，先交给父类
                break;

            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getX();
                int currentY = (int) event.getY();
                int dx = currentX - startX;
                int dy = currentY - startY;

                if (!isDragging && Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy)) {
                    isDragging = true;
                    startX = currentX;
                    startY = currentY;
                }

                if (isDragging) {
                    int newScrollX = currentScrollX - dx;
                    newScrollX = Math.max(0, Math.min(newScrollX, maxLength));
                    currentScrollX = newScrollX;
                    requestLayout();
                    startX = currentX;
                    startY = currentY;
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    isDragging = false;
                    handleActionUp();
                    return true;
                }
                break;
        }
        // 只有在滑动时才消费事件，否则交给父类（让点击事件能传递下去）
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                isDragging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) ev.getX() - startX;
                int dy = (int) ev.getY() - startY;
                if (Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy)) {
                    isDragging = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }
        return false;
    }

    private void handleActionUp() {
        if (currentScrollX > maxLength / 2) {
            smoothScrollTo(maxLength);
            isOpen = true;
            if (swipeListener != null) {
                swipeListener.onMenuOpened(this);
            }
        } else {
            smoothScrollTo(0);
            isOpen = false;
            if (swipeListener != null) {
                swipeListener.onMenuClosed(this);
            }
        }
    }

    private void smoothScrollTo(int destX) {
        ValueAnimator animator = ValueAnimator.ofInt(currentScrollX, destX);
        animator.addUpdateListener(animation -> {
            currentScrollX = (int) animation.getAnimatedValue();
            requestLayout();
        });
        animator.setDuration(200).start();
    }

    public void closeMenu() {
        smoothScrollTo(0);
        isOpen = false;
        if (swipeListener != null) {
            swipeListener.onMenuClosed(this);
        }
    }

    public void closeMenuImmediately() {
        currentScrollX = 0;
        requestLayout();
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.swipeListener = listener;
    }

    public interface OnSwipeListener {
        void onMenuOpened(SwipeLayout swipeLayout);
        void onMenuClosed(SwipeLayout swipeLayout);
    }
}
