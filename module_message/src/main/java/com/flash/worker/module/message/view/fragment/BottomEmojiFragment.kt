package com.flash.worker.module.message.view.fragment

import android.os.Bundle
import android.widget.EditText
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.module.message.R
import com.flash.worker.module.message.interfaces.IEmoticonSelectedListener
import kotlinx.android.synthetic.main.frag_bottom_emoji.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BottomEmojiFragment
 * Author: Victor
 * Date: 2021/5/10 16:08
 * Description: emoji表情面板
 * -----------------------------------------------------------------
 */
class BottomEmojiFragment: BaseFragment(), IEmoticonSelectedListener {

    companion object {

        fun newInstance(): BottomEmojiFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): BottomEmojiFragment {
            val fragment = BottomEmojiFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    var mEvMessage: EditText? = null

    override fun getLayoutResource() = R.layout.frag_bottom_emoji

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initialize()
    }

    fun initialize () {
        mEmoticonPickerView.withSticker = true//开启贴图功能
        mEmoticonPickerView.show(this)//显示表情视图并设置监听
        mEmoticonPickerView.attachEditText(mEvMessage)//显示表情视图并设置监听
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onEmojiSelected(key: String?) {
        Loger.e(TAG,"onEmojiSelected-key = $key")
    }

    override fun onStickerSelected(categoryName: String?, stickerName: String?) {
        Loger.e(TAG,"onEmojiSelected-categoryName = $categoryName")
        Loger.e(TAG,"onEmojiSelected-stickerName = $stickerName")
    }
}