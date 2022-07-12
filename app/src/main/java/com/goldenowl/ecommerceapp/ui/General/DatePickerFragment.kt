package com.goldenowl.ecommerceapp.ui.General

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class DatePickerFragment(private val editText: TextInputEditText, private val dob: String) :
    DialogFragment(),
    DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateString = dob.split("/")
        val dialog = DatePickerDialog(
            requireContext(),
            this,
            dateString[2].toInt(),
            dateString[1].toInt(),
            dateString[0].toInt()
        )

        dialog.datePicker.maxDate = Date().time
        return dialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val str = "${day}/${month + 1}/${year}"
        editText.setText(str)
    }
}