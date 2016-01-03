package ch.blinkenlights.android.vanilla;

import android.app.Activity;
import android.app.ActionBar;

import android.widget.AbsListView;


import android.view.View;
import android.view.Window;
import android.util.TypedValue;
import android.util.Log;

import android.animation.ValueAnimator;


public class FloatingActionBar
	implements AbsListView.OnScrollListener {

	private final Activity mActivity;
	private final ActionBar mActionBar;
	private final int mActionBarHeight;

	private boolean mScrolling;

	private View mTopView;

	private int mLastPosY;
	private int mUiPosY;

	FloatingActionBar(Activity activity) {
		mActivity = activity;
		mActivity.getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		// Calculate height of activities action bar
		TypedValue tv = new TypedValue();
		activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
		mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,activity.getResources().getDisplayMetrics());
		mActionBar = activity.getActionBar();
	}

	public void setTopView(View view) {
		mTopView = view;
		// force a redraw
		setUiOffset(0, true);
	}


	@Override
	public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mScrolling) {
			int curPosY = getViewOffset(view);
			int progress = curPosY - mLastPosY;
			mLastPosY = curPosY;
			shiftUiOffset(progress);
		} else {
			Log.v("VanillaMusic", ">>> Scroll event while not scrolling from view: "+view);
		}
	}

	@Override
	public void onScrollStateChanged (AbsListView view, int scrollState) {
		Log.v("VanillaMusic", ">>> State change from "+view);
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			mLastPosY = getViewOffset(view);
			mScrolling = true;
			Log.v("VanillaMusic", view+" is now scrolling");
		} else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			mScrolling = false;

			int target = (mUiPosY > mActionBarHeight/2) ? mActionBarHeight : 0;
			int distance = Math.abs(target - mUiPosY);

			if (distance != 0) {
				Log.v("VanillaMusic", "Animating distance px= "+distance);
				ValueAnimator va = ValueAnimator.ofInt(mUiPosY, target);
				va.setDuration(distance*4);
				va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator a) {
						setUiOffset((Integer)a.getAnimatedValue(), false);
					}
				});
				va.start();
			}
		}
	}

	private void shiftUiOffset(int diff) {
		setUiOffset(mUiPosY + diff, false);
	}

	private void setUiOffset(int pos, boolean force) {
		if (pos < 0)
			pos = 0;
		if (pos > mActionBarHeight)
			pos = mActionBarHeight;
		if (pos != mUiPosY || force) {
			mUiPosY = pos;
			mActionBar.setHideOffset(mUiPosY);
			if (mTopView != null)
				mTopView.setY(mActionBarHeight - mUiPosY);
		}
	}


	private int getViewOffset(AbsListView view) {
		int offset = 0;
		View child = view.getChildAt(0);
		if (child != null) {
			offset = child.getHeight() * view.getFirstVisiblePosition() - child.getTop();
		}
		return offset;
	}


}
