package com.anibij.demoapp.Utils;

/**
 * Created by bsoren on 15-Jul-16.
 */
public class Blank {

    /*




    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, List<DirectMessage>> {
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
        protected List<com.anibij.demoapp.model.DirectMessage> doInBackground(Void... params) {
            // Create the array
            directMessageList = new ArrayList<DirectMessage>();
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

                List<twitter4j.DirectMessage> directMessages = twitter.getDirectMessages();

                if (directMessages != null) {
                    Log.d(TAG, "Direct Message Size :" + directMessages.size());
                } else {
                    Log.d(TAG, "DirectMessage is null");
                }

                for (twitter4j.DirectMessage dm : directMessages) {

                    long createdAt = dm.getCreatedAt().getTime();
                    long id = dm.getId();
                    String receipientName = dm.getRecipient().getName();
                    long recipientId = dm.getRecipientId();
                    String receipientScreenName = dm.getRecipientScreenName();
                    String recipientImageUrl = dm.getRecipient().getProfileImageURL();
                    String senderName = dm.getSender().getName();
                    long senderId = dm.getSenderId();
                    String senderScreenName = dm.getSenderScreenName();
                    String textMessage = dm.getText();

                    DirectMessage newDirectMessage = new DirectMessage(createdAt, id, receipientName, recipientId,
                            receipientScreenName, senderName, senderId, senderScreenName, textMessage, recipientImageUrl);

                    Log.d(TAG, "Direct message \n " + newDirectMessage.toString());

                    ContentValues contentValue;
                    getContext().getContentResolver().insert(StatusContract.DM_CONTENT_URI,contentValue);

                    directMessageList.add(newDirectMessage);
                }

                return directMessageList;

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(List<com.anibij.demoapp.model.DirectMessage> result) {
            // Locate the listview in listview_main.xml

            if (result == null || result.size() <= 0) {
                return;
            }
            listview = (ListView) view.findViewById(R.id.messageListView);
            noRecordView.setVisibility(View.GONE);
            // Pass the results into ListViewAdapter.java
            adapter = new DirectMessageListViewAdapter(result, mContext);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
     */
}
