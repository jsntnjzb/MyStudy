package com.home.mystudy.Activity;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.home.mystudy.R;

import java.util.ArrayList;

/**
 * 通信
 * */
public class SocketActivity extends Activity {
    private GridView gridView;
    private ArrayList items = new ArrayList(){};
    private GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        items.add("HandlerThread");
        items.add("Volley");
        gridView = (GridView)findViewById(R.id.gridView);
        if(gridViewAdapter==null){
            gridViewAdapter = new GridViewAdapter(items,SocketActivity.this);
        }
        gridView.setAdapter(gridViewAdapter);
    }

    private class GridViewAdapter extends BaseAdapter {
        Context mContext;
        ArrayList items;
        private int clickTemp = -1;

        public GridViewAdapter(ArrayList mItems, Context mContext) {
            this.items = mItems;
            this.mContext = mContext;
        }


        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView==null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_grid_item,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.tv_item_name = (TextView)convertView.findViewById(R.id.tv_item);
                viewHolder.tv_item_name.setText((CharSequence) items.get(position));
                convertView.setTag(viewHolder);

            }else {
                viewHolder = (ViewHolder)convertView.getTag();
//                viewHolder.tv_item_name
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }


        @Override
        public boolean isEmpty() {
            return false;
        }

        public void setSelection(int position){
            clickTemp = position;
        }

    }

    class ViewHolder{
        TextView tv_item_name;
    }

}
