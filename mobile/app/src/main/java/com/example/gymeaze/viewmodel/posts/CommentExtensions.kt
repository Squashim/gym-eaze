package com.example.gymeaze.viewmodel.posts

fun cleanedCommentLength(text: String): Int {
    return text.trim()
        .replace(Regex("[ \\t]+"), " ")
        .replace(Regex("[\\n\\r]+"), "\n")
        .length
}