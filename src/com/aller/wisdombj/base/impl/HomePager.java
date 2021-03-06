package com.aller.wisdombj.base.impl;

import com.aller.wisdombj.base.BasePager;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
*	版权: Aller所有
*
*	作者 : Aller
*
*	E-mail : Aller_Dong@163.com
*
*	版本 : 1.0
*
*	时间 : 2016年5月4日下午7:43:32
*
*	描述 : 
*
*
*
*/

public class HomePager extends BasePager{

	public HomePager(Activity activity) {
		super(activity);
	}
	
	@Override
	public void initData(){
		tvTitle.setText("智慧北京");
		btnMenu.setVisibility(View.INVISIBLE);
		setSlidingMenuEnable(false);//没有侧滑菜单
		
		TextView textView = new TextView(mActivity) ;
		textView.setText("首页");
		textView.setTextColor(Color.RED);
		textView.setTextSize(25);
		textView.setGravity(Gravity.CENTER);//显示的布局居中
		
		//向FrameLayout中动态添加布局
		flContent.addView(textView);
	}
}
