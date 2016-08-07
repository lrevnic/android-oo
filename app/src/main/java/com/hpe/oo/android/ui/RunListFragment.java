package com.hpe.oo.android.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hpe.oo.android.OOConnector;
import com.hpe.oo.android.model.RunStatus;
import com.hpe.oo.android.utils.QueryPreferences;
import com.hpe.oo.android.model.Run;
import com.hpe.oo.android.oo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RunListFragment extends Fragment {
    private static final String  TAG = "RunListFragment";

    private List<Run>       mRuns;
    private RecyclerView    mRunRecyclerView;
    private OOConnector mOOConnector;

    public static RunListFragment newInstance() {
        Bundle args                     = new Bundle();
        RunListFragment runListFragment = new RunListFragment();
        runListFragment.setArguments(args);
        return runListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mOOConnector = OOConnector.newInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_list, container, false);
        mRunRecyclerView = (RecyclerView) view.findViewById(R.id.run_recycler_view);
        mRunRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_run_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_run_search);
        final SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);

                updateUI();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                QueryPreferences.setStoredQuery(getActivity(), newText);

                updateUI();

                return false;
                }
            }
        );

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_new_flow:
                Intent intent = new Intent(getActivity(), FlowLaunchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private class RunHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public  TextView  mNameTextView;
        private ImageView mStatusImgView;
        private TextView  mStartTextView;
        private TextView  mDurationTextView;
        private Run       mRun;

        public RunHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mNameTextView = (TextView) itemView.findViewById(R.id.run_list_item_nameTextView);
            mStatusImgView  = (ImageView) itemView.findViewById(R.id.run_list_item_statusImgButton);
            mStartTextView = (TextView) itemView.findViewById(R.id.run_list_item_startTextView);
            mDurationTextView = (TextView) itemView.findViewById(R.id.run_list_item_durationTextView);
        }

        public void bindRun(Run run){
            mRun = run;

            mNameTextView.setText(mRun.getName());

            if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.SYSTEM_FAILURE.name())) {
                mStatusImgView.setImageResource(android.R.drawable.presence_busy);
            } else if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.ERROR.name())) {
                mStatusImgView.setImageResource(android.R.drawable.presence_busy);
            } else if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.COMPLETED.name())) {
                mStatusImgView.setImageResource(android.R.drawable.presence_online);
            } else if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.RUNNING.name())) {
                mStatusImgView.setImageResource(android.R.drawable.ic_media_play);
            } else if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.PAUSED.name())) {
                mStatusImgView.setImageResource(android.R.drawable.ic_media_pause);
            } else if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.PENDIND_PAUSE.name())) {
                mStatusImgView.setImageResource(android.R.drawable.ic_media_pause);
            } else if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.CANCELED.name())) {
                mStatusImgView.setImageResource(android.R.drawable.presence_away);
            } else if (mRun.getResultStatusType().equalsIgnoreCase(RunStatus.PENDING_CANCEL.name())) {
                mStatusImgView.setImageResource(android.R.drawable.presence_away);
            }

            Date date = new Date(mRun.getRunStartTime());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            String formatted = format.format(date);

            if (formatted != null) {
                mStartTextView.setText(formatted);
            } else {
                mStartTextView.setText("<start_date_and_time>");
            }

            Long myDuration = mRun.getDuration();
            if (myDuration != null) {
                mDurationTextView.setText(Long.toString(myDuration));
            } else {
                mDurationTextView.setText("<duration>");
            }
        }

        protected Run getRun() {
            return mRun;
        }

        @Override
        public void onClick(View v) {
            Intent intent = InputListActivity.newIntent(getActivity(), mRun.getId());
            startActivity(intent);
        }
    }

    private class RunAdapter extends RecyclerView.Adapter<RunHolder> {
        public RunAdapter(List<Run> runs) {
            mRuns = runs;
        }

        @Override
        public int getItemCount() {
            return mRuns.size();
        }

        @Override
        public RunHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_run, parent, false);
            return new RunHolder(view);
        }

        @Override
        public void onBindViewHolder(RunHolder holder, int position) {
            Run run = mRuns.get(position);
            holder.bindRun(run);
        }
    }

   class FetchItemsTask extends AsyncTask<Void, Void, List<Run>> {
        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<Run> doInBackground(Void... params) {
            if (mQuery == null || mQuery.isEmpty()) {
                return mOOConnector.getAllRuns();
            } else {
                return mOOConnector.searchRuns(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<Run> runs) {
            Log.i(TAG, "Received " + runs.size() + " runs");
            mRuns = runs;
            setupAdapter();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            mRunRecyclerView.setAdapter(new RunAdapter(mRuns));
        }
    }
}