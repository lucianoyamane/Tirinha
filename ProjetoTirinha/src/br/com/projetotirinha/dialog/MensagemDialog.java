package br.com.projetotirinha.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.projetotirinha.R;
import br.com.projetotirinha.interfaceprincipal.InterfacePrincipal;

public class MensagemDialog extends Dialog implements OnClickListener, InterfacePrincipal {

	private EditText editTextMensagem;
	private ImageButton buttonConfirmarMensagem;
	private RelativeLayout linearLayoutMensagemDialog;
	private int widthLinearLayoutMensagemDialog;
	private int heightLinearLayoutMensagemDialog;
	private int resourceIdBackGround;
	private int topTextViewBalaoresultado;
	private int bottomTextViewBalaoresultado;
	private int leftTextViewBalaoresultado;
	private int rightTextViewBalaoresultado;

	private RelativeLayout linearLayoutBalaoResultado;
	private TextView textViewBalaoResultado;

	public MensagemDialog(Context context) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mensagem_dialog);
		this.adicionarComponentes();
		this.adicionarListeners();
	}
	
	@Override
	public void adicionarComponentes() {
		editTextMensagem = (EditText) this.findViewById(R.id.editTextMensagem);
		editTextMensagem.setTypeface(getComicSansFont()); 
		buttonConfirmarMensagem = (ImageButton) this
				.findViewById(R.id.buttonConfirmarMensagem);
		linearLayoutMensagemDialog = (RelativeLayout) this
				.findViewById(R.id.linearLayoutMensagemDialog);
	}
	
	private Typeface getComicSansFont(){
		return Typeface.createFromAsset(getContext().getAssets(), "Comic Sans MS Negrito.ttf");
	}

	@Override
	public void adicionarListeners() {
		buttonConfirmarMensagem.setOnClickListener(this);
	}

	public void defineBackground(int id) {
		resourceIdBackGround = id;
		linearLayoutMensagemDialog.setBackgroundResource(id);
	}

	private void redefinirTamanhoLinearLayoutBalao(int count) {
		if(count <= 5){
			widthLinearLayoutMensagemDialog = 125;
			heightLinearLayoutMensagemDialog = 75;
		}else if (count <= 10) {
			widthLinearLayoutMensagemDialog = 250;
			heightLinearLayoutMensagemDialog = 150;
		} else if (count > 10 && count <= 15) {
			widthLinearLayoutMensagemDialog = 250;
			heightLinearLayoutMensagemDialog = 150;
		} else if (count > 15 && count < 30) {
			widthLinearLayoutMensagemDialog = 250;
			heightLinearLayoutMensagemDialog = 150;
		}else if(count >= 30 && count <= 40){
			widthLinearLayoutMensagemDialog = 250;
			heightLinearLayoutMensagemDialog = 175;
		}else{
			widthLinearLayoutMensagemDialog = 250;
			heightLinearLayoutMensagemDialog = 225;
		}
	}
	
	private void redefinirPaddingTextView(int count){
		if(count <= 5){
			topTextViewBalaoresultado = 15;
			bottomTextViewBalaoresultado = 20;
			leftTextViewBalaoresultado = 20;
			rightTextViewBalaoresultado = 20;
		}else if(count > 5 && count <= 15){
			topTextViewBalaoresultado = 40;
			bottomTextViewBalaoresultado = 20;
			leftTextViewBalaoresultado = 20;
			rightTextViewBalaoresultado = 20;
		}else if(count > 15 && count < 30){
			topTextViewBalaoresultado = 50;
			bottomTextViewBalaoresultado = 20;
			leftTextViewBalaoresultado = 30;
			rightTextViewBalaoresultado = 30;
		}else if(count >= 30 && count <= 40){
			topTextViewBalaoresultado = 40;
			bottomTextViewBalaoresultado = 20;
			leftTextViewBalaoresultado = 30;
			rightTextViewBalaoresultado = 30;
		}else if(count > 40){
			topTextViewBalaoresultado = 55;
			bottomTextViewBalaoresultado = 30;
			leftTextViewBalaoresultado = 33;
			rightTextViewBalaoresultado = 33;
		}
	}

	public Bitmap definirLinearLayoutBalao() {
		redefinirTamanhoLinearLayoutBalao(mensagemDigitada().length());
		redefinirPaddingTextView(mensagemDigitada().length());
		
		linearLayoutBalaoResultado = new RelativeLayout(getContext());
		linearLayoutBalaoResultado.setBackgroundResource(resourceIdBackGround);
		linearLayoutBalaoResultado.layout(0, 0, widthLinearLayoutMensagemDialog, heightLinearLayoutMensagemDialog);

		textViewBalaoResultado = new TextView(getContext());
		textViewBalaoResultado.setTextColor(Color.BLACK);
		textViewBalaoResultado.setTextAppearance(getContext(), R.style.boldText);
		textViewBalaoResultado.setTypeface(getComicSansFont()); 

		textViewBalaoResultado.setText(mensagemDigitada());
		textViewBalaoResultado.layout(0, 0, widthLinearLayoutMensagemDialog, heightLinearLayoutMensagemDialog);
		textViewBalaoResultado.setGravity(Gravity.CENTER);
		textViewBalaoResultado.setPadding(leftTextViewBalaoresultado, topTextViewBalaoresultado, rightTextViewBalaoresultado, bottomTextViewBalaoresultado);
		linearLayoutBalaoResultado.addView(textViewBalaoResultado);

		linearLayoutBalaoResultado.setDrawingCacheEnabled(true);
		return Bitmap
				.createBitmap(linearLayoutBalaoResultado.getDrawingCache());
	}

	@Override
	public void onClick(View v) {
	}

	public String mensagemDigitada() {
		return editTextMensagem.getText().toString();
	}
	
	public Boolean confirmarMensagemClicado(View view){
		return view.equals(buttonConfirmarMensagem); 
	}
	
	public void fecharDialog() {
		dismiss();
	}
}
