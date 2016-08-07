package com.hpe.oo.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import com.hpe.oo.android.model.Run;

public class InputListActivity extends SingleFragmentActivity {
    private static final String EXTRA_RUN_ID = "com.hpe.oo.android.ui.run_id";

    public static Intent newIntent(Context packageContext, Long runId) {
        Intent i = new Intent(packageContext, InputListActivity.class);
        i.putExtra(EXTRA_RUN_ID, runId);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected Fragment createFragment() {
        Long runId  = (Long) getIntent().getSerializableExtra(EXTRA_RUN_ID);
        return InputListFragment.newInstance(runId);
    }

}
