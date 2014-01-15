package com.egco.storefinderproject.adapter;

import java.util.ArrayList;

import com.egco.storefinderproject.fragment.MainPageFavoriteFragment;
import com.egco.storefinderproject.fragment.MainPageHistoryFragment;
import com.egco.storefinderproject.fragment.MainPageMainFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPageViewPagerAdapter extends FragmentPagerAdapter{
	
	private ArrayList<Fragment> fmList;
	
	public MainPageViewPagerAdapter(FragmentManager fm)
	{
		super(fm);
		fmList = new ArrayList<Fragment>();
		
		fmList.add(new MainPageHistoryFragment());
		fmList.add(new MainPageMainFragment());
		fmList.add(new MainPageFavoriteFragment());

	}

	@Override
	public Fragment getItem(int arg0) {
		return fmList.get(arg0);
	}

	@Override
	public int getCount() {
		return fmList.size();
	}
	
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position)
        {
        case 0: return "History";
        case 1: return "Main";
        case 2: return "Favorite";
        default: return "Not specific yet";
        }
    }

}
