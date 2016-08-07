package com.hpe.oo.android.ui;

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
import android.widget.TextView;

import com.hpe.oo.android.OOConnector;
import com.hpe.oo.android.model.Flow;
import com.hpe.oo.android.oo.R;
import com.hpe.oo.android.utils.QueryPreferences;

import java.util.List;

public class FlowLaunchFragment extends Fragment {
    private static final String  TAG = "FlowLaunchFragment";

    private List<Flow>           mFlows;
    private RecyclerView         mFlowRecyclerView;

    private static OOConnector sMOOConnector;

    public static FlowLaunchFragment newInstance() {
        Bundle args                           = new Bundle();
        FlowLaunchFragment flowLaunchFragment = new FlowLaunchFragment();
        flowLaunchFragment.setArguments(args);
        return flowLaunchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        sMOOConnector = OOConnector.newInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flow_list, container, false);

        mFlowRecyclerView = (RecyclerView) view.findViewById(R.id.flow_recycler_view);
        mFlowRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

        inflater.inflate(R.menu.menu_flow_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_flow_search);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchFlowsTask(query).execute();
    }

    private class FlowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Flow       mFlow;
        public  TextView   mFlowNameTextView;
        public  TextView   mFlowDescrTextView;

        public FlowHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mFlowNameTextView = (TextView) itemView.findViewById(R.id.flow_list_item_nameTextView);
            mFlowDescrTextView = (TextView) itemView.findViewById(R.id.flow_list_item_descriptionTextView);
        }

        public void bindFlow(Flow flow){
            mFlow = flow;
            mFlowNameTextView.setText(flow.getFlowName());
            mFlowDescrTextView.setText(flow.getFlowDescription());
        }

        protected Flow getFlow() {
            return mFlow;
        }

        @Override
        public void onClick(View v) {
        }
    }

    private class FlowAdapter extends RecyclerView.Adapter<FlowHolder> {
        private List<Flow> mFlows;
        public FlowAdapter(List<Flow> flows) {
            mFlows = flows;
        }

        @Override
        public int getItemCount() {
            return mFlows.size();
        }

        @Override
        public FlowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_flow, parent, false);
            return new FlowHolder(view);
        }

        @Override
        public void onBindViewHolder(FlowHolder holder, int position) {
            Flow flow = mFlows.get(position);
            holder.bindFlow(flow);
        }
    }

    class FetchFlowsTask extends AsyncTask<Void, Void, List<Flow>> {
        private String mQuery;

        public FetchFlowsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<Flow> doInBackground(Void... params) {
            if (mQuery == null || mQuery.isEmpty()) {
                return sMOOConnector.getAllFlows();
            } else {
                return sMOOConnector.searchFlows(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<Flow> flows) {
            Log.i(TAG, "Received " + flows.size() + " flows");
            mFlows = flows;
            setupAdapter();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            mFlowRecyclerView.setAdapter(new FlowAdapter(mFlows));
        }
    }
}

