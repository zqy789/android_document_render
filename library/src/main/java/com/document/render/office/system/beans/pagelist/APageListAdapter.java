
package com.document.render.office.system.beans.pagelist;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class APageListAdapter extends BaseAdapter {

    private APageListView listView;


    public APageListAdapter(APageListView view) {
        this.listView = view;
    }


    public int getCount() {
        return listView.getPageCount();
    }


    public Object getItem(int position) {
        return null;
    }


    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        APageListItem pageItem = (APageListItem) convertView;
        Rect pageSize = listView.getPageListViewListener().getPageSize(position);
        if (convertView == null) {
            pageItem = listView.getPageListItem(position, convertView, parent);
            if (pageItem == null) {
                pageSize.right = 794;
                pageSize.bottom = 1124;
                pageItem = new APageListItem(listView, pageSize.width(), pageSize.height());
            }
        }
        pageItem.setPageItemRawData(position, pageSize.width(), pageSize.height());
        return pageItem;
    }


    public void dispose() {
        listView = null;
    }
}
