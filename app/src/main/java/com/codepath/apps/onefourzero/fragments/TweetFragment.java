package com.codepath.apps.onefourzero.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.models.Tweet;
import com.codepath.apps.onefourzero.models.User;
import com.codepath.apps.onefourzero.support.CircleTransform;
import com.squareup.picasso.Picasso;

public class TweetFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private Button btnSave;
    private TextView tvUser;
    private EditText etTweet;
    private ImageView ivProfileImage;
    private Tweet tweet;
    private User user;

    public interface TweetDialogListener {
        void onFinishTweetDialog(Tweet tweet);
    }

    public TweetFragment() { }

    public static TweetFragment newInstance(String title) {
        TweetFragment frag = new TweetFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container);
        Bundle bundle = this.getArguments();
        tweet = bundle.getParcelable("tweet");
        if (tweet != null) { user = tweet.getUser(); }
        return setupView(view);
    }

    private View setupView(final View view) {

        if (user != null) {
            ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
            tvUser = (TextView) view.findViewById(R.id.tvUserName);
            tvUser.setText(user.getScreenName());
            Picasso.with(view.getContext()).load(user.getProfileImageUrl()).transform(new CircleTransform()).into(ivProfileImage);
        }
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        etTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Bundle bundle = this.getArguments();
        String hint = getArguments().getString("hint");
        etTweet.setHint(hint);
        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetDialogListener listener = (TweetDialogListener) getActivity();
                tweet.setBody(etTweet.getText().toString());
                listener.onFinishTweetDialog(tweet);
                dismiss();
            }
        });
        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            dismiss();
            return true;
        }
        return false;
    }
}
