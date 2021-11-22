package uz.sicnt.eimzo.view

//import kotlinx.android.synthetic.main.activity_documents.view.*
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import kotlinx.android.synthetic.main.fragment_pdf.view.*
import uz.sicnt.eimzo.ConnectURL
import uz.sicnt.eimzo.R
import java.io.IOException
import java.io.InputStream
import java.net.URL


class PDFFrag : DialogFragment() {


    var decodedPDF: ByteArray? = null
    var pdfView: PDFView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_pdf, container, false)
        val relativeLayout = view.rellayout

        pdfView = view.pdfView5
        decodedPDF = arguments?.getByteArray("pdffile")
        var pdf_File= arguments?.getString("pdffileid")

        if (decodedPDF?.isNotEmpty() == true) {
            pdfView?.fromBytes(decodedPDF)
                ?.password(null)
                ?.defaultPage(0)
                ?.enableSwipe(true)
                ?.swipeHorizontal(false)
                ?.enableSwipe(true)
                ?.enableDoubletap(true)
                ?.enableAntialiasing(true)
                ?.scrollHandle(DefaultScrollHandle(context))
                ?.enableAnnotationRendering(false)
                ?.spacing(10)
                ?.load()
        }

        if (pdf_File?.isNotEmpty() == true){
            object : AsyncTask<Void?, Void?, Void?>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    try {
                        val input: InputStream =
                            URL("${ConnectURL().PDFURL}$pdf_File").openStream()
                        pdfView?.fromStream(input)
                            ?.spacing(10)
                            ?.load()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    return null
                }
            }.execute()

        }
        return view
    }
}
