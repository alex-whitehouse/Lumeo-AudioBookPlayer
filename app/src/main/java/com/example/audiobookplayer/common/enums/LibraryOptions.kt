package com.example.audiobookplayer.common.enums

sealed class SortOption(val value: String) {
    object RECENT : SortOption("recent")
    object TITLE : SortOption("title")
    object PROGRESS : SortOption("progress")
}

sealed class FilterStatus(val value: String) {
    object ALL : FilterStatus("all")
    object NEW : FilterStatus("new")
    object STARTED : FilterStatus("started")
    object FINISHED : FilterStatus("finished")
}
