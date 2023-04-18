package com.example.arec.model.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.arec.R

class ImagePagerAdapter(private val imageUrls: List<String?>, private val context: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)

        Glide.with(context)
            .load(imageUrls[position])
            .placeholder(R.drawable.ic_loading)
            .into(imageView)

        // Add padding to the top and bottom of the image to create vertical orientation
        val paddingPx = convertDpToPixels(context, 16) // 16 dp, adjust as needed
        imageView.setPadding(0, paddingPx, 0, paddingPx)


        container.addView(imageView)
        return imageView
    }
    private fun convertDpToPixels(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}
