package com.we.ui.widget;

import com.we.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabBar extends LinearLayout implements View.OnClickListener {
	public static final int TAB_FLOW = 0; // 内容流
	public static final int TAB_FIND = 1; // 发现
	public static final int TAB_IM = 2; // 消息
	public static final int TAB_PROFILE = 3; // 个人
	public static final int TAB_PUBLISH = 4; // 发表

	private SparseArray<CheckedTextView> mTabViews = new SparseArray<CheckedTextView>(5);
	private TextView mIMRedPointTextView;

	private OnTabClickListener mOnTabClickListener;

	private int mSelectedTabId = -1;
	private CheckedTextView mTabPublish;
	private CheckedTextView mTabFlow;
	private CheckedTextView mTabFind;
	private CheckedTextView mTabIM;
	private CheckedTextView mTabProfile;

	public TabBar(Context context) {
		super(context);
		init();
	}

	public TabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	public void onClick(final View v) {
		Integer tabId = (Integer) v.getTag();
		changeTabView(tabId);
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.tab_bar, this);

		mTabFlow = (CheckedTextView) findViewById(R.id.tab_flow);
		mTabFind = (CheckedTextView) findViewById(R.id.tab_find);
		mTabIM = (CheckedTextView) findViewById(R.id.tab_im);
		mTabProfile = (CheckedTextView) findViewById(R.id.tab_profile);
		mTabPublish = (CheckedTextView) findViewById(R.id.tab_publish);

		mTabFlow.setTag(TAB_FLOW);
		mTabFind.setTag(TAB_FIND);
		mTabPublish.setTag(TAB_PUBLISH);
		mTabIM.setTag(TAB_IM);
		mTabProfile.setTag(TAB_PROFILE);

		mIMRedPointTextView = (TextView) findViewById(R.id.im_red_point);

		mTabViews.append(TAB_FLOW, mTabFlow);
		mTabViews.append(TAB_FIND, mTabFind);
		mTabViews.append(TAB_IM, mTabIM);
		mTabViews.append(TAB_PROFILE, mTabProfile);
		mTabViews.append(TAB_PUBLISH, mTabPublish);

		mTabFlow.setOnClickListener(this);
		mTabFind.setOnClickListener(this);
		mTabPublish.setOnClickListener(this);
		mTabIM.setOnClickListener(this);
		mTabProfile.setOnClickListener(this);
	}

	public void changeTabView(int tabId) {
		if (mOnTabClickListener != null) {
			if (mSelectedTabId == tabId) {
				mOnTabClickListener.onTabClickAgain(tabId);
			} else {
				mOnTabClickListener.onTabChange(tabId);
				updateTabSelectState(tabId);
			}
		}
	}

	private void updateTabSelectState(int tabId) {
		if (tabId == TAB_PUBLISH) {
			mTabPublish.setChecked(true);
			mIMRedPointTextView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mTabPublish.setChecked(false);
				}
			}, 1000);
		} else {
			int size = mTabViews.size();
			for (int i = 0; i < size; i++) {
				CheckedTextView view = mTabViews.valueAt(i);
				view.setChecked(false);
			}
			mTabViews.get(tabId).setChecked(true);
			mSelectedTabId = tabId;
		}
	}

	public void setOnTabClickListener(OnTabClickListener listener) {
		mOnTabClickListener = listener;
	}

	public void showRedPoint(int tabId, int redPointNum) {
		if (redPointNum == 0) {
			mIMRedPointTextView.setVisibility(View.INVISIBLE);
		} else {
			mIMRedPointTextView.setVisibility(View.VISIBLE);
			mIMRedPointTextView.setText(String.valueOf(redPointNum));
		}
	}

	public static interface OnTabClickListener {
		public void onTabChange(int tabId);

		public void onTabClickAgain(int tabId);
	}
}
