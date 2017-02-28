package com.example.gift;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gift.adapter.GiftAdapter;
import com.example.gift.bean.GiftBean;
import com.example.gift.utils.GiftDateUtlis;
import com.example.gift.view.CustomRoundView;
import com.example.gift.view.GiftGridView;
import com.example.gift.view.MagicTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    /**
     * 送礼物
     */
    private LinearLayout llgiftcontent;
    /**
     * 动画相关
     */
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;
    /**
     * 数据相关
     */
    private List<View> giftViewCollection = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGift();
    }

    public void ShowGiftClick(View view) {
        showGiftDialog();
    }

    private List<GiftBean> giftList = new ArrayList<GiftBean>();
    private GiftGridView gv_gift;
    private GiftAdapter mDialogGiftAdapter;
    private int select_gift = -1;

    private void showGiftDialog() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_gift_view, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(MainActivity.this, R.style.common_dialog);
        dialog.setContentView(view);
        dialog.show();

        // 监听
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.dialog_gift_btn_gifts:
                        // 取消
                        if (select_gift < 0) {
                            Toast.makeText(MainActivity.this, "请选择您要赠送的礼物", Toast.LENGTH_SHORT).show();
                        } else {
                            showGift(giftList.get(select_gift).getTypes() + giftList.get(select_gift).getMsg(), giftList.get(select_gift).getTypes(), giftList.get(select_gift).getMsg(), giftList.get(select_gift).getImgID());
                        }
                        break;

                }
            }

        };
        Button btn_gifts = (Button) view.findViewById(R.id.dialog_gift_btn_gifts);
        btn_gifts.setTextColor(getResources().getColor(R.color.white_F));
        btn_gifts.setOnClickListener(listener);

        gv_gift = (GiftGridView) view.findViewById(R.id.dialog_gift_gv_gift);
        gv_gift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < giftList.size(); i++) {
                    if (i == position) {
                        giftList.get(i).setSelect("1");
                    } else {
                        giftList.get(i).setSelect("0");
                    }
                    select_gift = position;
                }
                mDialogGiftAdapter.notifyDataSetChanged();
            }
        });
        select_gift = -1;
        giftList.clear();
        giftList = GiftDateUtlis.initGiftDate();//初始化礼物列表
        mDialogGiftAdapter = new GiftAdapter(MainActivity.this, giftList);
        gv_gift.setAdapter(mDialogGiftAdapter);

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);

    }


    private void initGift() {
        llgiftcontent = (LinearLayout) findViewById(R.id.llgiftcontent);


        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_out);
        giftNumAnim = new NumAnim();
        clearTiming();
    }

    /**
     * 添加礼物view,(考虑垃圾回收)
     */
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            /*如果垃圾回收中没有view,则生成一个*/
            view = LayoutInflater.from(this).inflate(R.layout.item_gift, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            view.setLayoutParams(lp);
            llgiftcontent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    giftViewCollection.add(view);
                }
            });
        } else {
            view = giftViewCollection.get(0);
            giftViewCollection.remove(view);
        }
        return view;
    }

    /**
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = llgiftcontent.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llgiftcontent.removeViewAt(index);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }

    /**
     * 显示礼物的方法
     */
    private void showGift(String tag, String name, String msg, int imgID) {
        View giftView = llgiftcontent.findViewWithTag(tag);
        if (giftView == null) {/*该用户不在礼物显示列表*/

            if (llgiftcontent.getChildCount() > 2) {/*如果正在显示的礼物的个数超过两个，那么就移除最后一次更新时间比较长的*/
                View giftView1 = llgiftcontent.getChildAt(0);
                CustomRoundView picTv1 = (CustomRoundView) giftView1.findViewById(R.id.crvheadimage);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = llgiftcontent.getChildAt(1);
                CustomRoundView picTv2 = (CustomRoundView) giftView2.findViewById(R.id.crvheadimage);
                long lastTime2 = (Long) picTv2.getTag();
                if (lastTime1 > lastTime2) {/*如果第二个View显示的时间比较长*/
                    removeGiftView(1);
                } else {/*如果第一个View显示的时间长*/
                    removeGiftView(0);
                }
            }

            giftView = addGiftView();/*获取礼物的View的布局*/
            giftView.setTag(tag);/*设置view标识*/

            CustomRoundView crvheadimage = (CustomRoundView) giftView.findViewById(R.id.crvheadimage);
            final TextView item_gift_tv_name = (TextView) giftView.findViewById(R.id.item_gift_tv_name);/*找到name*/
            item_gift_tv_name.setText(name);
            final TextView item_gift_tv_msg = (TextView) giftView.findViewById(R.id.item_gift_tv_msg);/*找到name*/
            item_gift_tv_msg.setText(msg);
            final ImageView gift_img_gift = (ImageView) giftView.findViewById(R.id.gift_img_gift);/*找到name*/
            gift_img_gift.setImageDrawable(getResources().getDrawable(imgID));
            final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            giftNum.setText("x1");/*设置礼物数量*/
            crvheadimage.setTag(System.currentTimeMillis());/*设置时间标记*/
            giftNum.setTag(1);/*给数量控件设置标记*/

            llgiftcontent.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            llgiftcontent.invalidate();/*刷新该view*/
            giftView.startAnimation(inAnim);/*开始执行显示礼物的动画*/
            inAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    giftNumAnim.start(giftNum);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {/*该用户在礼物显示列表*/
            CustomRoundView crvheadimage = (CustomRoundView) giftView.findViewById(R.id.crvheadimage);/*找到头像控件*/
            final TextView item_gift_tv_name = (TextView) giftView.findViewById(R.id.item_gift_tv_name);/*找到name*/
            item_gift_tv_name.setText(name);
            final TextView item_gift_tv_msg = (TextView) giftView.findViewById(R.id.item_gift_tv_msg);/*找到name*/
            item_gift_tv_msg.setText(msg);
            final ImageView gift_img_gift = (ImageView) giftView.findViewById(R.id.gift_img_gift);/*找到name*/
            gift_img_gift.setImageDrawable(getResources().getDrawable(imgID));
            MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            int showNum = (Integer) giftNum.getTag() + 1;
            giftNum.setText("x" + showNum);
            giftNum.setTag(showNum);
            crvheadimage.setTag(System.currentTimeMillis());
            giftNumAnim.start(giftNum);
        }
    }

    /**
     * 定时清除礼物
     */
    private void clearTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int count = llgiftcontent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = llgiftcontent.getChildAt(i);
                    CustomRoundView crvheadimage = (CustomRoundView) view.findViewById(R.id.crvheadimage);
                    long nowtime = System.currentTimeMillis();
                    long upTime = (Long) crvheadimage.getTag();
                    if ((nowtime - upTime) >= 3000) {
                        removeGiftView(i);
                        return;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);
    }

    /**
     * 数字放大动画
     */
    public class NumAnim {
        private Animator lastAnimator = null;

        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 2.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 2.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }


}
