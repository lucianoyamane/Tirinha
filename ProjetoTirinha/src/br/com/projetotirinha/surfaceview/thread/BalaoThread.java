package br.com.projetotirinha.surfaceview.thread;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import br.com.projetotirinha.surfaceview.BalaoSurfaceView;

public class BalaoThread extends Thread {
	private SurfaceHolder surfaceHolder;
	private BalaoSurfaceView balaSurfaceView;
	private boolean run = false;

	public BalaoThread(SurfaceHolder surfaceHolder,
			BalaoSurfaceView surfaceView) {
		this.surfaceHolder = surfaceHolder;
		this.balaSurfaceView = surfaceView;
	}

	public void setRunning(boolean run) {
		this.run = run;
	}

	@Override
	public void run() {
		Canvas canvas;
		while (run) {
			canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					if (balaSurfaceView != null && canvas != null) {
						balaSurfaceView.onDrawCanvas(canvas);
					}
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}