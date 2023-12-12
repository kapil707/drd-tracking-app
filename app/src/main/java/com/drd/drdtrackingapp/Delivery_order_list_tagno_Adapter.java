package com.drd.drdtrackingapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Delivery_order_list_tagno_Adapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Delivery_order_list_tagno_get_or_set> arrayitems;

    public Delivery_order_list_tagno_Adapter(Context context, List<Delivery_order_list_tagno_get_or_set> arraylist)
    {
        this.context = context;
        this.arrayitems = arraylist;
    }
 
    @Override
	public int getCount() {
		return arrayitems.size();
	}
    
   
    @Override
	public View getView(int position, View view, ViewGroup parent) 
    {
		// TODO Auto-generated method stub
       
        LayoutInflater abc = ((Activity) context).getLayoutInflater();
		View itemView = abc.inflate(R.layout.delivery_list_by_tagno_item, null,true);
		final Delivery_order_list_tagno_get_or_set m = arrayitems.get(position);

        LinearLayout select_chemist_LinearLayout = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout);
        TextView select_b_lbl1 = (TextView) itemView.findViewById(R.id.select_b_lbl1);
        TextView select_b_lbl2 = (TextView) itemView.findViewById(R.id.select_b_lbl2);
        TextView select_b_lbl3 = (TextView) itemView.findViewById(R.id.select_b_lbl3);
        TextView select_b_lbl4 = (TextView) itemView.findViewById(R.id.select_b_lbl4);
        TextView select_b_lbl5 = (TextView) itemView.findViewById(R.id.select_b_lbl5);


        LinearLayout select_chemist_LinearLayout1 = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout1);
        TextView select_w_lbl1 = (TextView) itemView.findViewById(R.id.select_w_lbl1);
        TextView select_w_lbl2 = (TextView) itemView.findViewById(R.id.select_w_lbl2);
        TextView select_w_lbl3 = (TextView) itemView.findViewById(R.id.select_w_lbl3);
        TextView select_w_lbl4 = (TextView) itemView.findViewById(R.id.select_w_lbl4);
        TextView select_w_lbl5 = (TextView) itemView.findViewById(R.id.select_w_lbl5);
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

        select_b_lbl1.setText(m.gstvno());
        select_b_lbl2.setText(m.mydate());
        select_b_lbl3.setText(m.chemist_code()+"-"+m.chemist_name());
        select_b_lbl4.setText("Amount :" +m.amount() +"/-");
        //select_medicine.setText(m.medicine_items());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            select_b_lbl5.setText(Html.fromHtml(m.medicine_items(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            select_b_lbl5.setText(Html.fromHtml(m.medicine_items()));
        }

        select_w_lbl1.setText(m.gstvno());
        select_w_lbl2.setText(m.mydate());
        select_w_lbl3.setText(m.chemist_code()+"-"+m.chemist_name());
        select_w_lbl4.setText("Amount :" +m.amount() +"/-");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            select_w_lbl5.setText(Html.fromHtml(m.medicine_items(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            select_w_lbl5.setText(Html.fromHtml(m.medicine_items()));
        }

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