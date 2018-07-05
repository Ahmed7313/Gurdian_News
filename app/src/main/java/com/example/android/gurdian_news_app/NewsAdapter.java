package com.example.android.gurdian_news_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter <News>  {

    private static final String LOCATION_SEPARATOR = "T";

    public NewsAdapter(@NonNull Context context, @NonNull List<News> newsData) {
        super(context, 0, newsData);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get the nwes object at postion on the adapter
        News currentNews = getItem(position);
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.adpetr_body,parent,false);
        }

        TextView sectionName = (TextView)listItemView.findViewById(R.id.sectionName);
        sectionName.setText(currentNews.getSectionName());

        String webPublicationDate = currentNews.getWebPublicationDate();
        String date = "";
        String time = "";

        if (webPublicationDate.contains(LOCATION_SEPARATOR)) {
            String[] parts = webPublicationDate.split(LOCATION_SEPARATOR);
            date = parts[0] + LOCATION_SEPARATOR;
            time = parts[1];
        }


        TextView dateOfNews = (TextView)listItemView.findViewById(R.id.date);
        String dateString = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        Date dateObject = new Date();
        try {
            dateObject = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = dateFormat.format(dateObject);
        dateOfNews.setText(formattedDate);

        TextView timeOfNews = (TextView)listItemView.findViewById(R.id.time);
        String TimeString = time;
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        Date timeObject = new Date();
        try {
            timeObject = timeFormat.parse(TimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedTime = timeFormat.format(timeObject);
        timeOfNews.setText(formattedTime);

        TextView mainTitle = (TextView) listItemView.findViewById(R.id.news_title);
        mainTitle.setText(currentNews.getWebTitle());

        TextView author = (TextView) listItemView.findViewById(R.id.author_name);
        author.setText(currentNews.getAuthorName());
        return listItemView;
    }

}
