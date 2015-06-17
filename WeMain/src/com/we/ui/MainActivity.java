package com.we.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.we.R;
import com.we.base.BaseActivity;
import com.we.ui.fragment.MainTabFindFragment;
import com.we.ui.fragment.MainTabFlowFragment;
import com.we.ui.fragment.MainTabIMFragment;
import com.we.ui.fragment.MainTabProfileFragment;
import com.we.ui.widget.TabBar;

public class MainActivity extends BaseActivity implements TabBar.OnTabClickListener {
	TabBar mTabBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		mTabBar = (TabBar) findViewById(R.id.tab_bar);
		mTabBar.setOnTabClickListener(this);
		mTabBar.changeTabView(TabBar.TAB_FLOW);
	}

	@Override
	public void onTabChange(int tabId) {
		Fragment fragment = null;

		switch (tabId) {
		case TabBar.TAB_FLOW:
			fragment = new MainTabFlowFragment();
			break;
		case TabBar.TAB_FIND:
			fragment = new MainTabFindFragment();
			break;
		case TabBar.TAB_PUBLISH:
			break;
		case TabBar.TAB_IM:
			fragment = new MainTabIMFragment();
			break;
		case TabBar.TAB_PROFILE:
			fragment = new MainTabProfileFragment();
			break;
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.home_content, fragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	public void onTabClickAgain(int tabId) {

	}

}
