package com.app.fm001.utils

import kotlin.random.Random

fun ClosedFloatingPointRange<Float>.random(): Float {
    return Random.nextFloat() * (endInclusive - start) + start
} 