package com.maunumental.dnd.conditiontracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class AddBadConditionView extends View 
{	
	
	public AddBadConditionView(Context c)
	{
		super(c);
		
	}
	
	public AddBadConditionView(Context c, AttributeSet as)
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
				R.drawable.shield_base);
		canvas.drawBitmap(image, null, new Rect(0,0,width,height), paint);
		
		
		String text = "Bad";
		
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(24);
		//paint.setStrokeWidth(5);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		
		int x = width/2 - bounds.width()/2;
		int y = height/2 + bounds.height()/2;
		
		canvas.drawText(text, x, y, paint);
	}
	

}
