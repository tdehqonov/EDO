package uz.sicnt.eimzo

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.custom_row.view.*
import kotlinx.android.synthetic.main.fragment_custom_dialog.*
import kotlinx.android.synthetic.main.fragment_custom_dialog.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uz.sicnt.eimzo.view.DocFragment
import uz.sicnt.eimzo.view.ViewFragment
import java.util.*
import kotlin.collections.ArrayList

class CustomDialogFragment
    (ArrayResponse: JSONArray?, access_token: String?,document_type:String) : DialogFragment() {
    var arrayList = ArrayList<String>()
    var ArrayResponse: JSONArray? = ArrayResponse
    var mCount = 0
    var maccess_token = access_token
    var tuzuvch_fio=""
    var document_type=document_type

    init {
        mCount = ArrayResponse?.length()!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var rootView: View = inflater.inflate(R.layout.fragment_custom_dialog, container, false)

        var istablet: Boolean? = null
        istablet = isTablet(activity as Context)

        val activity = activity as Context

        var lvNotificationDocument = rootView.findViewById<ListView>(R.id.lvNotificationDocument)

        val MyCustomAdapter: ArrayAdapter<String> =
                object : ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, arrayList) {

                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                        val layoutInflater = LayoutInflater.from(activity)
//                        var customRow = layoutInflater.inflate(R.layout.custom_row, parent, false)

//                        if (istablet == false){
//                            customRow = layoutInflater.inflate(R.layout.custom_row_mobile, parent, false)
//                        }
                        var customRow = layoutInflater.inflate(R.layout.custom_row_mobile, parent, false)
                        customRow.tvdocument_typeID.text = ArrayResponse?.getJSONObject(position)?.getJSONObject("document_type")?.optString("name_uz_latin")
                        customRow.tvdocument_templateID.text = ArrayResponse?.getJSONObject(position)?.getJSONObject("document_template")?.optString("name_uz_latin")
                        customRow.tvdocument_dateID.text = ArrayResponse?.getJSONObject(position)?.optString("document_date")
                        customRow.tvdocument_numberID.text = ArrayResponse?.getJSONObject(position)?.optString("document_number")
                        customRow.tvpdf_file_nameID.text = ArrayResponse?.getJSONObject(position)?.optString("pdf_file_name")
                        if (position % 2 == 0) {
                            customRow.setBackgroundColor(Color.LTGRAY)
                        }
                        return customRow
                    }

                    override fun getCount(): Int {
                        return mCount
                    }

                    override fun getItemId(position: Int): Long {
                        return position.toLong()
                    }
                }

        lvNotificationDocument.adapter = MyCustomAdapter
        lvNotificationDocument.setOnItemClickListener { parent, view, position, id ->
            var pdf_file_name = ArrayResponse?.getJSONObject(position)?.optString("pdf_file_name")
           rootView.progressBar3ID.visibility = View.VISIBLE
            RequsetWithDocument(pdf_file_name, maccess_token)
        }

        rootView.btnBarchasiID.setOnClickListener {
            dismiss()
            val notificationFragment = DocFragment()
            val bundle = Bundle()
            bundle.putString("token_type", token_type)
            bundle.putString("access_token", access_token)
            bundle.putString("title", "Notification")
            bundle.putString("document_type", document_type)
            notificationFragment.arguments = bundle
            val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            transaction?.addToBackStack(null)?.replace(R.id.fragment, notificationFragment)?.commit()
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()
        progressBar3ID.visibility = View.INVISIBLE
    }

    private fun RequsetWithDocument(pdf_file_name: String?, access_token: String?) {

        val stringRequest: StringRequest = object : StringRequest(
                Request.Method.POST, ConnectURL().DOCUMENTURL,
                Response.Listener { response ->
                    println("response :" + response.toString())
                    try {

                        val jsonObject = JSONObject(response)
                        val jsonDocuments = jsonObject.getJSONObject("document")
                        var pdf64 = jsonDocuments.getString("pdf")
                        val base64 = jsonDocuments.getString("base64")
                        var reaction_show = jsonDocuments.optString("reaction_show")
                        var reaction_status = jsonDocuments.optString("reaction_status")
                        var resolution_show = jsonDocuments.optBoolean("resolution_show")
                        var id = jsonDocuments.optString("id")
                        var action_type_id = jsonDocuments.optString("action_type_id")
                        var status = jsonDocuments.optInt("status")
                        var document_signers = jsonDocuments.getJSONArray("document_signers")
                        var document_files = jsonObject.getJSONArray("document_files")
                        var firstname_uz_latin: String? = null
                        var assignment: String? = null
                        var due_at: String? = null
                        var resolution_fio: String? = null

                        if (jsonDocuments.has("resolution")) {
                            var resolution = jsonDocuments.getJSONObject("resolution")
                            firstname_uz_latin = resolution.getJSONObject("parent_employee").optString("firstname_uz_latin")
                            firstname_uz_latin = firstname_uz_latin + " " + resolution.getJSONObject("parent_employee").optString("lastname_uz_latin") + "...             Topshiriq"
                            assignment = resolution.optString("assignment")
                            due_at = resolution.optString("due_at")
                            resolution_fio = resolution.optString("fio")

                        }


                        for (i in 0 until document_signers.length()) {
                            if (document_signers.getJSONObject(i).optString("action_type_id").equals("6") == true) {
                                tuzuvch_fio = document_signers.getJSONObject(i).optString("fio")
                            }
                        }

                        if (pdf64 != null) {
//                            val intent = Intent(activity, MainActivity::class.java)
//
//                            intent.putExtra("pdf64", "$pdf64")
//                            intent.putExtra("base64", "$base64")
//
//                            intent.putExtra("reaction_show", "$reaction_show")
//                            intent.putExtra("reaction_status", "$reaction_status")
//                            intent.putExtra("action_type_id", "$action_type_id")
//                            intent.putExtra("id", "$id")
//                            intent.putExtra("access_token", "$access_token")
//                            intent.putExtra("documentStatus", "$status")
//                            intent.putExtra("firstname_uz_latin", "$firstname_uz_latin")
//                            intent.putExtra("assignment", "$assignment")
//                            intent.putExtra("due_at", "$due_at")
//                            intent.putExtra("resolution_fio", "$resolution_fio")
//                            dismiss()
//                            startActivity(intent)

                            val viewFragment = ViewFragment()
                            val bundle = Bundle()
                            bundle.putString("pdf64", "$pdf64")
                            bundle.putString("base64", "$base64")
                            bundle.putString("reaction_show", "$reaction_show")
                            bundle.putString("reaction_status", "$reaction_status")
                            bundle.putBoolean("resolution_show", resolution_show)
                            bundle.putString("action_type_id", "$action_type_id")
                            bundle.putString("id", "$id")
                            bundle.putString("access_token", "$access_token")
                            bundle.putString("documentStatus", "$status")
                            bundle.putString("firstname_uz_latin", "$firstname_uz_latin")
                            bundle.putString("assignment", "$assignment")
                            bundle.putString("due_at", "$due_at")
                            bundle.putString("resolution_fio", "$resolution_fio")
                            bundle.putString("document_files", "${document_files.toString()}")
                            viewFragment.arguments = bundle
                            val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
                            dismiss()
                            transaction?.addToBackStack(null)?.replace(R.id.fragment, viewFragment)?.commit()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
//                    Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show()
                }) {

            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun getBody(): ByteArray? {
                var params: MutableMap<String, String> = HashMap()
                params.put("pdf_file_name", "$pdf_file_name")
                var jsonObj = JSONObject(params.toString())
                return jsonObj.toString().toByteArray()
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params.put("Authorization", "Bearer $access_token")
                params.put("Content-Type", "application/json")
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
        requestQueue.addRequestFinishedListener<Any> { requestQueue.cache.clear() }
    }

    fun isTablet(ctx: Context): Boolean {
        return ctx.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

}
