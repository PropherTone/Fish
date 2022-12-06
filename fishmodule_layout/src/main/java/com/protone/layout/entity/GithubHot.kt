package com.protone.layout.entity

data class GithubHot(
    val incomplete_results: Boolean,
    val items: List<Item>,
    val total_count: Int
)