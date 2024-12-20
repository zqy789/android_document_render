package com.document.render.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.document.render.R
import com.document.render.interfaces.OnPdfItemClickListener
import com.document.render.util.ViewUtils.hide
import com.document.render.util.ViewUtils.show
import kotlinx.android.synthetic.main.list_item_pdf.view.container_view
import kotlinx.android.synthetic.main.list_item_pdf.view.pageView
import kotlinx.android.synthetic.main.pdf_view_page_loading_layout.view.pdf_view_page_loading_progress

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PdfViewAdapter
 * Author: Victor
 * Date: 2023/09/28 11:17
 * Description: 
 * -----------------------------------------------------------------
 */

internal class PdfViewAdapter(
    private val renderer: PdfRendererCore?,
    private val pageSpacing: Rect,
    private val enableLoadingForPages: Boolean,
    private val listener: OnPdfItemClickListener?
) :
    RecyclerView.Adapter<PdfViewAdapter.PdfPageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        return PdfPageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_pdf, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        try {
            return renderer?.getPageCount() ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        holder.bindView()
    }

    inner class PdfPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnAttachStateChangeListener {

        fun bindView() {
            itemView.container_view.setOnClickListener {
                listener?.OnPdfItemClick(adapterPosition)
            }
        }

        private fun handleLoadingForPage(position: Int) {
            if (!enableLoadingForPages) {
                itemView.pdf_view_page_loading_progress.hide()
                return
            }

            if (renderer?.pageExistInCache(position) == true) {
                itemView.pdf_view_page_loading_progress.hide()
            } else {
                itemView.pdf_view_page_loading_progress.show()
            }
        }

        init {
            itemView.addOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(p0: View) {
            handleLoadingForPage(adapterPosition)
            renderer?.renderPage(adapterPosition) { bitmap: Bitmap?, pageNo: Int ->
                if (pageNo == adapterPosition) {
                    bitmap?.let {
                        itemView.container_view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            height =
                                (itemView.container_view.width.toFloat() / ((bitmap.width.toFloat() / bitmap.height.toFloat()))).toInt()
                            this.topMargin = pageSpacing.top
                            this.leftMargin = pageSpacing.left
                            this.rightMargin = pageSpacing.right
                            this.bottomMargin = pageSpacing.bottom
                        }
                        itemView.pageView.setImageBitmap(bitmap)
                        itemView.pageView.animation = AlphaAnimation(0F, 1F).apply {
                            interpolator = LinearInterpolator()
                            duration = 200
                        }
                        itemView.pdf_view_page_loading_progress.hide()
                    }
                }
            }
        }

        override fun onViewDetachedFromWindow(p0: View) {
            itemView.pageView.setImageBitmap(null)
            itemView.pageView.clearAnimation()
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)

        //canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }
}