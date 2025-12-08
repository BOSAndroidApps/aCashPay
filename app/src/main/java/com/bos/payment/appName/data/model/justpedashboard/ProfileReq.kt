package com.bos.payment.appName.data.model.justpedashboard

import java.io.File

data class ProfileReq(
    val UserId: String,
    val TaskType: String,
    val profileImage: File?
)
