package br.com.projetotirinha.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import br.com.projetotirinha.surfaceview.thread.BalaoThread;

public class BalaoSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private BalaoThread thread;
	private int x = 200;
	private int y = 200;
	private Bitmap balao;
	private int posicaoFotoX = 0;
	private int posicaoFotoY = 0;

	public BalaoSurfaceView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		getHolder().addCallback(this);
		thread = new BalaoThread(getHolder(), this);
		setFocusable(true);
		this.setZOrderOnTop(true);
		SurfaceHolder sfhTrack = this.getHolder();
		sfhTrack.setFormat(PixelFormat.TRANSPARENT);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = (int) event.getX();
		y = (int) event.getY();

		return true;
	}

	public void setBalao(Bitmap bitmap) {
		this.balao = bitmap;
	}
	
	public void onDrawCanvas(Canvas canvas){
		this.onDraw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (balao != null) {
			Paint paint = new Paint();
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPaint(paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
			posicaoFotoX = x - (balao.getWidth() / 2);
			posicaoFotoY = y - (balao.getHeight() / 2);
			canvas.drawBitmap(balao, posicaoFotoX, posicaoFotoY, null);
		}
	}

	public int getPosicaoFotoX() {
		return posicaoFotoX;
	}

	public int getPosicaoFotoY() {
		return posicaoFotoY;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!thread.isAlive() || thread.isInterrupted()) {
			try {
				this.thread.setRunning(true);
				this.thread.start();
			} catch (IllegalThreadStateException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		this.thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
