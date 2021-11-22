package uz.sicnt.eimzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import kotlinx.android.synthetic.main.activity_pdf.*

class Pdf_activity : AppCompatActivity() {

    var decodedPDF: ByteArray? = null
    var pdfView: PDFView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        pdfView = findViewById(R.id.pdfView5)

        decodedPDF = intent?.getByteArrayExtra("pdffile")

        pdfView?.fromBytes(decodedPDF)
            ?.password(null)
            ?.defaultPage(0)
            ?.enableSwipe(true)
            ?.swipeHorizontal(false)
            ?.enableSwipe(true)
            ?.enableDoubletap(true)
            ?.enableAntialiasing(true)
//                ?.autoSpacing(true)
                ?.scrollHandle(DefaultScrollHandle(this))
//                    ?.onDraw { canvas, pageWidth, pageHeight, displayedPage ->
//                    }?.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->
//
//                    }
//                    ?.onPageChange { page, pageCount ->
//                        setTitle(String.format("%s / %s", page + 1, pageCount));
//                    }?.onPageError { page, t ->
//                        Toast.makeText(this, "Error wille opening page" + page, Toast.LENGTH_SHORT).show()
//                        Log.d("ERROR", t.localizedMessage)
//                    }
//                    ?.onTap { true }
//                    ?.onRender { nbPages, pageWidth, pageHeight ->
//                    }
            ?.enableAnnotationRendering(false)
            ?.spacing(5)
            ?.load()
    }
}