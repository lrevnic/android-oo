package com.hpe.oo.android.ui;

/**
 * Created by revnic on 7/9/2016.
 */

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

public class FlowLaunchActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return FlowLaunchFragment.newInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

}
