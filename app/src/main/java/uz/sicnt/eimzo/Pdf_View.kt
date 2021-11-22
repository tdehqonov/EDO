package uz.sicnt.eimzo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nayaastra.skewpdfview.SkewPdfView

class Pdf_View(pdfFile: String) :DialogFragment() {

    var pdf_File=pdfFile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(R.layout.pdf_view, container, false)
//
        val pdf_url = "${ConnectURL().PDFURL}$pdf_File"
        var skewPdfView: SkewPdfView?=null
        skewPdfView=rootView.findViewById(R.id.skewPdfView)
        skewPdfView?.loadPdf(pdf_url)

        val activity = activity as Context

        return  rootView
                //  inflater.inflate(R.layout.pdf_view_dialog, container, true)
               //super.onCreateView(inflater, container, savedInstanceState)
    }
        override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDestroy() {
        dismiss()
        super.onDestroy()
    }
}