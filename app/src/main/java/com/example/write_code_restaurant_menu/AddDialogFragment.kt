package com.example.write_code_restaurant_menu

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.DialogFragment

class AddDialogFragment(val viewCommentButton: AppCompatButton) : DialogFragment() {
    lateinit var commentEditText: EditText
    lateinit var btnSubmit: View
    lateinit var buttonCancel: View
    private lateinit var dialogListener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_dialog,container, false)
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentEditText = view.findViewById(R.id.edit_text_comment)
        btnSubmit = view.findViewById(R.id.button_submit)
        buttonCancel = view.findViewById(R.id.button_cancel)

        val commentText = commentEditText
        this.btnSubmit.setOnClickListener{
            if (commentText.text.toString().isNotEmpty()) {
                // Notifica a MainActivity sobre a mudança de estado
                dialogListener.onCommentSubmitted(commentText.text.toString(), viewCommentButton)
                dismiss()
            }
        }
        buttonCancel.setOnClickListener{
            commentText.setText("")
            dialogListener.onCommentSubmitted(commentText.text.toString(), viewCommentButton)
            dismiss()
        }
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verifica se a Activity implementa a interface DialogListener
        if (context is DialogListener) {
            dialogListener = context
        } else {
            throw RuntimeException("$context must implement DialogListener")
        }
    }
}