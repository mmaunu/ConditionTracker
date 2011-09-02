package com.maunumental.dnd.conditiontracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class HitPointsMinusControlView extends View 
{	
	
	public HitPointsMinusControlView(Context c)
	{
		super(c);
		
	}
	
	public HitPointsMinusControlView(Context c, AttributeSet as)
	{
		super(c,as);
		
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(255,0,0,0);
		
		Bitmap image = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.shield_minus_green_rinse);
		canvas.drawBitmap(image, null, new Rect(0,0,width,height), paint);
		

	}
	

}
