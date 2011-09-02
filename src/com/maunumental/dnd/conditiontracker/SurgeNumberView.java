package com.maunumental.dnd.conditiontracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class SurgeNumberView extends View 
{	
	
	private CharacterDnD4Ed character;
	
	public SurgeNumberView(Context c)
	{
		super(c);
		
	}
	
	public SurgeNumberView(Context c, AttributeSet as)
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
				R.drawable.shield_base_red);
		canvas.drawBitmap(image, null, new Rect(0,0,width,height), paint);
	
	
		String currSurgeNumbs = "0";
		
		if(character != null)
		{
			currSurgeNumbs = "" + character.getCurrentSurges();
		}
		
		//setup the paint attributes and then calculate where to draw the surge number
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(40);

		Rect bounds = new Rect();
		paint.getTextBounds(currSurgeNumbs, 0, currSurgeNumbs.length(), bounds);
		
		int x = width/2 - bounds.width()/2;
		int y = height/2 + bounds.height()/2;
		
		//when it is a 2-digit number, it doesn't look centered b/c of the 1's shape
		if(currSurgeNumbs.length() > 1)
			x -= 5;
		
		canvas.drawText(currSurgeNumbs, x, y, paint);
		
		
	}
	

}
