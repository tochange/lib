package com.tochange.yang.lib.ui;


import java.lang.reflect.Field;

import com.tochange.yang.lib.log;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

public class ScreenLib {
    
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    
    /**
     * Checks if the device is a tablet or a phone
     * 
     * @param activityContext The Activity Context.
     * @return Returns true if the device is a Tablet
     */
    public static boolean isTabletDevice(Context activityContext)
    {
        // Verifies if the Generalized Size of the device is XLARGE to be
        // considered a Tablet

        // yangxj@20140722
        // boolean xlarge =
        // ((activityContext.getResources().getConfiguration().screenLayout &
        // Configuration.SCREENLAYOUT_SIZE_MASK) ==
        // Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        // If XLarge, checks if the Generalized Density is at least MDPI
        // (160dpi)
        if (xlarge)
        {
            DisplayMetrics metrics = new DisplayMetrics();
            Activity activity = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
            // DENSITY_TV=213, DENSITY_XHIGH=320
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == DisplayMetrics.DENSITY_TV
                    || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
            {

                // Yes, this is a tablet!
                return true;
            }
        }

        // No, this is not a tablet!
        return false;
    }


    public static int getStatusBarHeight(Context cc)
    {
        int ret = -1;
        try
        {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            ret = cc.getResources().getDimensionPixelSize(x);
        }
        catch (Exception e)
        {
            log.e("getStatusBarHeight error");
            e.printStackTrace();
        }
        return ret;
    }

    public static double[] getPhysicInch(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        double result[] = new double[3];
        result[0] = Math.pow(dm.widthPixels / dm.xdpi, 2);
        result[1] = Math.pow(dm.heightPixels / dm.ydpi, 2);
        result[2] = Math.sqrt(result[0] + result[1]);
        return result;
    }
	
	/**
	 * @description 重置layout
	 * @param view  layout对应的控件对象
	 * @param defaultDpi 当前layout匹配的屏幕密度
	 * @param defaultW   当前layout匹配的屏幕分辨率水平像素如1024px
	 * @param defaultH   当前layout匹配的屏幕分辨率垂直像素如 600px
	 */
	public static void resizeLayout(View view, final int defaultDpi, int defaultW, int defaultH){
		//获取当前屏幕分辨率、密度值
		DisplayMetrics metric = new DisplayMetrics();
		((Activity)view.getContext()).getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width  = metric.widthPixels;
		int height = metric.heightPixels;
		//float density  = metric.density;    //屏幕密度        (0.75, 1.0, 1.5, 2)
		int densityDpi = metric.densityDpi; //屏幕密度Dpi(120,  160, 240, 320)
		
		//判断分辨率参数
		if(defaultW == 0){
			defaultW = width;
		}
		if(defaultH == 0){
			defaultH = height;
		}
		
		//计算水平和垂直尺寸倍数
		float wDensity = (float)defaultDpi / (float)densityDpi * (float)width  / (float)defaultW;
		float hDensity = (float)defaultDpi / (float)densityDpi * (float)height / (float)defaultH;;
		
		//判断layout是否包含子控件
		boolean isViewGroup = isViewGroup(view);
		//Log.d(TAG, "机器密度值：" +wDensity + " x " + hDensity +"  分辨率: "  + width + " x " + height +  "  isViewGroup:" + isViewGroup + " 类名：" + view.getClass());	
		
		//递归设置layout及其子控件
		if(isViewGroup){			
			resize(view,wDensity, hDensity, (float)defaultDpi / (float)densityDpi);	
			//Log.d(TAG, ((ViewGroup)view).getChildCount() + "");
			for(int i = 0; i < ((ViewGroup)view).getChildCount(); i++){				
				resizeLayout(((ViewGroup)view).getChildAt(i),defaultDpi, defaultW, defaultH);
			}
		}else{
			resize(view, wDensity, hDensity, (float)defaultDpi / (float)densityDpi);			
		}
	}

	/**
	 * 重置控件布局参数
	 * 
	 * @param view	需要重置大小的View
	 * @param wDpi	水平方向尺寸值放大缩小倍数
	 * @param hDpi	垂直方向尺寸值放大缩小倍数
	 * @param fontDpi
	 */
	private static void resize(View view, final float wDpi, final float hDpi, final float fontDpi){		
		ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();		
		//重置宽度和高度
		if(params != null){
			if(params.width > 0){//小于0 表示 属性为 wrap_content、fill_parent、match_parent
				params.width  = (int) (params.width  * wDpi);
			}
			if(params.height > 0){//小于0 表示 属性为 wrap_content、fill_parent、match_parent
				params.height = (int) (params.height * hDpi);
			}
			view.setLayoutParams(params);
		}
		//重置外边距
		MarginLayoutParams params1 = (MarginLayoutParams) view.getLayoutParams();
		if(params1 != null){
			params1.topMargin    = (int) (params1.topMargin    * hDpi);
			params1.bottomMargin = (int) (params1.bottomMargin * hDpi);
			params1.leftMargin   = (int) (params1.leftMargin   * wDpi);
			params1.rightMargin  = (int) (params1.rightMargin  * wDpi);			
			view.setLayoutParams(params1);
		}
		//重置内边距
		float paddingTop    = view.getPaddingTop()    * hDpi;
		float paddingBottom = view.getPaddingBottom() * hDpi;
		float paddingLeft   = view.getPaddingLeft()   * wDpi;
		float paddingRight  = view.getPaddingRight()  * wDpi;		
		view.setPadding((int)paddingLeft, (int)paddingTop, (int)paddingRight, (int)paddingBottom);	
		
		//重置文本字体大小
		resetFontSize(view, fontDpi);
	}

	/**
	 * 判断控件是否包含子控件
	 * 
	 * @param view	被判断控件对象
	 * @return		true:父控件  false:不是父控件
	 */
	private static boolean isViewGroup(View view){	
		return(view.getClass().equals(FrameLayout.class)
		|| view.getClass().equals(RelativeLayout.class)
		|| view.getClass().equals(LinearLayout.class)
		|| view.getClass().equals(TableLayout.class)
		|| view.getClass().equals(ScrollView.class)
		|| view.getClass().equals(GridView.class)
		|| view.getClass().equals(ExpandableListView.class)
		|| view.getClass().equals(ListView.class));
	}

	/**
	 * 判断控件是否有文本
	 * 
	 * @param view	被判断控件对象
	 * @param dpi
	 */
	private static void resetFontSize(View view, float dpi){
		//Log.d(TAG, "font size x " + dpi);
		if(view.getClass().equals(TextView.class)){			
			((TextView)view).setTextSize(TypedValue.COMPLEX_UNIT_PX, ((TextView)view).getTextSize() * dpi);			
		}
		else if(view.getClass().equals(Button.class)){
			((Button)view).setTextSize(TypedValue.COMPLEX_UNIT_PX, ((Button)view).getTextSize() * dpi);			
		}
		else if(view.getClass().equals(EditText.class)){
			((EditText)view).setTextSize(TypedValue.COMPLEX_UNIT_PX, ((EditText)view).getTextSize() * dpi);		
		}		
	}
}