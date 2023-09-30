package com.application.stations.model

data class ResponseResult<T>(
    val status: Boolean,
    val data: T?,
    val message: String? = "",
)