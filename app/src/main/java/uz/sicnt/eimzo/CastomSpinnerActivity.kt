package uz.sicnt.eimzo

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_castom_spinner.*
import kotlinx.android.synthetic.main.dialog_searchable_spinner.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class CastomSpinnerActivity : AppCompatActivity() {


    var arrayListSon: ArrayList<StringWithHodimTag> = ArrayList<StringWithHodimTag>()
    var dialog:Dialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_castom_spinner)

        arrayListSon.add(StringWithHodimTag("StOne","0"))
        arrayListSon.add(StringWithHodimTag("Tow","1"))
        arrayListSon.add(StringWithHodimTag("Uch","2"))

        tvSelectNumber.setOnClickListener {
            dialog= Dialog(this)
            dialog?.setContentView(R.layout.dialog_searchable_spinner)
            dialog?.window?.setLayout(650,800)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.show()
            var adapter=ArrayAdapter<StringWithHodimTag>(this,android.R.layout.simple_expandable_list_item_1,arrayListSon)

          dialog?.list_view?.adapter=adapter

            dialog?.edit_text?.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
    }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    RequsetWithHodim("")
                    adapter?.notifyDataSetChanged()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

           dialog?.list_view?.setOnItemClickListener(object :AdapterView.OnItemClickListener {

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    tvSelectNumber.text= adapter?.getItem(position).toString()
                    dialog?.dismiss()
                }
            })
        }
    }


    private fun RequsetWithHodim(search: String) {
  //      arrayListSon.clear()

        val URL = ConnectURL().HODIMURL
        var okHttpClient: OkHttpClient = OkHttpClient()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("search", "$search")
            .addFormDataPart("locale", "uz_latin")
            .build()
        val request: Request = Request
            .Builder()
            .url(URL)
            .method("POST", body)
            .addHeader(
                "Authorization",
                "Bearer $access_token"
            )
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {

                try {
                    val jsonResponse: JSONObject
                    if (response != null) {
                        val jsonResponse = JSONObject(response.body()?.string())
                        var jsonResponseDataArray = jsonResponse.getJSONArray("Employees")
                        arrayListSon.add(StringWithHodimTag("Hodimlarni tanlang", "0"))

                        for (i in 0 until jsonResponseDataArray.length()) {
                            var EmploeesID = jsonResponseDataArray.getJSONObject(i).optString("id")
                            var tabel = jsonResponseDataArray.getJSONObject(i).optString("tabel")
                            var firstname_uz_latin =
                                jsonResponseDataArray.getJSONObject(i).optString("firstname")
                            var lastname_uz_latin =
                                jsonResponseDataArray.getJSONObject(i).optString("lastname")
                            var middlename_uz_latin =
                                jsonResponseDataArray.getJSONObject(i).optString("middlename")
                            var main_staff =
                                jsonResponseDataArray.getJSONObject(i).getJSONArray("main_staff")
                            var name = main_staff.getJSONObject(0).optJSONObject("position")
                                .optString("name")
                            arrayListSon.add(
                                StringWithHodimTag(
                                    "$firstname_uz_latin $lastname_uz_latin $middlename_uz_latin \n\t\t $name",
                                    EmploeesID
                                )
                            )
                        }
                    } else {
                        println("JAVOB NULL")
                    }
                } catch (er: Exception) {
                    println("HATO " + er.toString())
                }
            }
        })
    }

}
