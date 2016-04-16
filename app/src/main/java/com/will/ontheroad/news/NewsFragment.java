package com.will.ontheroad.news;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.will.ontheroad.R;
import com.will.ontheroad.constant.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will on 2016/4/14.
 */
public class NewsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_news_page,container,false);
        tabLayout = (TabLayout) view.findViewById(R.id.news_tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.news_view_pager);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.addTab(tabLayout.newTab().setText("头条"));
        tabLayout.addTab(tabLayout.newTab().setText("本地"));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(NewsListFragment.getInstance(Constant.TYPE_HEADLINE),"头条");
        adapter.addFragment(NewsListFragment.getInstance(Constant.TYPE_LOCAL),"本地");
        viewPager.setAdapter(adapter);
    }
    public static class ViewPagerAdapter extends FragmentPagerAdapter{
        List<Fragment> fragments = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();
        public void addFragment(Fragment fragment ,String title){
            fragments.add(fragment);
            fragmentTitles.add(title);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position){
            return fragments.get(position);
        }
        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }
        @Override
        public int getCount(){
            return fragments.size();
        }
        @Override
        public CharSequence getPageTitle(int position){
            return fragmentTitles.get(position);
        }
    }
}
