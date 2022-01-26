package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearSnapHelper
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.lib.common.view.adapter.RelatedCertificateAdapter
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.DisputeHistoryInfo
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import kotlinx.android.synthetic.main.rv_report_modify_his_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportModifyHistoryContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportModifyHistoryContentHolder(itemView: View) : ContentViewHolder(itemView)  {

    var categoryPosition: Int = 0

    fun bindData (data: DisputeHistoryInfo?, listener: AdapterView.OnItemClickListener?) {
        categoryPosition = data?.categoryPosition ?: 0

        itemView.mRvCertificate.onFlingListener = null
        LinearSnapHelper().attachToRecyclerView(itemView.mRvCertificate)

        itemView.mTvTime.text = data?.createTime
        itemView.mTvReportCompany.text = "【举报对象】${data?.employerName}"
        itemView.mTvEmployer.text = "【雇主】${data?.employerUsername}"
        itemView.mTvMatter.text = getComplaintItemsTxt(data?.complaintItems)
        itemView.mTvAppeal.text = getComplaintRequirementsTxt(data?.complaintRequirements)

        var cellAdapter = RelatedCertificateAdapter(itemView.context!!,listener)
        cellAdapter.isPreview = true
        cellAdapter.add(getcomplaintPics(data?.complaintPics))

        itemView.mRvCertificate.adapter = cellAdapter

        var picCount = data?.complaintPics?.size ?: 0
        if (picCount > 0) {
            itemView.tv_certificate.show()
            itemView.mRvCertificate.show()
        } else {
            itemView.tv_certificate.hide()
            itemView.mRvCertificate.hide()
        }

    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition, categoryPosition.toLong())
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

    fun getComplaintItemsTxt (complaintItems: List<String>?): String? {
        var complaintItemsSb = StringBuffer()

        var count = complaintItems?.size ?: 0
        for (i in 0 until count) {
            if (i < count - 1) {
                complaintItemsSb.append((i+1).toString() + "," + complaintItems?.get(i) + "\n")
            } else {
                complaintItemsSb.append((i+1).toString() + "," + complaintItems?.get(i))
            }
        }
        return complaintItemsSb.toString()
    }

    fun getComplaintRequirementsTxt (complaintRequirements: List<String>?): String? {
        var complaintItemsSb = StringBuffer()

        var count = complaintRequirements?.size ?: 0
        for (i in 0 until count) {
            if (i < count - 1) {
                complaintItemsSb.append((i+1).toString() + "," + complaintRequirements?.get(i) + "\n")
            } else {
                complaintItemsSb.append((i+1).toString() + "," + complaintRequirements?.get(i))
            }
        }
        return complaintItemsSb.toString()
    }

    fun getcomplaintPics (complaintPics: List<String>?): List<WorkPicInfo> {
        var pics = ArrayList<WorkPicInfo>()
        complaintPics?.forEach {
            if (!TextUtils.isEmpty(it)) {
                var item = WorkPicInfo()
                item.pic = it
                pics.add(item)
            }
        }
        return pics
    }

}