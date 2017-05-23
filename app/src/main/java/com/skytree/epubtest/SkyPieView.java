package com.skytree.epubtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.util.TypedValue;
import android.view.View;

public class SkyPieView extends View {
	public boolean isHidden = true;
	public double value = 0;
	public double oldValue = 0;
	
	String[] data_names;
	int[] data_values = new int[] {0,0};
	int[] color_values = new int[]{Color.YELLOW,Color.TRANSPARENT};
	
	public void setValue(double value) {
		this.value = value;
		if (value<oldValue) this.value = oldValue;
		int percent = (int)(value*100);
		data_values[0] = percent;
		data_values[1] = 100-data_values[0];
		oldValue = value;
//		this.invalidate();
	}
	
	public SkyPieView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		if (android.os.Build.VERSION.SDK_INT >= 11) {
		     setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}
	
	private void drawPercent(Canvas canvas) {		
		int width = this.getWidth();
		int height = this.getHeight();
		Paint paint = new Paint();		
		String message = String.format("%d%%",(int)(this.value*100));
		paint.setColor(Color.LTGRAY);
		paint.setTextAlign(Paint.Align.CENTER);
		int textSize = 11;
		int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		                              textSize, getResources().getDisplayMetrics());
		paint.setTextSize(pixel);
		canvas.drawText(message,width/2,height/2+(int)(height*0.04), paint);
	}
	
	boolean isDownloaded = false;
	public void drawPie(Canvas canvas) {
		// TODO Auto-generated method stub
		int alpha = 0xfa;
		int gf = 0xf0;
		int df = 0xaf;
		if (this.value==1.0) {
			alpha = 0xff;
			gf = 0xff;	
		}		
		int brightColor = Color.argb(alpha, gf, gf, gf);
		int darkColor =  Color.argb(alpha, df, df, df);
		int lineColor = Color.argb(alpha, gf, gf, gf);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		//screen width & height
		int width = this.getWidth();
		int height = this.getHeight();

		//chart area rectangle
		RectF outterBounds;
		RectF innerBounds;
		float sf = 0.5f;
		float max = Math.max(width, height);
		float rf = (int)(max*sf);
		float sx = width/2-rf/2;
		float sy = height/2-rf/2;
		outterBounds = new RectF(sx,sy,sx+rf,sy+rf);
		
		float isf = 0.5f;
		float irf = rf*isf;
		float isx = width/2-irf/2;
		float isy = height/2-irf/2;	
		
		innerBounds = new RectF(isx,isy,isx+irf,isy+irf);
		
		int value_sum = 0;		
		//sum of data values
		for (int datum : data_values)
			value_sum += datum;

		float startAngle = 270;
		
		// 외곽선을 그린다. 
		Paint linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeJoin(Join.ROUND);
		linePaint.setStrokeCap(Cap.ROUND);
		linePaint.setStrokeWidth(0.5f);
		linePaint.setColor(lineColor);
//		canvas.drawArc(innerBounds,0,360,true,linePaint);
//		canvas.drawArc(outterBounds,0,360,true,linePaint);

		if (this.value==0.0f) return;
		int datum = data_values[0];

		//calculate start & end angle for each data value
		float endAngle = value_sum == 0 ? 0 : 360 * datum / (float) value_sum;

		//gradient fill color
		RadialGradient gradient = new RadialGradient(
				getWidth()/2,
				getHeight()/2,
				rf, 
				new int[] {brightColor,darkColor},
				new float[] {0, 1}, 
				android.graphics.Shader.TileMode.CLAMP);
		paint.setShader(gradient);

		canvas.drawArc(outterBounds, startAngle, endAngle, true, paint);
		paint.setXfermode(new  PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawArc(innerBounds, startAngle, endAngle, true, paint);
		
/*
		canvas.save();
		//draw fill arc
		Path path = new Path();
		path.moveTo(outterBounds.centerX(), outterBounds.centerY());
		path.addArc(outterBounds, startAngle, endAngle);
		path.lineTo(outterBounds.centerX(), outterBounds.centerY());
		Path innerPath = new Path();
		innerPath.addArc(innerBounds, 0, 360);
		canvas.clipPath(path);
		canvas.clipPath(innerPath, Region.Op.DIFFERENCE);		// 하드웨어 가속 상태에서는 오류 	
		canvas.drawPath(path, paint);
//		canvas.clipPath(innerPath, Region.Op.UNION);
	*/	
		oldValue = value;
		canvas.restore();
	}

	
	@Override
	public void onDraw(Canvas canvas) {
		if (!isHidden && (value>0 && value<=1)) {
			this.drawPercent(canvas);
			this.drawPie(canvas);			
			this.drawPercent(canvas);
		}		
	}
}