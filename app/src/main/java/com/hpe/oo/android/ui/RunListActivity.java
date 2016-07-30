package com.hpe.oo.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return RunListFragment.newInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

}
