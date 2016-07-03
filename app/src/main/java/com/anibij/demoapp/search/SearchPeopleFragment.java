package com.anibij.demoapp.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.anibij.demoapp.R;
import com.anibij.demoapp.Utils.ConnectionDetector;
import com.anibij.demoapp.model.MentionListViewAdapter;
import com.anibij.demoapp.model.Status;
import com.anibij.demoapp.view.AlertDialogManager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class SearchPeopleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    private OnFragmentInteractionListener mListener;

    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";

    private static SharedPreferences mSharedPreferences;
    SQLiteDatabase db;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";


    // Declare Variables
    ListView listview;
    View view;
    ProgressDialog mProgressDialog;
    MentionListViewAdapter adapter;
    private List<Status> statusList = null;
    SwipeRefreshLayout refreshLayout;
    ConnectionDetector cd;
    Context mContext;
    LinearLayout mBottomLinearLayout;
    TextView mNoRecordView;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    public SearchPeopleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MentionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPeopleFragment newInstance(String param1, String param2) {
        SearchPeopleFragment fragment = new SearchPeopleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSharedPreferences = getActivity().getSharedPreferences(PREF_NAME, 0);
        mContext = getActivity();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mentions, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe_refresh);
        mNoRecordView = (TextView) view.findViewById(R.id.empty_view);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLayout.setRefreshing(false);
                cd = new ConnectionDetector(mContext);
                boolean isInternetAvailable = cd.isConnectingToInternet();

                // Toast.makeText(mContext,"Internet Available? "+isInternetAvailable,Toast.LENGTH_SHORT).show();

                if (!isInternetAvailable) {

                    //refreshLayout.setRefreshing(false);

                    alert.showAlertDialog(mContext, "Internet Connection Error",
                            "Please connect to working Internet Connection", false);

                    // stop executing code by return

                    return;
                }

                SearchPeopleFragment.this.doRefresh();
            }

        });


        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void doRefresh() {
        new RemoteDataTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, List<Status>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Retrieving Mentions...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected List<com.anibij.demoapp.model.Status> doInBackground(Void... params) {
            // Create the array
            statusList = new ArrayList<com.anibij.demoapp.model.Status>();
            try {

                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                List<twitter4j.Status> statuses = twitter.getMentionsTimeline();

                for (twitter4j.Status returnStatus : statuses) {

                    String id = String.valueOf(returnStatus.getId());
                    String userName = returnStatus.getUser().getName();
                    String screenName = returnStatus.getUser().getScreenName();
                    String profileImage = returnStatus.getUser().getProfileImageURL();
                    long createdAt = returnStatus.getCreatedAt().getTime();
                    String textMessage = returnStatus.getText();
                    int retweetCount = returnStatus.getRetweetCount();
                    int favCount = returnStatus.getFavoriteCount();

                    com.anibij.demoapp.model.Status newStatus =
                            new com.anibij.demoapp.model.Status(id, userName, textMessage, createdAt, profileImage, null, null, retweetCount, favCount, screenName);

                    statusList.add(newStatus);
                }
                return statusList;

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<com.anibij.demoapp.model.Status> result) {

            if (result == null || result.size() <= 0) {

                return;
            }
            // Locate the listview in listview_main.xml
            listview = (ListView) view.findViewById(R.id.mentionListView);
            mNoRecordView.setVisibility(View.GONE);
            // Pass the results into ListViewAdapter.java
            adapter = new MentionListViewAdapter(getActivity(), result);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}
