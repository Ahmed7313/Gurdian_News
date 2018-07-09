package com.example.android.gurdian_news_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends ArrayAdapter <News>  {


    public NewsAdapter(@NonNull Context context, @NonNull List<News> newsData) {
        super(context, 0, newsData);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        News currentnews = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adpetr_body, parent, false);
        }

        ImageView articleImageView = convertView.findViewById(R.id.article_image_view);
        String imageUrl = currentnews.getImageUrl();

        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(articleImageView);
        } else {
            Picasso.get().load(R.drawable.default_background).into(articleImageView);
        }

        TextView sectionTextView = convertView.findViewById(R.id.article_section_text_view);
        sectionTextView.setText(currentnews.getSection());

        TextView articleAuthorTextView = convertView.findViewById(R.id.article_author_text_view);
        String author = currentnews.getAuthorName();

        if (author != null) {
            articleAuthorTextView.setText(author);
        } else {
            articleAuthorTextView.setVisibility(View.GONE);
        }

        TextView articleTitleTextView = convertView.findViewById(R.id.article_title_text_view);
        articleTitleTextView.setText(currentnews.getWebTitle());

        TextView articleDateTextView = convertView.findViewById(R.id.article_date_text_view);
        articleDateTextView.setText(currentnews.getFormatedDate());

        return convertView;

    }

}
