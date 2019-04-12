package com.wh.camapp;


public class CamWorker
{
	public static String getName(){ return "";}
	public static String getDescription(){return "";}
	public void setup(String dir,int screenW,int screenH){}
	public void destroy(){}
	public String processFrame(byte[] data, int previewWidth, int previewHeight,CanvasView canvasView){
		return "";
	}
}
