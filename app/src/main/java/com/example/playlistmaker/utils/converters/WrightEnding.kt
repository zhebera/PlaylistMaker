package com.example.playlistmaker.utils.converters

fun rightEndingTrack(number: Int): String {
    val num100 = number % 100
    if (num100 in 5..20) return "$number треков"
    else {
        val num10 = num100 % 10
        if (num10 == 1) return "$number трек"
        else if (num10 in 2..4) return "$number трека"
        else return "$number треков"
    }
}

fun rightEndingMinutes(number: Int): String {
    val num100 = number % 100
    if (num100 in 5..20) return "$number минут"
    else {
        val num10 = num100 % 10
        if (num10 == 1) return "$number минута"
        else if (num10 in 2..4) return "$number минуты"
        else return "$number минут"
    }
}