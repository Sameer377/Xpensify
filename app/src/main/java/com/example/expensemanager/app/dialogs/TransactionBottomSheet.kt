package com.example.expensemanager.app.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.example.expensemanager.app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransactionBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v = inflater.inflate(R.layout.transaction_bottom_sheet_dialog, container, false)

        var e = v.findViewById<AppCompatEditText>(R.id.bottom_sheet_edit1)

        e.requestFocus()





        return v
    }
}