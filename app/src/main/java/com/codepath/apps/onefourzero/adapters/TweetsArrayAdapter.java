package com.codepath.apps.onefourzero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.models.Tweet;
import com.codepath.apps.onefourzero.support.CircleTransform;
import com.joanzapata.android.iconify.Iconify;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView itvRetweet;
        TextView tvUserName;
        TextView tvRetweet;
        TextView itvReply;
        TextView itvStar;
        TextView tvBody;
        TextView tvTime;
        TextView tvStar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.itvRetweet = (TextView) convertView.findViewById(R.id.itvRetweet);
            viewHolder.itvReply = (TextView) convertView.findViewById(R.id.itvReply);
            viewHolder.itvStar = (TextView) convertView.findViewById(R.id.itvStar);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tvRetweet = (TextView) convertView.findViewById(R.id.tvRetweet);
            viewHolder.tvStar = (TextView) convertView.findViewById(R.id.tvStar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvTime.setText(tweet.getCreatedAt());
        viewHolder.tvUserName.setText(tweet.getUser().getScreenName());
        viewHolder.tvRetweet.setText(tweet.getRetweets());
        viewHolder.tvStar.setText(tweet.getStars());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).transform(new CircleTransform()).into(viewHolder.ivProfileImage);
        Iconify.addIcons(viewHolder.itvStar);
        Iconify.addIcons(viewHolder.itvReply);
        Iconify.addIcons(viewHolder.itvRetweet);
        return convertView;
    }
}
