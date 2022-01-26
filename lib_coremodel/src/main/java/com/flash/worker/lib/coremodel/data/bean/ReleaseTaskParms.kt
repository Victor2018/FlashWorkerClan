package com.flash.worker.lib.coremodel.data.bean

import com.flash.worker.lib.coremodel.data.parm.ReleaseTaskParm
import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReleaseTaskParms
 * Author: Victor
 * Date: 2021/12/6 18:14
 * Description: 
 * -----------------------------------------------------------------
 */
class ReleaseTaskParms: Serializable {
    var body: ReleaseTaskParm? = null
    var taskPrepaidDetailData: TaskPrepaidDetailData? = null
    var status: Int = 0//雇佣发布状态,1,编辑中；2,发布中；3，已下架；4，已驳回
}