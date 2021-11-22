package uz.sicnt.eimzo

import android.widget.Button

data class ExamleItem(
    val txtDocument_number: String,
    val txtDocument_date: String,
    val txtName_uz_latin: String,
    val txtLastname_uz_latin: String,
    val txtFrom_department: String,
    val txtTo_department: String,
    val txtDocument_signers: String,
    val txtStatus:String,
    val txtPdf_file_name:String
)