package com.maunumental.dnd.conditiontracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HitPointDisplayView extends View
{
	
	private CharacterDnD4Ed character;
	
	
	public HitPointDisplayView(Context c)
	{
		super(c);
		setBackgroundResource(R.drawable.hit_point_display_border);
	}
	
	public HitPointDisplayView(Context c, AttributeSet as)
	{
		super(c,as);
		setBackgroundResource(R.drawable.hit_point_display_border);
	}
	
	public void setCharacter(CharacterDnD4Ed charry)
	{
		character = charry;
	}
	
	
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if(character == null)   //seems like a reasonable thing to check...
		{
			return;
		}
		
		int buffer = 10; //from the right and left edges as well as top and bottom edges
		int width = this.getWidth();
		int height = this.getHeight();
		int currHPDisplayWidth = width - 3*buffer;
		
		int currHPDisplayHeightNoTempHP = 40;
		int currHPDisplayHeightWithTempHP = currHPDisplayHeightNoTempHP + 30;
		//hpDisplayHeightStart represents the starting height for the clear area...
		//it extends down from there
		int hpDisplayHeightStart = (int)(.06*height);
		
		int roundRectRadius = 10;
		
		//establish the ratio of current to max
		float ratio;
		int curr = character.getCurrentHitPoints();
		int max = character.getMaxHitPoints();
		int tempHP = character.getTempHitPoints();
		if(max == 0) 
			return;	// paranoid
		ratio = (float)(curr)/max;
		
		
		//drawable.draw(canvas);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		
		
		//trying to draw a nice frame...we'll see...oh, we'll see...later, get rid of this
		//and put in a nice picture!!
//		paint.setARGB(255, 255, 255, 255);
//		canvas.drawRoundRect( new RectF(0, 0, width, height), 
//				roundRectRadius, roundRectRadius, paint);
		
		
		
		//goof off here...draw a dagger...background image!!
//		Bitmap dagger = BitmapFactory.decodeResource(context.getResources(),R.drawable.dagger);
//		canvas.drawBitmap(dagger, null, new Rect(HP_BAR_X_START,buffer,HP_BAR_X_FINISH, 
//				height - buffer), paint);
		
		
		
		
		
		
		
		
		//draw tempHP bubble first...then the regular hp...aesthetics...
		//but only if the health bar is down a bit
		
		
		//if there are temp hit points, draw a # of pixels tall rectangle...
		//if there is room at the top (b/c health is low), draw above normal health bar
		//otherwise just draw it at the top (the top pixels of the health bar)
		int tempHPHealthBarDisplayHeight = 30;
		int currentLengthOfHealthBar = (int)(ratio*(height - 2*buffer));
		if(tempHP > 0 && 
				currentLengthOfHealthBar + tempHPHealthBarDisplayHeight < height - 2*buffer)
		{
			paint.setARGB(255, 0, 0, 255);
			canvas.drawRoundRect( 
				new RectF(buffer, 
					height - currentLengthOfHealthBar - tempHPHealthBarDisplayHeight - buffer, 
					width - buffer, height - currentLengthOfHealthBar ), 
				roundRectRadius, roundRectRadius, paint);					
		}
		
		
		if(max == 0)
		{
			ratio = 1;
			paint.setARGB(255, 0, 255, 0);
			canvas.drawRoundRect( new RectF(0, 0, this.getWidth(), this.getHeight()), 
					roundRectRadius, roundRectRadius, paint);
			return;
		}
		else
		{
			//otherwise, get width of bar to paint and figure out which color
			//green for more 75%, yellow until 50%, orange until 25%, red after
			
			if(ratio > .75)
				paint.setColor(getResources().getColor(R.color.green));
			else if (ratio > .5)
				paint.setColor(getResources().getColor(R.color.yellow));
			else if (ratio > .25)
				paint.setColor(getResources().getColor(R.color.orange));	
			else
				paint.setColor(getResources().getColor(R.color.red));
		}
		
		//draw the normal health bar if currentHitPoints is greater than 0...
		if(curr > 0)
			canvas.drawRoundRect( new RectF(buffer, (int)((height-buffer)*(1.0-ratio)) + buffer, 
												width-buffer, height-buffer), 
					roundRectRadius, roundRectRadius, paint);
		else
		{//draw a half, reverse health bar going up to bloodied...
/*			paint.setARGB(255, 255, 0, 0);
			canvas.drawRoundRect(new RectF(width/2, (float)((1+ratio)*height), width, height), 
					roundRectRadius, roundRectRadius, paint);
			paint.setARGB(255, 255, 255, 0);
			canvas.drawRoundRect(new RectF(width/2+5, (float)((1+ratio)*height)+5, 
								width-5, height-5), 
					roundRectRadius, roundRectRadius, paint);
*/					
		}
		
		
		//draw temp hit points after the health bar if the health bar is mostly full...
		//looks nicer
		if(tempHP > 0 && 
				currentLengthOfHealthBar + tempHPHealthBarDisplayHeight >= height - 2*buffer)
		{
			paint.setARGB(255, 0, 0, 255);
			canvas.drawRoundRect( new RectF(buffer, buffer, width - buffer, 
						tempHPHealthBarDisplayHeight + 2*buffer),
						roundRectRadius, roundRectRadius, paint);
		}
		
		
		
		
		//draw in some markers for 3/4, 1/2 and 1/4 full
		paint.setColor(getResources().getColor(R.color.redHPBorder));
		
		paint.setStrokeWidth(5);
		canvas.drawLine(buffer, (float)(.25*height), width/4, (float)(.25*height), paint);
		canvas.drawLine(3*width/4, (float)(.25*height), width - buffer, 
				(float)(.25*height), paint);
		canvas.drawLine(buffer, (float)(.5*height), 2*width/5, (float)(.5*height), paint);
		canvas.drawLine(3*width/5, (float)(.5*height), width - buffer, 
				(float)(.5*height), paint);
		canvas.drawLine(buffer, (float)(.75*height), width/4, (float)(.75*height), paint);
		canvas.drawLine(3*width/4, (float)(.75*height), width - buffer, 
				(float)(.75*height), paint);
		
/*		canvas.drawArc(new RectF(0, (float).25*height - 20, width, (float).25*height), 
				90, 90, false, paint);
		canvas.drawArc(new RectF(0, (float).25*height, width, (float).25*height + 20), 
				180, 90, false, paint);
		paint.setStrokeWidth(5);
		canvas.drawLine(buffer, (float)(.25*height), width/4, (float)(.25*height), paint);
*/		
		
		
		
		
		
		
		
		
		//Draw a "shaded" bubble over the health bar at the hpDisplayHeightStart
		//different heights on the bubble if there are temp hit points
		paint.setColor(getResources().getColor(R.color.lightGray));
		if(tempHP > 0)
			canvas.drawRoundRect( 
					new RectF( (width - currHPDisplayWidth)/2, hpDisplayHeightStart, 
							(width + currHPDisplayWidth)/2, 
							hpDisplayHeightStart + currHPDisplayHeightWithTempHP), 
					roundRectRadius, roundRectRadius, paint);
		else
			canvas.drawRoundRect( 
					new RectF( (width - currHPDisplayWidth)/2, hpDisplayHeightStart, 
							(width + currHPDisplayWidth)/2, 
							hpDisplayHeightStart + currHPDisplayHeightNoTempHP), 
					roundRectRadius, roundRectRadius, paint);
		
		
		
		
		//draw the current number of hp...
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(40);
		int xCoordText;
		if(curr >= 100)
			paint.setTextSize(30);
		String hpTextToDraw = "" + curr;
		Rect bounds = new Rect();
		paint.getTextBounds(hpTextToDraw, 0, hpTextToDraw.length(), bounds);
		xCoordText = width/2 - bounds.width()/2;

		canvas.drawText(hpTextToDraw, xCoordText, 
				hpDisplayHeightStart + currHPDisplayHeightNoTempHP - 5, paint);
		
		
		
		//if curr is < -bloodied...draw oh crap text
		if( curr < -1*(max/2) )
		{
			paint.setARGB(255, 255, 0, 0);
			paint.setTextSize(24);
			canvas.drawText("DEAD!!", buffer, hpDisplayHeightStart - buffer, paint);
		}
		
		
		//draw in the number of temp hit points...if any
		paint.setTextSize(30);
		paint.setARGB(255, 0, 0, 255);
		String thpText = "+" + tempHP;
		bounds = new Rect();
		paint.getTextBounds(thpText, 0, thpText.length(), bounds);
		int xTHPStart = width/2 - bounds.width()/2;
		if(tempHP > 0)
			canvas.drawText(thpText, xTHPStart, 
					hpDisplayHeightStart + currHPDisplayHeightWithTempHP - 5, paint);
		
		
		
		//attempt to draw a border
	//	Bitmap image = BitmapFactory.decodeResource(this.getResources(),R.drawable.hit_point_display_border);
	//	canvas.drawBitmap(image, null, new Rect(0,0,width,height), paint);
		
	}
	
}