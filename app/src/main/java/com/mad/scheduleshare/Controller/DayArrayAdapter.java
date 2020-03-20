package com.mad.scheduleshare.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mad.scheduleshare.R;

import java.util.ArrayList;

/**
 * Controller between UI and Day Schedule Data.
 */
public class DayArrayAdapter extends ArrayAdapter<String> {


    private static class ViewHolder {
        TextView mName;
    }

    public DayArrayAdapter(Context context, ArrayList<String> days) {
        super(context, R.layout.day_item, days);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        String day = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.day_item, parent, false);

            viewHolder.mName = (TextView) convertView.findViewById(R.id.day_item_name);

            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object into the template view.
        viewHolder.mName.setText(day);

        // Return the completed view to render on screen
        return convertView;
    }

}
