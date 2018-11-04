package cn.njcit.showimage.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * *************************************************************** 
 * �ļ����� : GalleryFlow.java 
 * ����ʱ�� : 2010-11-2 ����05:27:46 
 * �ļ����� : �Զ���Gallery��ͼ��ʵ��ͼƬ��תЧ��
 ***************************************************************** 
 */
public class GalleryFlow extends Gallery {

	/**
	 * ����ImageView�ľ���任
	 */
	private Camera mCamera = new Camera();

	private int mMaxRotationAngle = 60;

	/**
	 * The maximum zoom on the centre Child
	 */
	private int mMaxZoom = -120;

	/**
	 * Coverflow������
	 */
	private int mCoveflowCenter;

	public GalleryFlow(Context context) {
		super(context);
		this.setStaticTransformationsEnabled(true);
	}

	public GalleryFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public GalleryFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
	}

	/**
	 * get set ����
	 * 
	 * @return
	 */
	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	public int getMaxZoom() {
		return mMaxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	/**
	 * ��ȡCoverflow������λ��
	 * 
	 * @return
	 */
	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	/**
	 * ��ȡ����ͼ������λ��
	 * 
	 * @param view
	 * @return
	 */
	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {

		final int childCenter = getCenterOfView(child);

		final int childWidth = child.getWidth();
		int rotationAngle = 0;
		/**
		 * ����transformation
		 */
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0);
		} else {
			/**
			 * ������ת�Ƕ�
			 */
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
						: mMaxRotationAngle;
			}
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}

		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 
	 * ������ͼ��ת��Ӧ�Ƕ�
	 * 
	 * @param imageView
	 *            the ImageView whose bitmap we want to rotate
	 * @param t
	 *            transformation
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap
	 */
	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {
		/**
		 * ���������ǰ״̬
		 */
		mCamera.save();
		/**
		 * ��ȡ�任����
		 */
		final Matrix imageMatrix = t.getMatrix();

		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);

		/**
		 * ��Z���������ƶ�camera���ӽǣ�ʵ��Ч��Ϊ�Ŵ�ͼƬ�� �����Y�����ƶ�����ͼƬ�����ƶ���X���϶�ӦͼƬ�����ƶ���
		 */
		mCamera.translate(0.0f, 0.0f, 100.0f);

		// As the angle of the view gets less, zoom in
		if (rotation < mMaxRotationAngle) {
			float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
			mCamera.translate(0.0f, 0.0f, zoomAmount);
		}

		/**
		 * ��Y������ת����ӦͼƬ�������﷭ת�������X������ת�����ӦͼƬ�������﷭ת��
		 */
		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));

		/**
		 * �ָ����ԭ״̬
		 */
		mCamera.restore();
	}
}
