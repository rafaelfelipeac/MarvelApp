package com.rafaelfelipeac.marvelapp.features.details.domain.model

data class DetailInfoList(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<DetailInfo>
)
