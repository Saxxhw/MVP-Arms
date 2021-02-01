package com.zkyc.arms.feature

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.core.os.bundleOf
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.luck.picture.lib.photoview.PhotoView
import com.zkyc.arms.R
import com.zkyc.arms.base.activity.BaseVBActivity
import com.zkyc.arms.databinding.ActivityPicturePreviewBinding
import com.zkyc.arms.glide.load

class PicturePreviewActivity : BaseVBActivity<ActivityPicturePreviewBinding>(), ViewPager.OnPageChangeListener {

    companion object {

        private const val ARG_PICTURE_URLS = "picture_urls"
        private const val ARG_POSITION = "position"

        fun startWith(activity: Activity, urls: List<String>, position: Int) = with(activity) {
            val starter = Intent(this, PicturePreviewActivity::class.java)
            starter.putExtras(
                bundleOf(
                    ARG_PICTURE_URLS to urls.toTypedArray(),
                    ARG_POSITION to position
                )
            )
            startActivity(starter)
        }

        fun startWith(activity: Activity, url: String) = with(activity) {
            val starter = Intent(this, PicturePreviewActivity::class.java)
            starter.putExtras(bundleOf(ARG_PICTURE_URLS to arrayOf(url), ARG_POSITION to 0))
            startActivity(starter)
        }
    }

    // 图片链接
    private var mPictureUrls: Array<String>? = null

    // 图片位置
    private var mPosition: Int = 0

    override fun inflateVB(inflater: LayoutInflater) =
        ActivityPicturePreviewBinding.inflate(inflater)

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)
        mPictureUrls = extras.getStringArray(ARG_PICTURE_URLS)
        mPosition = extras.getInt(ARG_POSITION)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        with(mBinding.vpPictures) {
            adapter = PicturesAdapter(mPictureUrls)
            addOnPageChangeListener(this@PicturePreviewActivity)
            currentItem = mPosition
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.vpPictures.removeOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        mBinding.tvPageNum.text = getString(R.string.picture_preview_page_num, position + 1, mPictureUrls?.size)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    /**
     * 适配器
     */
    private class PicturesAdapter(private val data: Array<String>?) : PagerAdapter() {

        override fun getCount() = data?.size ?: 0

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView = PhotoView(container.context)
            photoView.load(data?.get(position))
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
    }
}