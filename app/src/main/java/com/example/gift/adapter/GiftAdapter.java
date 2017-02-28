package com.example.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.gift.R;
import com.example.gift.bean.GiftBean;

import java.util.List;


public class GiftAdapter extends BaseAdapter{
    private List<GiftBean> list ;
    private Context context;
    public GiftAdapter(Context context, List<GiftBean> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder mHolder=null;
        if(convertView==null){

            mHolder=new ViewHolder();

            convertView=inflater.inflate(R.layout.item_dialog_gift,null);

            mHolder.ll_recharge = (RelativeLayout) convertView.findViewById(R.id.item_dialog_gift_ll_gift);
            mHolder.img_gift = (ImageView) convertView.findViewById(R.id.item_dialog_gift_img_gift);
            mHolder.img_select = (ImageView) convertView.findViewById(R.id.item_dialog_gift_img_select);
            mHolder.tv_coins = (TextView) convertView.findViewById(R.id.item_dialog_gift_tv_coins);
            convertView.setTag(mHolder);
        }else {
            mHolder= (ViewHolder) convertView.getTag();
        }

        mHolder.img_gift.setImageDrawable(context.getResources().getDrawable(list.get(position).getImgID()));
        mHolder.tv_coins.setText(list.get(position).getCoin()+"映币");
        if("0".equals(list.get(position).getSelect())){
            mHolder.img_select.setVisibility(View.GONE);
        }else if("1".equals(list.get(position).getSelect())){
            mHolder.img_select.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class ViewHolder{
        private RelativeLayout ll_recharge;
        private ImageView img_gift;
        private ImageView img_select;
        private TextView tv_coins;
    }
}
