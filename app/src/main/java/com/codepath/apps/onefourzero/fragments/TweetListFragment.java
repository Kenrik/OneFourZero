package com.codepath.apps.onefourzero.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.adapters.TweetsArrayAdapter;
import com.codepath.apps.onefourzero.models.Tweet;
import com.codepath.apps.onefourzero.support.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

public class TweetListFragment extends Fragment {

    private ListView lvTweets;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private SwipeRefreshLayout swipeContainer;
    TweetListListener listener;

    public interface TweetListListener {
        void onTweetTapped(Tweet tweet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int sinceCurrentCount) {
               loadTweetsForPage(page);
            }
        });
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvTweets.setAdapter(aTweets);

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = (Tweet) tweets.get(position);
                listener.onTweetTapped(tweet);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(),tweets);
        listener = (TweetListListener) getActivity();
    }

    public void loadTweetsForPage(int page) {
    }

    public void reload() {
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void clear() {
        aTweets.clear();
    }
}
