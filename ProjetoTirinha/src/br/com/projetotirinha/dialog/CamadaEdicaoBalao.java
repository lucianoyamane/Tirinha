package br.com.projetotirinha.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import br.com.projetotirinha.R;
import br.com.projetotirinha.adapter.ListaBaloesAdapter;
import br.com.projetotirinha.surfaceview.BalaoSurfaceView;

public class CamadaEdicaoBalao extends Dialog implements
		android.view.View.OnClickListener {

	private Gallery galleryBalao;
	private ImageButton buttonFecharBalao;
	private ImageButton buttonCompartilhar;
	private ImageButton buttonSalvar;
	private BalaoSurfaceView surfaceViewBalao;
	private ListaBaloesAdapter listaBaloesAdapter;
	private MensagemDialog mensagemDialog;
	private Bitmap bitmapBalao;

	public CamadaEdicaoBalao(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		setContentView(R.layout.camadaedicaobalao);
		this.adicionarComponentes();
		this.adicionarListeners();
	}

	private void adicionarListeners() {
		galleryBalao.setOnItemClickListener(galleryBallaoOnItemClick);
		buttonFecharBalao.setOnClickListener(this);
		buttonCompartilhar.setOnClickListener(this);
		buttonSalvar.setOnClickListener(this);
	}

	public Boolean fecharFoiClicado(View view) {
		return view.equals(buttonFecharBalao);
	}

	public Boolean compartilharFoiClicado(View view) {
		return view.equals(buttonCompartilhar);
	}

	public Boolean salvarFoiClicado(View view) {
		return view.equals(buttonSalvar);
	}

	private void adicionarComponentes() {
		galleryBalao = (Gallery) this.findViewById(R.id.galleryBalao);
		listaBaloesAdapter = new ListaBaloesAdapter(this.getContext());
		galleryBalao.setAdapter(listaBaloesAdapter);
		registerForContextMenu(galleryBalao);
		buttonFecharBalao = (ImageButton) this
				.findViewById(R.id.buttonFecharBalao);
		buttonCompartilhar = (ImageButton) this
				.findViewById(R.id.buttonCompartilhar);
		buttonSalvar = (ImageButton) this.findViewById(R.id.buttonSalvar);
		surfaceViewBalao = (BalaoSurfaceView) this
				.findViewById(R.id.surfaceViewBalao);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.dismiss();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnItemClickListener galleryBallaoOnItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View imageView, int index,
				long arg3) {
			openDialog(index);
		}
	};

	private void openDialog(int index) {

		mensagemDialog = new MensagemDialog(getContext()) {
			@Override
			public void onClick(View v) {
				super.onClick(v);
				if (confirmarMensagemClicado(v)) {
					bitmapBalao = definirLinearLayoutBalao();
					surfaceViewBalao.setBalao(bitmapBalao);
					fecharDialog();
				}

			}
		};
		mensagemDialog.defineBackground(listaBaloesAdapter
				.getResourceImageList()[index]);
		mensagemDialog.show();
		// alert.getWindow().getAttributes().windowAnimations =
		// R.style.Animations_SmileWindow;
	}

	@Override
	public void onClick(View v) {
	}

	public int getPosicaoFotoX() {
		return surfaceViewBalao.getPosicaoFotoX();
	}

	public int getPosicaoFotoY() {
		return surfaceViewBalao.getPosicaoFotoY();
	}

	public Bitmap getBitmapBalao() {
		return bitmapBalao;
	}

}
