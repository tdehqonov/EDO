package uz.sicnt.eimzo.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.view.*
import kotlinx.android.synthetic.main.layouy_pagination.*
import kotlinx.android.synthetic.main.layouy_pagination.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uz.sicnt.eimzo.ConnectURL
import uz.sicnt.eimzo.ExamleItem
import uz.sicnt.eimzo.ExampleAdapter
import uz.sicnt.eimzo.R
import java.util.*
import kotlin.collections.ArrayList


class DocFragment : Fragment(), ExampleAdapter.OnItemClickListener, View.OnClickListener {

    private var recycler_view: RecyclerView? = null
    var exampleList1: List<ExamleItem>? = null
    var bundle: Bundle? = null
    var token_type: String? = null
    var access_token: String? = null
    var text_docs: TextView? = null
    var title: String? = null
    var document_type: String? = null
    var employee_id:String?=null
    var progressBar: ProgressBar? = null
    var jsonObjectResponse: JSONObject? = null
    var tuzuvch_fio = ""

    var from = 0
    var to = 0
    var total = 0
    var page = 1
    var itemsPerPage = 20
    var last_page = 0
    var izohlarButtonStatus=false
    var content:String?=null
    var document_number:String?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)


        recycler_view = view?.recycler_view
        progressBar = view?.progressBar
        text_docs = view?.text_docs

        bundle = arguments

        if (bundle != null) {
            token_type = bundle!!.getString("token_type", "").toString()
            access_token = bundle!!.getString("access_token", "").toString()
            title = bundle!!.getString("title", "").toString()
            document_type = bundle!!.getString("document_type", "").toString()
            employee_id = bundle!!.getString("employee_id", "").toString()
        }

        activity?.setTitle(title)

        RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)

        text_docs?.text = when(document_type){
            "inbox" -> "Kiruvchi Hujjatlar"
            "outbox" -> "Chiquvchi Hujjatlar"
            "draft" -> "Qoralama Hujjatlar"
            "cancel" -> "Bekor Qilingan"
            else -> "$document_type"
        }

        view.ivTopID.setOnClickListener(this)
        view.ivBackID.setOnClickListener(this)
        view.ivBottomID.setOnClickListener(this)
        view.ivRightID.setOnClickListener(this)
        // perform set on query text listener event
        // perform set on query text listener event
       view.simpleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                content=query
                RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)
                text_docs?.text = when(document_type) {
                    "inbox" -> "Kiruvchi Hujjatlar"
                    "outbox" -> "Chiquvchi Hujjatlar"
                    "draft" -> "Qoralama Hujjatlar"
                    "cancel" -> "Bekor Qilingan"
                    else -> "$document_type"
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                content=newText
                RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)
                text_docs?.text = when(document_type) {
                    "inbox" -> "Kiruvchi Hujjatlar"
                    "outbox" -> "Chiquvchi Hujjatlar"
                    "draft" -> "Qoralama Hujjatlar"
                    "cancel" -> "Bekor Qilingan"
                    else -> "$document_type"
                }
                return false
            }

        })

        view.svDocumentNumber.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                document_number=query
                RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)
                text_docs?.text = when(document_type) {
                    "inbox" -> "Kiruvchi Hujjatlar"
                    "outbox" -> "Chiquvchi Hujjatlar"
                    "draft" -> "Qoralama Hujjatlar"
                    "cancel" -> "Bekor Qilingan"
                    else -> "$document_type"
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                document_number=newText
                RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)
                text_docs?.text = when(document_type) {
                    "inbox" -> "Kiruvchi Hujjatlar"
                    "outbox" -> "Chiquvchi Hujjatlar"
                    "draft" -> "Qoralama Hujjatlar"
                    "cancel" -> "Bekor Qilingan"
                    else -> "$document_type"
                }
                return false
            }

        })




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation = AnimationUtils.loadAnimation(context, R.anim.animation_card)
        view.startAnimation(animation)
    }

    override fun onItemClick(position: Int) {
        // Toast.makeText(this, "Item ${position + 1} clicked", Toast.LENGTH_SHORT).show()
        var clickedItem: ExamleItem = exampleList1!![position]
        progressBar?.visibility = View.VISIBLE
        RequsetWithDocument(access_token, clickedItem.txtPdf_file_name)
    }
    private fun RequsetWithDocument(
        access_token: String?,
        pdf_file_name: String?
    ) {
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, ConnectURL().DOCUMENTURL,
            Response.Listener { response ->

                try {
                    val jsonObject = JSONObject(response)
                    val jsonDocuments = jsonObject.getJSONObject("document")
                    var pdf64 = jsonDocuments.getString("pdf")
                    val base64 = jsonDocuments.getString("base64")
                    var reaction_show = jsonDocuments.optString("reaction_show")
                    var reaction_status = jsonDocuments.optString("reaction_status")
                    var id = jsonDocuments.optString("id")
                    var action_type_id = jsonDocuments.optString("action_type_id")
                    var status = jsonDocuments.optInt("status")
                    var document_signers = jsonDocuments.getJSONArray("document_signers")
                    var document_files = jsonObject.getJSONArray("document_files")

                    var firstname_uz_latin: String? = null
                    var assignment: String? = null
                    var due_at: String? = null
                    var resolution_fio: String? = null
                    var resolution_show:Boolean=false

                    if(jsonDocuments.has("resolution_show"))
                    {
                        resolution_show=jsonDocuments.optBoolean("resolution_show")
                    }

                    if (jsonDocuments.has("resolution")) {
                        var resolution = jsonDocuments.getJSONObject("resolution")
                        firstname_uz_latin = resolution.getJSONObject("parent_employee").optString("firstname_uz_latin")
                        firstname_uz_latin = firstname_uz_latin + " " + resolution.getJSONObject("parent_employee").optString("lastname_uz_latin") + "...             Topshiriq"
                        assignment = resolution.optString("assignment")
                        due_at = resolution.optString("due_at")
                        resolution_fio = resolution.optString("fio")
                    }

                    var commentsId=""

                    for (i in 0 until document_signers.length()) {
                        if (document_signers.getJSONObject(i).optString("action_type_id").equals("6") == true) {
                            tuzuvch_fio = document_signers.getJSONObject(i).optString("fio")
                        }

                        var staff_id=document_signers.getJSONObject(i).optString("staff_id")
                        var signer_employee_id=document_signers.getJSONObject(i).optString("signer_employee_id")

                        if (employee_id.equals(staff_id)==true or employee_id.equals(signer_employee_id)==true){
                            izohlarButtonStatus=true
                        } else{
                            izohlarButtonStatus=false
                        }

                        if(employee_id.equals(signer_employee_id)==true){
                            commentsId=document_signers.getJSONObject(i).optString("id")
                        }
                    }

                    if (pdf64 != null) {
//                        val intent = Intent(context, MainActivity::class.java)
//                        intent.putExtra("pdf64", "$pdf64")
//                        intent.putExtra("base64", "$base64")
//                        intent.putExtra("reaction_show", "$reaction_show")
//                        intent.putExtra("reaction_status", "$reaction_status")
//                        intent.putExtra("action_type_id", "$action_type_id")
//                        intent.putExtra("id", "$id")
//                        intent.putExtra("access_token", "$access_token")
//                        intent.putExtra("documentStatus", "$status")
//                        intent.putExtra("firstname_uz_latin", "$firstname_uz_latin")
//                        intent.putExtra("assignment", "$assignment")
//                        intent.putExtra("due_at", "$due_at")
//                        intent.putExtra("resolution_fio", "$resolution_fio")
//                        intent.putExtra("izohlarButtonStatus", "$izohlarButtonStatus")
//                        intent.putExtra("commentsId", "$commentsId")
//                        intent.putExtra("employee_id", "$employee_id")
//                        intent.putExtra("resolution_show", "$resolution_show")
//                        startActivity(intent)


                        val viewFragment = ViewFragment()
                        val bundle = Bundle()
                        bundle.putString("pdf64", "$pdf64")
                        bundle.putString("base64", "$base64")
                        bundle.putString("reaction_show", "$reaction_show")
                        bundle.putString("reaction_status", "$reaction_status")
                        bundle.putString("action_type_id", "$action_type_id")
                        bundle.putString("id", "$id")
                        bundle.putString("access_token", "$access_token")
                        bundle.putString("tuzuvch_fio", "$tuzuvch_fio")
                        bundle.putInt("documentStatus", status)
                        bundle.putString("firstname_uz_latin", "$firstname_uz_latin")
                        bundle.putString("assignment", "$assignment")
                        bundle.putString("due_at", "$due_at")
                        bundle.putString("resolution_fio", "$resolution_fio")
                        bundle.putString("izohlarButtonStatus", "$izohlarButtonStatus")
                        bundle.putString("commentsId", "$commentsId")
                        bundle.putString("employee_id", "$employee_id")
                        bundle.putBoolean("resolution_show", resolution_show)
                        bundle.putString("document_files", "${document_files.toString()}")

                        viewFragment.arguments = bundle
                        val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
                        transaction?.addToBackStack(null)?.replace(R.id.fragment, viewFragment)?.commit()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
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
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
        requestQueue.addRequestFinishedListener<Any> { requestQueue.cache.clear() }
    }

   public fun RequsetWithFilter(
        token_type: String?,
        access_token: String?,
        document_type: String?,
        page: Int, itemsPerPage: Int
    ) {

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, ConnectURL().FILTERURL,
            Response.Listener { response ->


                try {
                    val jsonObject = JSONObject(response)
                    val jsonDocuments = jsonObject.getJSONObject("documents")
                    activity?.runOnUiThread {
                        from = jsonDocuments.optInt("from")
                        last_page = jsonDocuments.optInt("last_page")
                        to = jsonDocuments.optInt("to")
                        total = jsonDocuments.optInt("total")

                        txtBrinch?.text = "$from - $to of $total"
                    }
                    val dataJsonArray = jsonDocuments.optJSONArray("data")
                    val exampleList: List<ExamleItem> = generateDummyList(dataJsonArray.length(), dataJsonArray)
                    exampleList1 = exampleList
                    recycler_view?.adapter = ExampleAdapter(exampleList, this)
                    recycler_view?.layoutManager = LinearLayoutManager(context)
                    recycler_view?.setHasFixedSize(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }) {

            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun getBody(): ByteArray? {
                var params: MutableMap<String, String> = HashMap()
                val params2: MutableMap<String, String> = HashMap()
                val params3: MutableMap<String, String> = HashMap()

                //Change with your post params
                params2.put("attributes", "[]")
                if (content.isNullOrEmpty()==true || content!!.isBlank()==true)
                    params2.put("content", "\"\"")
                else
                params2.put("content", "\"$content\"")
                params2.put("created_by", "\"\"")
                params2.put("document_end_date", "\"\"")

                if (document_number.isNullOrEmpty()==true || document_number!!.isBlank()==true)
                    params2.put("document_number", "\"\"")
                else
                    params2.put("document_number", "\"$document_number\"")
                params2.put("document_start_date", "\"\"")
                params2.put("document_template_id", "0")
                params2.put("document_type_id", "0")
                params2.put("menu_item", "\"$document_type\"")
                params2.put("pending_action", "\"\"")
                params2.put("reaction_status", "[0, 1, 2, 3, 4]")
                params2.put("receive_by", "\"\"")
                params2.put("send_by", "\"\"")
                params.put("\"filter\"", params2.toString())
                params.put("\"language\"", "\"uz_latin\"")
                params3.put("groupBy", "[]")
                params3.put("groupDesc", "[]")
                params3.put("itemsPerPage", "$itemsPerPage")
                params3.put("multiSort", "false")
                params3.put("mustSort", "false")
                params3.put("page", "$page")
                params3.put("sortBy", "[]")
                params3.put("sortDesc", "[]")
                params.put("\"pagination\"", params3.toString())

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
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
        requestQueue.addRequestFinishedListener<Any> { requestQueue.cache.clear() }
    }
    private fun generateDummyList(size: Int, dataJsonArray: JSONArray): List<ExamleItem> {

        val list = ArrayList<ExamleItem>()

        for (i in 0 until size) {
            var jsonObject = dataJsonArray.getJSONObject(i)
            var document_date = jsonObject.optString("document_date")
            var document_number = jsonObject.optString("document_number")
            var document_type = jsonObject.getJSONObject("document_type")
            var name_uz_latin = document_type.getString("name_uz_latin")
            var from_department = jsonObject.optString("from_department")
            var from_manager = jsonObject.optString("from_manager")
            var employee = jsonObject.getJSONObject("employee")
            var lastname_uz_latin = employee.getString("lastname_uz_latin")
            var firstname_uz_latin = employee.getString("firstname_uz_latin")
            var middlename_uz_latin = employee.getString("middlename_uz_latin")
            var to_department = jsonObject.optString("to_department")
            var to_manager = jsonObject.optString("to_manager")
            var documentStatus = jsonObject.optInt("status")
            var pdf_file_name = jsonObject.optString("pdf_file_name")

            var txtStatus = when (documentStatus) {
                0 -> "Yangi"
                1 -> "E'lon qiliish"
                2 -> "Qayta ishlash"
                3 -> "Imzolandi"
                4 -> "Bajarildi"
                5 -> "Yakunlandi"
                6 -> "Bekor qilindi"
                else -> ""
            }

            var document_signers = jsonObject.getJSONArray("document_signers")

            var status: Int
            var data_lastname_uz_latin = ""

            for (ii in 0 until document_signers.length()) {
                status = document_signers.getJSONObject(ii).getInt("status")
                var taken_at: String? = document_signers.getJSONObject(ii).getString("taken_at")


                if ((status == 0 || status == 3 || status == 4) && !taken_at.isNullOrEmpty()) {
                    var signer_employee_id: String? = document_signers.getJSONObject(ii).get("signer_employee_id").toString()


                    if (signer_employee_id?.equals("null") == true) {
                        var employee_staffs = document_signers.getJSONObject(ii).getJSONObject("employee_staffs")
                        var employee = employee_staffs.getJSONObject("employee")
                        lastname_uz_latin = employee.getString("lastname_uz_latin")
                        firstname_uz_latin = employee.getString("firstname_uz_latin")
                        middlename_uz_latin = employee.getString("middlename_uz_latin")
                    } else {

                        if (document_signers.getJSONObject(ii).has("signer_employee")) {
                            var signer_employeeJSON = document_signers.getJSONObject(ii).getJSONObject("signer_employee")
                            lastname_uz_latin = signer_employeeJSON.getString("lastname_uz_latin")
                            firstname_uz_latin = signer_employeeJSON.getString("firstname_uz_latin")
                            middlename_uz_latin = signer_employeeJSON.getString("middlename_uz_latin")
                        }
                    }

                    lastname_uz_latin = "$lastname_uz_latin ${
                        firstname_uz_latin.substring(
                            0,
                            1
                        )
                    }.${middlename_uz_latin.substring(0, 1)}"

                    data_lastname_uz_latin = data_lastname_uz_latin + lastname_uz_latin + "\n"
                }
//                else {
//                    var employee_staffs =
//                        document_signers.getJSONObject(ii).getJSONObject("employee_staffs")
//                    var employee = employee_staffs.getJSONObject("employee")
//                    var lastname_uz_latin = employee.getString("lastname_uz_latin")
//                    var firstname_uz_latin = employee.getString("firstname_uz_latin")
//                    var middlename_uz_latin = employee.getString("middlename_uz_latin")
//                    lastname_uz_latin = "$lastname_uz_latin ${
//                        firstname_uz_latin.substring(
//                            0,
//                            1
//                        )
//                    }.${middlename_uz_latin.substring(0, 1)}"
//                    data_lastname_uz_latin = data_lastname_uz_latin + lastname_uz_latin + "\n"
//                }


            }

            val item = ExamleItem(
                "$document_number",
                "$document_date",
                "$name_uz_latin",
                "$lastname_uz_latin",
                "$from_department\n$from_manager",
                "$to_department\n$to_manager",
                "$data_lastname_uz_latin",
                "$txtStatus",
                "$pdf_file_name"
            )
            list += item
        }
        return list
    }

    override fun onClick(view: View?) {

        when (view?.id) {

            ivTopID.id -> {
                page = 1
                RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)

                ivBottomID.isEnabled = true
                ivRightID.isEnabled = true
                ivTopID.isEnabled = false
                ivBackID.isEnabled = false
            }


            ivBackID.id -> {

                if (page == from) {
                    ivTopID.isEnabled = false
                    ivBackID.isEnabled = false
                    Toast.makeText(activity, "Boshlanishiha keldi", Toast.LENGTH_SHORT).show()
                } else {
                    page--
                    ivTopID.isEnabled = true
                    ivBackID.isEnabled = true
                    RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)
                    Toast.makeText(activity, "Orqaga  varoqlash", Toast.LENGTH_SHORT).show()
                }

                ivBottomID.isEnabled = true
                ivRightID.isEnabled = true
            }

            ivRightID.id -> {

                if (page == last_page) {
                    ivRightID.isEnabled = false
                    ivBottomID.isEnabled = false
                    Toast.makeText(activity, "Oxiriga keldi", Toast.LENGTH_SHORT).show()
                } else {
                    page++
                    ivRightID.isEnabled = true
                    ivBottomID.isEnabled = true
                    RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)
                    Toast.makeText(activity, "Oldinga varoqlasj", Toast.LENGTH_SHORT).show()
                }

                ivTopID.isEnabled = true
                ivBackID.isEnabled = true
            }


            ivBottomID.id -> {

                if (page < last_page) {
                    page = last_page
                    RequsetWithFilter(token_type, access_token, document_type, page, itemsPerPage)
                }

                Toast.makeText(activity, "Ohiriga keldi", Toast.LENGTH_SHORT).show()
                ivBottomID.isEnabled = false
                ivTopID.isEnabled = true
                ivBackID.isEnabled = true
                ivRightID.isEnabled = false
            }
        }
    }

}