package com.lixue.aibei.universalimageloaderlib.core.display;

import android.graphics.Bitmap;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;

import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageViewAware;

/**
 * 具有圆角且有渐晕效果的图象显示器
 * Created by Administrator on 2016/3/29.
 */
public class RoundedVignetteBitmapDisplayer extends RoundedBitmapDisplayer {
    public RoundedVignetteBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
        super(cornerRadiusPixels, marginPixels);
    }

    @Override
    public void dispaly(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }
        imageAware.setImageDrawable(new RoundedVignetteDrawable(bitmap, cornerRadius, margin));
    }

    protected static class RoundedVignetteDrawable extends RoundedDrawable {
        RoundedVignetteDrawable(Bitmap bitmap, int cornerRadius, int margin) {
            super(bitmap, cornerRadius, margin);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            RadialGradient vignette = new RadialGradient(
                    mRect.centerX(), mRect.centerY() * 1.0f / 0.7f, mRect.centerX() * 1.3f,
                    new int[]{0, 0, 0x7f000000}, new float[]{0.0f, 0.7f, 1.0f},
                    Shader.TileMode.CLAMP);

            Matrix oval = new Matrix();
            oval.setScale(1.0f, 0.7f);
            vignette.setLocalMatrix(oval);

            paint.setShader(new ComposeShader(bitmapShader, vignette, PorterDuff.Mode.SRC_OVER));
        }
    }
}
