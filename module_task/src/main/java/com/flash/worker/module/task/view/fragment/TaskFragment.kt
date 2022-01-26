package com.flash.worker.module.task.view.fragment

import android.os.Bundle
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.module.task.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskFragment
 * Author: Victor
 * Date: 2021/12/1 10:37
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskFragment: BaseFragment() {
    companion object {

        fun newInstance(): TaskFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): TaskFragment {
            val fragment = TaskFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_task
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun handleBackEvent() = false

    override fun freshFragData() {
    }
}