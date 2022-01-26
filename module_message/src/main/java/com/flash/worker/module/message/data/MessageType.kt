package com.flash.worker.module.message.data


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageType
 * Author: Victor
 * Date: 2020/12/30 16:13
 * Description: 
 * -----------------------------------------------------------------
 */
object MessageType {
    var TYPE_SEND_TEXT = 3
    var TYPE_RECV_TEXT = 4

    var TYPE_SEND_IMAGE = 5
    var TYPE_RECV_IMAGE = 6

    var TYPE_SEND_JOB = 7
    var TYPE_RECV_JOB = 8

    var TYPE_SEND_RESUME = 9
    var TYPE_RECV_RESUME = 10

    var TYPE_SEND_LOCATION = 11
    var TYPE_RECV_LOCATION = 12

    var TYPE_SEND_T_RELEASE = 13
    var TYPE_RECV_T_RELEASE = 14

    var TYPE_SEND_TASK = 15
    var TYPE_RECV_TASK = 16

    var TYPE_NOTIFY = 15
}