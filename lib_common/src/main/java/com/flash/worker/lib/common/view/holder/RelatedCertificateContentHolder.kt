package com.flash.worker.lib.common.view.holder

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import kotlinx.android.synthetic.main.rv_related_certificate_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RelatedCertificateContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class RelatedCertificateContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (context: Context, data: WorkPicInfo?,isPreview: Boolean) {
        if (TextUtils.isEmpty(data?.pic)) {
            itemView.mIvRelatedCertificateBorder.visibility = View.VISIBLE
            itemView.mIvRelatedCertificateAdd.visibility = View.VISIBLE
            itemView.mTvRelatedCertificateTitle.visibility = View.VISIBLE

            itemView.mIvRelatedCertificate.visibility = View.GONE
            itemView.mIvRelatedCertificateDel.visibility = View.GONE
        } else {
            ImageUtils.instance.loadImage(context,itemView.mIvRelatedCertificate,data?.pic)

            itemView.mIvRelatedCertificateBorder.visibility = View.GONE
            itemView.mIvRelatedCertificateAdd.visibility = View.GONE
            itemView.mTvRelatedCertificateTitle.visibility = View.GONE

            itemView.mIvRelatedCertificate.visibility = View.VISIBLE

            if (isPreview) {
                itemView.mIvRelatedCertificateDel.visibility = View.GONE
            } else {
                itemView.mIvRelatedCertificateDel.visibility = View.VISIBLE
            }
        }

        itemView.mIvRelatedCertificateDel.setOnClickListener(this)
        itemView.mClCertificateRoot.setOnClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}