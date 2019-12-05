package com.example.ourmessenger.modules


class Message(
    val text: String,
    val firstUser: String,
    val secondUser: String,
    val messageId: String,
    val timeStamp: Long
) {
    constructor() : this("", "", "", "", -1)
}