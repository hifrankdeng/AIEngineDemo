package com.wh.camapp;

/**
 * Created by Administrator on 2016/3/24.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2015/12/2.
 */
public class CanvasView extends View {
    private Paint mPaint;
    
    public interface  DrawableItem
    {
    	public void draw(Canvas canvas);
    }
    // 矩形
    public class RectItem implements DrawableItem
    {
    	public Rect r = new Rect();
		public Paint paint = null;
    	public int stroke = 4;
    	public int color = Color.rgb(255, 0, 0);
    	public RectItem(int x1,int y1,int x2,int y2)
    	{
    		r.set(x1, y1,x2,y2);
    	}
		@Override
		public void draw(Canvas canvas)
		{
			if (r.width() > 0 && r.height() > 0)
			{
				if (paint != null)
				{
					canvas.drawRect(r, paint);
					return ;
				}

				mPaint.setColor(color);
				if (stroke > 0)
				{
					mPaint.setStrokeWidth(stroke);
					mPaint.setStyle(Style.STROKE);//空心矩形框  
				}
				else {
					mPaint.setStyle(Style.FILL); 
				}
				canvas.drawRect(r, mPaint);
			}
		}
    }
    
    // 文字
    public class TextItem implements DrawableItem
    {
    	public int x,y;
    	public String text;
    	public int textSize = 25;
    	public int color = Color.rgb(0, 255, 0);
    	public TextItem(int x,int y,String text)
    	{
    		this.text = text;
    		this.x = x;
    		this.y = y;
    	}
		@Override
		public void draw(Canvas canvas)
		{
			if (!text.isEmpty())
			{
				if (textSize > 0)
				{
					mPaint.setColor(color);
					mPaint.setTextSize(textSize);
					mPaint.setStyle(Style.FILL); 
				}
				
				canvas.drawText(text, x, y, mPaint);
			}
		}
    }
    
    // 圆
    public class CircleItem implements DrawableItem
    {
    	PointF centerF = new PointF();
    	float  radius = 0f;
    	public int stroke = 0;
    	public int color = Color.rgb(255, 0, 0);
    	public CircleItem(int x1,int y1,int radius)
    	{
    		centerF.set(x1, y1);
    		this.radius = radius;
    	}
    	
		@Override
		public void draw(Canvas canvas)
		{
			if (radius > 0)
			{
				mPaint.setColor(color);
				if (stroke > 0)
				{
					mPaint.setStrokeWidth(stroke);
					mPaint.setStyle(Style.STROKE);//空心矩形框  
				}
				else {
					mPaint.setStyle(Style.FILL); 
				}
				canvas.drawCircle(centerF.x,centerF.y, radius, mPaint);
			}
		}
    }
    
    // 线段
    public class LineItem implements DrawableItem
    {
    	public int x1,y1,x2,y2;
    	public int stroke = 4;
    	public int color = Color.rgb(255, 0, 0);
    	public LineItem(int x1,int y1,int x2,int y2)
    	{
    		this.x1 = x1;
    		this.y1 = y1;
    		this.x2 = x2;
    		this.y2 = y2;
    	}
    	
		@Override
		public void draw(Canvas canvas)
		{
			if (x1 != x2 || y1 != y2)
			{
				if (stroke > 0)
				{
					mPaint.setColor(color);
					mPaint.setStrokeWidth(stroke);
				}
				mPaint.setStyle(Style.STROKE);
				canvas.drawLine(x1,y1,x2,y2, mPaint);
			}
		}
    }
	//bitmap
	public class BitmapItem implements DrawableItem
	{
		public Rect r = new Rect();
		public Bitmap image;
		public int color = Color.rgb(255, 0, 0);
		public BitmapItem(int x1,int y1,int x2,int y2,Bitmap image)
		{
			r.set(x1, y1,x2,y2);
			this.image = image;
		}

		@Override
		public void draw(Canvas canvas)
		{
			//mPaint.setStyle(Style.STROKE);
			canvas.drawBitmap(image,null,r, mPaint);
		}
	}

	public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public Paint getPaint()
    {
    	return mPaint;
    }
    
    // 绘制列表
    private List<DrawableItem> mDrawList = new ArrayList<CanvasView.DrawableItem>();
    
    public void clear()
    {
    	mDrawList.clear();
    	invalidate();
    }
    
    public void paintBegin()
    {
    	mDrawList.clear();
    	//Log.e("draw", "clear");
    }
    public void paintEnd()
    {
    	invalidate();
    }
    
    public Rect drawRect(int x1,int y1,int x2,int y2,int color,int stroke)
    {
    	int x11 = Math.min(x1, x2);
    	int x22 = Math.max(x1, x2);
    	int y11 = Math.min(y1, y2);
    	int y22 = Math.max(y1, y2);
    	RectItem item = new RectItem(x11,y11,x22,y22);
    	item.color = color;
    	item.stroke = stroke;
    	mDrawList.add(item);
    	//Log.e("draw", x11+","+y11+","+x22+","+y22);
    	//Log.e("draw", "size:"+mDrawList.size());
    	return item.r;
    }

	public Rect drawRect(int x1,int y1,int x2,int y2,Paint paint)
	{
		int x11 = Math.min(x1, x2);
		int x22 = Math.max(x1, x2);
		int y11 = Math.min(y1, y2);
		int y22 = Math.max(y1, y2);
		RectItem item = new RectItem(x11,y11,x22,y22);
		item.paint = paint;
		mDrawList.add(item);
		return item.r;
	}
    
    public void drawText(int x,int y,String text,int color,int textSize)
    {
    	TextItem item = new TextItem(x,y, text);
    	item.color = color;
    	item.textSize = textSize;
    	mDrawList.add(item);
    	//Log.e("draw", "size:"+mDrawList.size());
    }
    
    public void drawCircle(int x1,int y1,int radius,int color,int stroke)
    {
    	CircleItem item = new CircleItem(x1,y1,radius);
    	item.color = color;
    	item.stroke = stroke;
    	mDrawList.add(item);
    }
    
    public void drawLine(int x1,int y1,int x2,int y2,int color,int stroke)
    {
    	LineItem item = new LineItem(x1,y1,x2,y2);
    	item.color = color;
    	item.stroke = stroke;
    	mDrawList.add(item);
    }

	public void drawBitmap(int x1,int y1,int x2,int y2,Bitmap bitmap)
	{
		BitmapItem item = new BitmapItem(x1,y1,x2,y2,bitmap);
		mDrawList.add(item);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		for (int i = 0; i < mDrawList.size(); i++)
		{
			mDrawList.get(i).draw(canvas);
		}
		mDrawList.clear();
	}
}

