package com.example.pillwatch.utils

import com.example.pillwatch.R

enum class TakenStatus(val labelResId: Int) {
    TAKEN(R.string.taken),
    POSTPONED(R.string.postponed),
    MISSED(R.string.missed)
}