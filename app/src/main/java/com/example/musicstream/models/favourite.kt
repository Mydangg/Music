package com.example.musicstream.models

data class favourite(
    var id_user : String,
    var id_song : String,
    var likes : Boolean
) {

    constructor() : this("","",false)
}