package com.drd.drdtrackingapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Delivery_list_Adapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Delivery_list_get_or_set> movieItems;

    public Delivery_list_Adapter(Context context, List<Delivery_list_get_or_set> arraylist)
    {
        this.context = context;
        this.movieItems = arraylist;
    }
 
    @Override
	public int getCount() {
		return movieItems.size();
	}
    
   
    @Override
	public View getView(int position, View view, ViewGroup parent) 
    {
		// TODO Auto-generated method stub
       
        LayoutInflater abc = ((Activity) context).getLayoutInflater();
		View itemView = abc.inflate(R.layout.delivery_list_item, null,true);
		final Delivery_list_get_or_set m = movieItems.get(position);

        LinearLayout select_chemist_LinearLayout = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout);
        TextView select_chemist_name = (TextView) itemView.findViewById(R.id.select_chemist_name);
        TextView select_chemist_altercode = (TextView) itemView.findViewById(R.id.select_chemist_altercode);
        TextView select_amt = (TextView) itemView.findViewById(R.id.select_amt);
        TextView select_gstvno = (TextView) itemView.findViewById(R.id.select_gstvno);


        LinearLayout select_chemist_LinearLayout1 = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout1);
        TextView select_chemist_name1 = (TextView) itemView.findViewById(R.id.select_chemist_name1);
        TextView select_chemist_altercode1 = (TextView) itemView.findViewById(R.id.select_chemist_altercode1);
        TextView select_amt1 = (TextView) itemView.findViewById(R.id.select_amt1);
        TextView select_gstvno1 = (TextView) itemView.findViewById(R.id.select_gstvno1);
        int intid = 0;
        intid = Integer.valueOf(m.intid());
        if(intid%2==0)
        {
            select_chemist_LinearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            select_chemist_LinearLayout1.setVisibility(View.VISIBLE);
        }

        select_chemist_name.setText(m.mytagno());
        select_chemist_altercode.setText(m.mydate());
        select_gstvno.setText(m.mytime());

        select_chemist_name1.setText(m.mytagno());
        select_chemist_altercode1.setText(m.mydate());
        select_gstvno1.setText(m.mytime());

        return itemView;
    }

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}