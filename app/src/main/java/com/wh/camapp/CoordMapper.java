package com.wh.camapp;

public class CoordMapper
{
	public float ox,oy;
	public float sx,sy;
	
	public CoordMapper()
	{
		ox = oy = 0;
		sx = sy = 1;
	}
	
	public void set(int ax1,int ay1,int bx1,int by1,
			int ax2,int ay2,int bx2,int by2 )
	{
		sx = (bx1 - bx2)/(float)(ax1 - ax2);
		ox = bx1 - sx*ax1;
		
		sy = (by1 - by2)/(float)(ay1 - ay2);
		oy = by1 - sy*ay1;
	}
	
	public int mapX(int x)
	{
		return (int) (sx * x + ox);
	}
	public int scaleX(int x)
	{
		return (int) (sx * x);
	}
	public int invX(int x)
    {
        return (int)((x - ox)/sx);
    }
	public int scaleY(int y)
	{
		return (int) (sy * y);
	}
	public int mapY(int y)
	{
		return (int) (sy*y+oy);
	}
    public int invY(int y)
    {
        return (int)((y - oy)/sy);
    }
}
