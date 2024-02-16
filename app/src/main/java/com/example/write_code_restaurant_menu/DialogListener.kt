package com.example.write_code_restaurant_menu
import androidx.appcompat.widget.AppCompatButton

interface DialogListener {
    fun onCommentSubmitted(comment: String, view: AppCompatButton)
}
