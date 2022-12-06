package com.protone.common.entity

class BaseResponse<T>(
    var success: Boolean = false,
    var data: T,
    var code: Int = -1,
    var message: String = ""
)