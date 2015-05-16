package br.com.projetotirinha;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.projetotirinha.constantes.Constantes;
import br.com.projetotirinha.dialog.CamadaEdicaoBalao;
import br.com.projetotirinha.interfaceprincipal.InterfacePrincipal;

public class ProjetoTirinhaActivity extends Activity implements
		InterfacePrincipal {

	private ImageButton buttonObterImagem;
	private ImageButton buttonObterBalao;
	private AlertDialog menuDialogObterImage = null;
	private static final int OBTER_IMAGEM_CAMERA = 0;
	private static final int OBTER_IMAGEM_GALLERY = 1;
	private static final int COMPARTILHAMENTO = 2;
	private ImageView imageViewContainer;
	private Bitmap foto;
	private CamadaEdicaoBalao camadaEdicaoBalao;
	private boolean imagemCarregada = false;
	private File fotoGravada;
	private String imagepath;
	private int imageHeight;
	private int imageWidth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.adicionarComponentes();
		this.adicionarListeners();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void adicionarComponentes() {
		buttonObterImagem = (ImageButton) this
				.findViewById(R.id.buttonObterImagem);
		buttonObterBalao = (ImageButton) this
				.findViewById(R.id.buttonObterBalao);
		imageViewContainer = (ImageView) this
				.findViewById(R.id.imageViewContainer);
	}

	@Override
	public void adicionarListeners() {
		buttonObterImagem.setOnClickListener(buttonObterImagemClick);
		buttonObterBalao.setOnClickListener(buttonObterBalaoClick);
	}

	private OnClickListener buttonObterBalaoClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (buttonObterImagemEstaVisivel()) {
				visibilidadeBotoes(Constantes.INVISIVEL);
				acionarCamadaEdicao();
			}
		}
	};

	public Boolean buttonObterImagemEstaVisivel() {
		return buttonObterImagem.getVisibility() == Constantes.VISIVEL;
	}

	private void visibilidadeBotoes(Integer visibilidade) {
		buttonObterImagem.setVisibility(visibilidade);
		if (imagemCarregada) {
			buttonObterBalao.setVisibility(visibilidade);
		}
	}

	private void mostrarAviso(int mensagemId) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast,
		                               (ViewGroup) findViewById(R.id.toast_layout_root));
		
		TextView textCustomToast = (TextView) layout.findViewById(R.id.textCustomToast);
		textCustomToast.setTypeface(getComicSansFont());
		textCustomToast.setText(mensagemId);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}
	
	private Typeface getComicSansFont(){
		return Typeface.createFromAsset(getAssets(), "Comic Sans MS Negrito.ttf");
	}

	private void acionarCamadaEdicao() {

		camadaEdicaoBalao = new CamadaEdicaoBalao(this) {
			private Bitmap pegarTirinha() {
				return criarTirinha(getBitmapBalao(), getPosicaoFotoX(),
						getPosicaoFotoY());
			}

			@Override
			public void onClick(View v) {
				super.onClick(v);
				if (fecharFoiClicado(v)) {
					fecharCamadaEdicao();
				}
				if (compartilharFoiClicado(v)) {
					if (salvarFoto(pegarTirinha())) {
						createShareIntent();
					}
				}
				if (salvarFoiClicado(v)) {
					if (salvarFoto(pegarTirinha())) {
						mostrarAviso(R.string.salvo_com_sucesso);
					}
				}
			}

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0) {
					visibilidadeBotoes(Constantes.VISIVEL);
				}
				return super.onKeyDown(keyCode, event);
			}
		};
		// camadaEdicaoBalao.definirTamanhoSurfaceView(imageViewContainer.getWidth(),
		// imageViewContainer.getHeight());
		camadaEdicaoBalao.show();
	}

	private void fecharCamadaEdicao() {
		camadaEdicaoBalao.dismiss();
		visibilidadeBotoes(Constantes.VISIVEL);
	}

	private void createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.setType("image/*");
		Uri uri = Uri.fromFile(fotoGravada);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivityForResult(Intent.createChooser(shareIntent,
				getString(R.string.como_voce_quer_compartilhar)),
				COMPARTILHAMENTO);
	}

	private Boolean salvarFoto(Bitmap bitmap) {
		OutputStream fOut = null;
		try {
			File folder = new File(getDiretorioGZ());
			boolean success = true;
			if (!folder.exists()) {
			    success = folder.mkdir();
			}
			if(success){
				fotoGravada = new File(getDiretorioGZ(),
						getString(R.string.nome_arquivo_tirinha));
				fOut = new FileOutputStream(fotoGravada);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
				MediaStore.Images.Media.insertImage(getContentResolver(),
						fotoGravada.getAbsolutePath(), fotoGravada.getName(),
						fotoGravada.getName());
				fOut.flush();
				fOut.close();
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e){
			return false;
		}
		return true;
	}

	private String getDiretorioGZ() {
		return Environment.getExternalStorageDirectory().toString() + "//gz//";
	}

	private Display getDisplay() {
		return getWindowManager().getDefaultDisplay();
	}

	private Bitmap criarTirinhaBitmap() {
		RelativeLayout tirinhaContainer = new RelativeLayout(
				getApplicationContext());
		tirinhaContainer.layout(0, 0, getDisplay().getWidth(), getDisplay()
				.getHeight());
		tirinhaContainer.setBackgroundResource(R.drawable.fundo_tirinha);
		tirinhaContainer.addView(criarImageViewTirinha());
		tirinhaContainer.setDrawingCacheEnabled(true);
		return Bitmap.createBitmap(tirinhaContainer.getDrawingCache());
	}

	private ImageView criarImageViewTirinha() {
		ImageView imageView = new ImageView(getApplicationContext());
		imageView.layout(0, 0, getDisplay().getWidth(), getDisplay()
				.getHeight());
		imageView.setBackgroundResource(R.drawable.fundo_tirinha);
		imageView.setPadding(21, 21, 21, 21);
		imageView.setImageBitmap(foto);
		return imageView;
	}

	private Bitmap criarTirinha(Bitmap balao, int posicaoBalaoX,
			int posicaoBalaoY) {
		Bitmap temp = criarTirinhaBitmap();

		Bitmap container = Bitmap.createBitmap(temp.getWidth(),
				temp.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas tirinha = new Canvas(container);
		tirinha.drawBitmap(temp, 0, 0, null);
		if (balao != null) {
			tirinha.drawBitmap(balao, posicaoBalaoX, posicaoBalaoY, null);
		}

		tirinha.drawBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.gz_icone_teste_menor), temp.getWidth() - 100,
				temp.getHeight() - 100, null);
		return container;
	}

	private OnClickListener buttonObterImagemClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			criarOpcaoImagemDialog();
		}
	};

	private String getRealPathFromURI(Uri contentUri) {
		String[] projx = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, projx, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private int getOrientation(Context context, Uri photoUri) {
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		imageHeight = options.outHeight;
		imageWidth = options.outWidth;
		int inSampleSize = 1;

		if (imageHeight > reqHeight || imageWidth > reqWidth) {
			if (imageWidth > imageHeight) {
				inSampleSize = Math.round((float) imageHeight
						/ (float) reqHeight);
			} else {
				inSampleSize = Math
						.round((float) imageWidth / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	private Bitmap decodeSampledBitmapFromUri(Uri capturedImageUri) {

		imagepath = getRealPathFromURI(capturedImageUri);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(imagepath, options);
		options.inSampleSize = calculateInSampleSize(options, getDisplay()
				.getWidth(), getDisplay().getHeight());
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(imagepath, options);
	}

	private Matrix getMatrixOrientation(Uri uri) {
		Matrix matrix = new Matrix();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		if (getOrientation(getApplicationContext(), uri) != 0) {
			matrix.postRotate(getOrientation(getApplicationContext(), uri));
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		return matrix;
	}

	private void definirVisualizacaoFoto(Bitmap bitmap) {
		imagemCarregada = !(bitmap == null);
		imageViewContainer.setImageBitmap(foto);
		if (imagemCarregada) {
			buttonObterBalao.setVisibility(Constantes.VISIVEL);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == OBTER_IMAGEM_CAMERA || requestCode == OBTER_IMAGEM_GALLERY)
				&& resultCode == Activity.RESULT_OK) {

			definirVisualizacaoFoto(null);
			Uri capturedImageUri = data.getData();
			Bitmap temp = decodeSampledBitmapFromUri(capturedImageUri);

			foto = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(),
					temp.getHeight(), getMatrixOrientation(capturedImageUri),
					false);
			definirVisualizacaoFoto(foto);
		}

		if (requestCode == COMPARTILHAMENTO) {
			fecharCamadaEdicao();
			mostrarAviso(R.string.compartilhado_com_sucesso);
		}

	}

	private void criarOpcaoImagemDialog() {
		final CharSequence[] items = this.getResources().getTextArray(
				R.array.obter_imagem_opcoes);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					Intent intent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, OBTER_IMAGEM_CAMERA);
				} else if (item == 1) {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,
							getString(R.string.selecione_uma_imagem)),
							OBTER_IMAGEM_GALLERY);
				}
				menuDialogObterImage.dismiss();
			}
		});
		menuDialogObterImage = builder.create();
		menuDialogObterImage.show();

	}
}