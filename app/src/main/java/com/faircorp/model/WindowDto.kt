package com.faircorp.model

data class WindowDto(
    val id: Long?,
    val name: String,
    val roomId: Long,
    val windowStatus: Status)