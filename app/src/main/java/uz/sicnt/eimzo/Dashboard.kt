package uz.sicnt.eimzo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.layouy_notification.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uz.sicnt.eimzo.view.DocFragment
import uz.sicnt.eimzo.view.HomeFragment
import uz.sicnt.eimzo.view.ViewFragment
import java.util.*

var token_type: String? = null
var access_token: String? = null

var tabel: String? = null
var jsonObjectResponse: JSONObject? = null

class Dashboard : AppCompatActivity(), View.OnClickListener {

    private fun ToDocFragment(title: String, document_type: String) {

        val docFragment = DocFragment()
        val bundle = Bundle()
        bundle.putString("token_type", token_type)
        bundle.putString("access_token", access_token)
        bundle.putString("title", title)
        bundle.putString("document_type", document_type)

        docFragment.arguments = bundle
        supportFragmentManager.popBackStack()   //handling back to menu
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment, docFragment).addToBackStack(null)
        ft.commitAllowingStateLoss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_navig)

        val homebutton = findViewById<Button>(R.id.homebutton)
        val drawarLayout = findViewById<DrawerLayout>(R.id.drawarLayout)
        val imgMenu = findViewById<ImageView>(R.id.imgMenu)

        val navView = findViewById<NavigationView>(R.id.navDawar)
        navView.itemIconTintList = null

        imgMenu.setOnClickListener {
            drawarLayout.openDrawer(GravityCompat.START)
        }

        val navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupWithNavController(navView, navController)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.Kiruvchi -> {
                    // handle click
                    ToDocFragment("Kiruvchi hujjatlar", "inbox")
                    drawarLayout.closeDrawers()
                    true
                }
                R.id.Chiquvchi -> {
                    // handle click
                    ToDocFragment("CHiquvchi hujjatlar", "outbox")
                    drawarLayout.closeDrawers()
                    true
                }
                R.id.Qoralama -> {
                    // handle click
                    ToDocFragment("Qoralama hujjatlar", "draft")
                    drawarLayout.closeDrawers()
                    true
                }
                R.id.Bekor -> {
                    // handle click
                    ToDocFragment("Bekor qilingan hujjatlar", "cancel")
                    drawarLayout.closeDrawers()
                    true
                }
                R.id.Ulashish -> {
                    // handle click
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "type/palin"
                    val sharebody =
                        "Quyidagi link orqali, EDOning mobile versiyasini yuklab oling. "
                    val sharesub =
                        "https://play.google.com/store/apps/details?id=edo.uzautomotors.com"
                    intent.putExtra(Intent.EXTRA_SUBJECT, sharebody)
                    intent.putExtra(Intent.EXTRA_TEXT, sharesub)
                    startActivity(Intent.createChooser(intent, "Share your app"))
                    drawarLayout.closeDrawers()
                    true
                }
                R.id.Imzo -> {
                    // handle click
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=uz.yt.eimzo")
                    )
                    startActivity(intent)
                    drawarLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        homebutton.setOnClickListener {

            val homeFragment = HomeFragment()

            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment, homeFragment).addToBackStack(null)
            ft.commit()
        }

        //        ________________________________________________________

        var sharedPreference: SharedPreferences =
            this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!

        token_type = sharedPreference.getString("token_type", "")
        access_token = sharedPreference.getString("access_token", "")
        tabel = sharedPreference.getString("tabel", "")

        var file_id = sharedPreference.getString("file_id", "")
        if (file_id != "null") {
            RequsetWithDocument(file_id, access_token)
            sharedPreference.edit().remove("file_id")
        }

        RequsetWithNotification(token_type, access_token)

        ivNotificationTimerID.setOnClickListener(this)
        ivNotificationLightningID.setOnClickListener(this)
        ivNotificationAlertID.setOnClickListener(this)
        ivNotificationPlusID.setOnClickListener(this)
        ivNotificationChekID.setOnClickListener(this)
        ivNotificationSearchID.setOnClickListener(this)
        ivNotificationFierID.setOnClickListener(this)
        ivNotificationMailID.setOnClickListener(this)
        ivNotificationStartID.setOnClickListener(this)
        ivNotificationInfoID.setOnClickListener(this)

    }

    fun RequsetWithDocument(pdf_file_name: String?, access_token: String?) {

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

                    if (jsonDocuments.has("resolution")) {
                        var resolution = jsonDocuments.getJSONObject("resolution")
                        firstname_uz_latin = resolution.getJSONObject("parent_employee")
                            .optString("firstname_uz_latin")
                        firstname_uz_latin =
                            firstname_uz_latin + " " + resolution.getJSONObject("parent_employee")
                                .optString("lastname_uz_latin") + "...             Topshiriq"
                        assignment = resolution.optString("assignment")
                        due_at = resolution.optString("due_at")
                        resolution_fio = resolution.optString("fio")

                    }

                    if (pdf64 != null) {
                        val viewFragment = ViewFragment()
                        val bundle = Bundle()
                        bundle.putString("pdf64", "$pdf64")
                        bundle.putString("base64", "$base64")
                        bundle.putString("reaction_show", "$reaction_show")
                        bundle.putString("reaction_status", "$reaction_status")
                        bundle.putString("action_type_id", "$action_type_id")
                        bundle.putString("id", "$id")
                        bundle.putString("access_token", "$access_token")
                        bundle.putString("documentStatus", "$status")
                        bundle.putString("firstname_uz_latin", "$firstname_uz_latin")
                        bundle.putString("assignment", "$assignment")
                        bundle.putString("due_at", "$due_at")
                        bundle.putString("resolution_fio", "$resolution_fio")
                        bundle.putString("document_files", "$document_files")

                        viewFragment.arguments = bundle

                        val ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.fragment, viewFragment).addToBackStack(null)
                        ft.commit()
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
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
        requestQueue.addRequestFinishedListener<Any> { requestQueue.cache.clear() }
    }

    fun RequsetWithNotification(token_type: String?, access_token: String?) {
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, ConnectURL().NOTIFICATIONURL,
            Response.Listener { response ->
                try {
                    jsonObjectResponse = JSONObject(response)
                    var length = jsonObjectResponse?.getString("length")
                    var length_executor = jsonObjectResponse?.getString("length_executor")
                    var length_expired = jsonObjectResponse?.getString("length_expired")
                    var length_prosesing = jsonObjectResponse?.getString("length_prosesing")
                    var length_resolutions = jsonObjectResponse?.getString("length_resolutions")
                    var length_results = jsonObjectResponse?.getString("length_results")
                    var length_substantiate = jsonObjectResponse?.getString("length_substantiate")
                    var length_watcher = jsonObjectResponse?.getString("length_watcher")
                    var length_star = jsonObjectResponse?.getString("length_star")
                    var length_info = jsonObjectResponse?.getString("length_info")

                    tvFier?.text = length_expired
                    tvMail?.text = length
                    tvLightning?.text = length_executor
                    tvTimer?.text = length_prosesing
                    tvPlus?.text = length_resolutions
                    tvChek?.text = length_results
                    tvAlert?.text = length_substantiate
                    tvSearch?.text = length_watcher
                    tvStart?.text = length_star
                    tvInfo?.text = length_info

                    if (tvInfo?.text == "0") {
                        cvNotificationInfo.visibility = View.GONE
                        ivNotificationInfoID.visibility = View.GONE

                    } else {
                        cvNotificationInfo.visibility = View.VISIBLE
                        ivNotificationInfoID.visibility = View.VISIBLE
                    }

                    if (tvStart?.text == "0") {
                        cvNotificationStart.visibility = View.GONE
                        ivNotificationStartID.visibility = View.GONE

                    } else {
                        cvNotificationStart.visibility = View.VISIBLE
                        ivNotificationStartID.visibility = View.VISIBLE
                    }


                    if (tvTimer?.text == "0") {
                        cvNotificationTimer.visibility = View.GONE
                        ivNotificationTimerID.visibility = View.GONE

                    } else {
                        cvNotificationTimer.visibility = View.VISIBLE
                        ivNotificationTimerID.visibility = View.VISIBLE
                    }

                    if (tvLightning?.text == "0") {
                        cvNotificationLightning.visibility = View.GONE
                        ivNotificationLightningID.visibility = View.GONE

                    } else {
                        cvNotificationLightning.visibility = View.VISIBLE
                        ivNotificationLightningID.visibility = View.VISIBLE
                    }

                    if (tvAlert?.text == "0") {
                        cvNotificationAlert.visibility = View.GONE
                        ivNotificationAlertID.visibility = View.GONE

                    } else {
                        cvNotificationAlert.visibility = View.VISIBLE
                        ivNotificationAlertID.visibility = View.VISIBLE
                    }

                    if (tvPlus?.text == "0") {
                        cvNotificationPlus.visibility = View.GONE
                        ivNotificationPlusID.visibility = View.GONE

                    } else {
                        cvNotificationPlus.visibility = View.VISIBLE
                        ivNotificationPlusID.visibility = View.VISIBLE
                    }

                    if (tvChek?.text == "0") {
                        cvNotificationChek.visibility = View.GONE
                        ivNotificationChekID.visibility = View.GONE

                    } else {
                        cvNotificationChek.visibility = View.VISIBLE
                        ivNotificationChekID.visibility = View.VISIBLE
                    }

                    if (tvSearch?.text == "0") {
                        cvNotificationSearch.visibility = View.GONE
                        ivNotificationSearchID.visibility = View.GONE

                    } else {
                        cvNotificationSearch.visibility = View.VISIBLE
                        ivNotificationSearchID.visibility = View.VISIBLE
                    }

                    if (tvFier?.text == "0") {
                        cvNotificationFier.visibility = View.GONE
                        ivNotificationFierID.visibility = View.GONE

                    } else {
                        cvNotificationFier.visibility = View.VISIBLE
                        ivNotificationFierID.visibility = View.VISIBLE
                    }

                    if (tvMail?.text == "0") {
                        cvNotificationMile.visibility = View.GONE
                        ivNotificationMailID.visibility = View.GONE

                    } else {
                        cvNotificationMile.visibility = View.VISIBLE
                        ivNotificationMailID.visibility = View.VISIBLE
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
//                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Authorization"] = "Bearer $access_token"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
        requestQueue.addRequestFinishedListener<Any> { requestQueue.cache.clear() }
    }

    override fun onClick(v: View?) {
        var JSONArrayRespons: JSONArray? = null

        when (v?.id) {

            ivNotificationTimerID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("prosesing")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "prosesing")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationLightningID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("executor")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "executor")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationAlertID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("substantiate")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "substantiate")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationPlusID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("resolutions")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "resolutions")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationChekID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("resolution_results")
                var dialog =
                    CustomDialogFragment(JSONArrayRespons, access_token, "resolution_results")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationSearchID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("watcher")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "watcher")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationFierID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("expired")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "expired")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationMailID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("document")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "notification")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationStartID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("star")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "star")
                dialog.show(supportFragmentManager, "customDialog")
            }

            ivNotificationInfoID.id -> {
                JSONArrayRespons = jsonObjectResponse?.optJSONArray("information")
                var dialog = CustomDialogFragment(JSONArrayRespons, access_token, "information")
                dialog.show(supportFragmentManager, "customDialog")
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
