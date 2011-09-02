package com.maunumental.dnd.conditiontracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DiceRollerSingleKeyView extends View 
{
	private String displayValue;
	
	public DiceRollerSingleKeyView(Context c)
	{
		super(c);
	}
	
	public DiceRollerSingleKeyView(Context c, AttributeSet as)
	{
		super(c,as); 
	}
	
	public void setDisplayValue(String d)
	{
		displayValue = d;
	}
	
	public String getDisplayValue()
	{
		return displayValue;
	}
	
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if(displayValue == null)
			displayValue = "";
		
		int width = getWidth();
		int height = getHeight();
		int buffer = 5;
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		/*
		Bitmap image = null;
		
		if(displayValue.equals("1"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key1);
		else if(displayValue.equals("2"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key2);
		else if(displayValue.equals("3"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key3);
		else if(displayValue.equals("4"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key4);
		else if(displayValue.equals("5"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key5);
		else if(displayValue.equals("6"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key6);
		else if(displayValue.equals("7"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key7);
		else if(displayValue.equals("8"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key8);
		else if(displayValue.equals("9"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key9);
		else if(displayValue.equals("0"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key0);
		else if(displayValue.equals("+"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_plus);
		else if(displayValue.equals("-"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_minus);
		else if(displayValue.equals("Reroll(r)"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_reroll);
		else if(displayValue.equals("Crit(c)"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_crit);
		else if(displayValue.equals("Delete"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_delete);
		else if(displayValue.equals("Clear"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_clear);
		else if(displayValue.equals("Done"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_done);
		else if(displayValue.equals("d"))
			image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.die_roller_key_d);

		
		
		if(image != null)
			canvas.drawBitmap(image, null, new Rect(0,0,width,height), paint);	
			
*/		
		
		
		//draw a lightGray background
		paint.setColor(getResources().getColor(R.color.lightGray));
		canvas.drawRect(0, 0, width, height, paint);
		
		paint.setARGB(255, 0, 0, 0);
		canvas.drawRoundRect(new RectF(buffer-1, buffer-1, width-buffer+1, height-buffer+1), 
				5, 5, paint);
		
		//draw a gray area just inside of the area, 
		paint.setColor(getResources().getColor(R.color.gray));
		canvas.drawRoundRect(new RectF(buffer, buffer, width-buffer, height-buffer), 
				5, 5, paint);
		
		
		paint.setTextSize(30);
		paint.setARGB(255, 0, 0, 0);
		
		//determine how big the text is so as to position it in the center
		Rect bounds = new Rect();
		paint.getTextBounds(displayValue, 0, displayValue.length(), bounds);
		
		int x = width/2 - bounds.width()/2;
		int y = height/2 + bounds.height()/2;
		
		if(x < 0 || displayValue.length() > 5)
		{
			paint.setTextSize(20);
			Rect bounds2 = new Rect();
			paint.getTextBounds(displayValue, 0, displayValue.length(), bounds2);
			x = width/2 - bounds2.width()/2;
			y = height/2 + bounds2.height()/2;
		}
	
		canvas.drawText(displayValue, x, y, paint);

	}
}
