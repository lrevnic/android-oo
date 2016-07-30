package com.hpe.oo.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hpe.oo.android.OoConnector;
import com.hpe.oo.android.model.Run;
import com.hpe.oo.android.oo.R;

import java.util.List;

public class RunPagerActivity extends AppCompatActivity {
    protected static final String EXTRA_RUN_ID = "com.hpe.oo.android.ui.run_id";
    private ViewPager mViewPager;

    public static Intent newIntent(Context packageContext, Long runId) {
        Intent intent = new Intent(packageContext, RunPagerActivity.class);
        intent.putExtra(EXTRA_RUN_ID, runId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.activity_run_pager_view_pager);
        setContentView(mViewPager);

        final List<Run> runs = OoConnector.newInstance().getAllRuns();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return runs.size();
            }
            @Override
            public Fragment getItem(int pos) {
                Long id =  runs.get(pos).getId();
                return RunFragment.newInstance(id);
            }
        });

        Long runId = (Long)getIntent().getSerializableExtra(EXTRA_RUN_ID);
        for (int i = 0; i < runs.size(); i++) {
            if (runs.get(i).getId().equals(runId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}