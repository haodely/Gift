package com.example.gift.utils;



import com.example.gift.R;
import com.example.gift.bean.GiftBean;

import java.util.ArrayList;
import java.util.List;

public class GiftDateUtlis {

    public static List<GiftBean> initGiftDate(){
        List<GiftBean> giftList = new ArrayList<GiftBean>();
        int img[] = {R.mipmap.yipitiezhi001,R.mipmap.yipitiezhi002,R.mipmap.yipitiezhi006,R.mipmap.yipitiezhi007,
                R.mipmap.yipitiezhi012,R.mipmap.yipitiezhi013,R.mipmap.yipitiezhi021,R.mipmap.yipitiezhi024};
        String msg[] = {"送一了朵鲜花","送了一个糖果","送了一颗钻戒","送了一面魔镜",
                "送了一匹骏马","送了一束气球","送了一颗星星","送了一个香吻"};
        String coin[] = {"1","5","10","20","50","100","500","1000"};
        String types[] = {"1","2","3","4","5","6","7","8"};
        for(int i=0;i<8;i++){
            GiftBean mGiftBean = new GiftBean();
            mGiftBean.setSelect("0");
            mGiftBean.setImgID(img[i]);
            mGiftBean.setCoin(coin[i]);
            mGiftBean.setMsg(msg[i]);
            mGiftBean.setTypes(types[i]);
            giftList.add(mGiftBean);
        }
        return giftList;
    }

//    public static int getGiftImgID(String msg){
//        int imgID = -1;
//        List<GiftBean> giftList = initGiftDate();
//        for(int i = 0; i<giftList.size();i++){
//            if(msg.equals(giftList.get(i).getMsg())){
//                imgID = giftList.get(i).getImgID();
//            }
//        }
//        return imgID;
//    }
}
