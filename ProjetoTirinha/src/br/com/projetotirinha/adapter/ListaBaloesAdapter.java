package br.com.projetotirinha.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import br.com.projetotirinha.R;

public class ListaBaloesAdapter extends BaseAdapter {

	private int mGalleryItemBackground;
	private Context contexto;
	private Integer[] resourceImageList = { R.drawable.balao,R.drawable.balao_invertido,
			R.drawable.thought_bubble, R.drawable.thought_bubble_invertido};

	public ListaBaloesAdapter(Context contexto) {
		this.contexto = contexto;
		TypedArray styledAttributes = this.contexto
				.obtainStyledAttributes(R.styleable.Gallery1);
		mGalleryItemBackground = styledAttributes.getResourceId(
				R.styleable.Gallery1_android_galleryItemBackground, 0);
		styledAttributes.recycle();
	}

	public int getCount() {
		return getResourceImageList().length;
	}

	public Integer[] getResourceImageList() {
		return this.resourceImageList;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(contexto);

		imageView.setImageResource(getResourceImageList()[position]);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setLayoutParams(new Gallery.LayoutParams(150, 125));
		imageView.setBackgroundResource(mGalleryItemBackground);

		return imageView;
	}

}
