package com.maunumental.dnd.conditiontracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;



public class PowerPointNumberView extends View 
{	
	
	private CharacterDnD4Ed character;
	
	public PowerPointNumberView(Context c)
	{
		super(c);
	}
	
	public PowerPointNumberView(Context c, AttributeSet as)
	{
		super(c,as);
	}
	
	public void setCharacter(CharacterDnD4Ed c)
	{
		character = c;
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		int width = this.getWidth();
		int height = this.getHeight();

		
		paint.setARGB(255,0,0,0);
		
		Bitmap image = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.shield_base_purple);
		canvas.drawBitmap(image, null, new Rect(0,0,width,height), paint);
		
		//draw  a white rounded rectangle
//		paint.setARGB(255, 255, 255, 255);
//		canvas.drawRoundRect(new RectF(0, 0, width, height), 10, 10, paint);
		
	
		String numbPowerPts = "0";
		
		if(character != null)
		{
			numbPowerPts = "" + character.getCurrentPowerPoints();
		}
		
		//setup the paint attributes and then calculate where to draw the number
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(40);
		//paint.setStrokeWidth(5);
		Rect bounds = new Rect();
		paint.getTextBounds(numbPowerPts, 0, numbPowerPts.length(), bounds);
		
		int x = width/2 - bounds.width()/2;
		int y = height/2 + bounds.height()/2;
		
		//it just looks off center if the number is 1 b/c of the shape...
		if(character.getCurrentPowerPoints() == 1)
			x -= 5;
		
		canvas.drawText(numbPowerPts, x, y, paint);
		
		
	}
	

}
