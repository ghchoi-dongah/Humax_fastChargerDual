package com.dongah.fastcharger.controlboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dongah.fastcharger.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ListViewDspAdapter extends BaseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ListViewDspAdapter.class);

    ArrayList<ListViewItem> listViewItems = new ArrayList<>();

    public ListViewDspAdapter() {
    }


    public void addItem(String type, String title, String value) {
        try {
            ListViewItem listViewItem = new ListViewItem();
            listViewItem.setItemType(type);
            listViewItem.setItemTitle(title);
            listViewItem.setItemValue(value);
            listViewItems.add(listViewItem);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void clearItem() {
        if (getCount() > 0) {
            listViewItems.clear();
        }
    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final Context context = parent.getContext();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_control_item, parent, false);
            }
            TextView itemType = (TextView) convertView.findViewById(R.id.item_type);
            TextView itemTitle = (TextView) convertView.findViewById(R.id.item_title);
            TextView itemValue = (TextView) convertView.findViewById(R.id.item_value);
            ListViewItem listViewItem = listViewItems.get(position);
            itemType.setText(listViewItem.getItemType());
            itemTitle.setText(listViewItem.getItemTitle());
            itemValue.setText(listViewItem.getItemValue());
            return convertView;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
