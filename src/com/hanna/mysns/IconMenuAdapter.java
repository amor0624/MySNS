package com.hanna.mysns;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconMenuAdapter extends ArrayAdapter<IconMenu> {
	
	Context context;
	int layoutResourceId;
	IconMenu data[] = null;

	public IconMenuAdapter(Context context, int layoutResourceId, IconMenu[] data) {
	    super(context, layoutResourceId, data);
	    this.layoutResourceId = layoutResourceId;
	    this.context = context;
	    this.data = data;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View row  = convertView;
		IconMenuHolder holder = null;
		
		if (row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new IconMenuHolder();
			holder.imgIcon = (ImageView)row.findViewById(R.id.iconMenuImage);
			holder.txtTitle = (TextView)row.findViewById(R.id.iconMenuTitle);
			
			row.setTag(holder);
		} else {
			holder = (IconMenuHolder)row.getTag();
		}
		
		IconMenu iconMenu = data[position];
		holder.txtTitle.setText(iconMenu.title);
		holder.imgIcon.setImageResource(iconMenu.iconResId);
		
	    return row;
    }

	static class IconMenuHolder{
		ImageView imgIcon;
		TextView txtTitle;
	}
	
}
