package com.example.pillwatch.utils

import com.example.pillwatch.R

enum class FilterOption(val labelResId: Int) {
    NON_ARCHIVED(R.string.filter_option_nonarchived),
    ARCHIVED(R.string.filter_option_archived),
    ALL(R.string.filter_option_all)
}