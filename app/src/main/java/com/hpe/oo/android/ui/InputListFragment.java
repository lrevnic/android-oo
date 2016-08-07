package com.hpe.oo.android.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hpe.oo.android.OOConnector;
import com.hpe.oo.android.model.FlowInput;
import com.hpe.oo.android.oo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by revnic on 7/30/2016.
 */
public class InputListFragment extends Fragment {
        private static final String  TAG = "InputListFragment";

        private static Long        sRunId;
        private List<FlowInput>    mFlowInputs;
        private RecyclerView       mInputRecyclerView;
        private OOConnector        mOOConnector;

        public static InputListFragment newInstance(Long runId) {
            Bundle args                         = new Bundle();
            InputListFragment inputListFragment = new InputListFragment();
            inputListFragment.setArguments(args);
            sRunId = runId;
            return inputListFragment;
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
            View view = inflater.inflate(R.layout.fragment_input_list, container, false);
            mInputRecyclerView = (RecyclerView) view.findViewById(R.id.input_recycler_view);
            mInputRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            updateUI();

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();

            updateUI();
        }

        private void updateUI() {
            new FetchFlowInputsTask().execute();
        }

        private class FlowInputHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public  TextView  mNameTextView;
            public  TextView  mValueTextView;
            private FlowInput mFlowInput;

            public FlowInputHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

                mNameTextView   = (TextView)   itemView.findViewById(R.id.input_list_item_nameTextView);
                mValueTextView  = (EditText) itemView.findViewById(R.id.input_list_item_valueTextField);
            }

            public void bindInput(FlowInput flowInput){
                mFlowInput = flowInput;

                mNameTextView.setText(mFlowInput.getName());
                mValueTextView.setText(mFlowInput.getValue());
            }

            protected FlowInput getFlowInput() {
                return mFlowInput;
            }

            @Override
            public void onClick(View v) {

            }
        }

        private class InputAdapter extends RecyclerView.Adapter<FlowInputHolder> {
            public InputAdapter(List<FlowInput> flowInputs) {
                mFlowInputs = flowInputs;
            }

            @Override
            public int getItemCount() {
                return mFlowInputs.size();
            }

            @Override
            public FlowInputHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.list_item_input, parent, false);
                return new FlowInputHolder(view);
            }

            @Override
            public void onBindViewHolder(FlowInputHolder holder, int position) {
                FlowInput flowInput = mFlowInputs.get(position);
                holder.bindInput(flowInput);
            }
        }

        class FetchFlowInputsTask extends AsyncTask<Void, Void, List<FlowInput>> {
            public FetchFlowInputsTask() {
            }

            @Override
            protected List<FlowInput> doInBackground(Void... params) {
                //TODO: Remove dummy inputs generation
                List<FlowInput> myFlowInputs = new ArrayList<>();
                for (int i = 0; i < 10; ++i) {
                    FlowInput myFlowInput = new FlowInput();
                    myFlowInput.setName("Input " + i);
                    myFlowInput.setLabel("Label " + i);
                    myFlowInput.setValue(Integer.toString(i));
                    myFlowInputs.add(myFlowInput);
                }

                return myFlowInputs;
            }

            @Override
            protected void onPostExecute(List<FlowInput> inputs) {
                Log.i(TAG, "Received " + inputs.size() + " inputs");
                mFlowInputs = inputs;
                setupAdapter();
            }
        }

        private void setupAdapter() {
            if (isAdded()) {
                mInputRecyclerView.setAdapter(new InputAdapter(mFlowInputs));
            }
        }
    }
