package uz.sicnt.eimzo

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login_cons.*
import org.json.JSONException
import org.json.JSONObject


class LoginActivityCons : AppCompatActivity() {

    var sharedPreference: SharedPreferences? = null
    var sharedPreferenceLogin: SharedPreferences? = null
    var LOGINURL: String? = ConnectURL().LOGINURL
    var TOKENURL: String? =ConnectURL().TOKENURL

    var access_token: String? = null
    var token_type: String? = null
    var doc_file_id:String? = null
//    var btn_login: Button? = null
//    var mPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_cons)

        var btn_login = findViewById<Button>(R.id.buttonLogin)

       var mPreferences = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        sharedPreferenceLogin = getSharedPreferences("MyUserPref", MODE_PRIVATE)

//        etUserNameCon?.setText(mPreferences?.getString("username", null))
//        etPasswordCon?.setText(mPreferences?.getString("password", null))

        var uri = intent?.data

        var path = uri.toString()
        val pos: Int? = path?.lastIndexOf("/")

        doc_file_id = path?.substring(pos!! + 1, path?.length!!)

        if (doc_file_id != "null"){
            btn_login?.performClick()
        }

        etUserNameCon?.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                tvPasswordMesage?.text = ""
            } else {
                if (etUserNameCon?.text.isNullOrEmpty())
                    tvPasswordMesage?.text = "Userni kiriting!"
            }
        }

        etPasswordCon?.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                tvPasswordMesage?.text = ""
            } else {
                if (etPasswordCon?.text.isNullOrEmpty())
                    tvPasswordMesage?.text = "Parolni kiriting!"
            }
        }

        var sharedPreference: SharedPreferences =
            this.getSharedPreferences("MyUserPref", Context.MODE_PRIVATE)!!
        var isLogin = sharedPreference.getBoolean("isLogin", false)

        if (isLogin){   
//            performAuth(sharedPreference.getString("username",""),sharedPreference.getString("password",""))
        }
        
        

        btn_login.setOnClickListener {

//            performAuth(etUserNameCon?.getText().toString().trim(),etPasswordCon?.getText().toString().trim() )
            if (etUserNameCon?.text.isNullOrEmpty() && etPasswordCon?.text.isNullOrEmpty()) {
                tvPasswordMesage?.text = "User va parolni kiriting!"
                buttonLogin.isEnabled=true
            } else {
//            buttonLogin.isEnabled=false
                val stringRequest: StringRequest = object : StringRequest(
                    Method.POST, LOGINURL,
                    Response.Listener { response ->

                        try {
                            val jsonObject = JSONObject(response)
                            token_type = jsonObject.getString("token_type")
                            access_token = jsonObject.getString("access_token")
                            if (!token_type.isNullOrEmpty()) {
                                tvPasswordMesage?.text = ""
                                RequsetWithToken(token_type!!, access_token!!)
                                var editor = sharedPreferenceLogin?.edit()
                                editor?.putBoolean("isLogin", true)
                                editor?.putString("username",etUserNameCon?.getText().toString().trim())
                                editor?.putString("password",etPasswordCon?.getText().toString().trim())
                                editor?.apply()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { error ->
                        Toast.makeText(this, "WIFI aloqani tekshiring!!!", Toast.LENGTH_LONG).show()
                        tvPasswordMesage?.text = "User yoki parol xato"
                        buttonLogin.isEnabled=true
                    }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        //Change with your post params
                        params["grant_type"] = "password"
                        params["client_id"] = "4"
                        params["client_secret"] ="9ahPAmLUe2PG3uo38HsdUwpfMxDOpU6ueDucV7XH"
                        params["username"] = etUserNameCon?.getText().toString().trim()
                        params["password"] = etPasswordCon?.getText().toString().trim()
                        return params
                    }
                }
                val requestQueue = Volley.newRequestQueue(this)
                requestQueue.add(stringRequest)
            }

    }
    }

    fun performAuth(username: String?, password: String?) {

    }

    override fun onRestart() {
        super.onRestart()
        buttonLogin.isEnabled=true
    }

    fun RequsetWithToken(token_type: String, access_token: String) {
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, TOKENURL,
            Response.Listener { response ->
//                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                try {

                    var jsonObject = JSONObject(response)
                    var username = jsonObject.getString("username")
                    var employee_id = jsonObject.getString("employee_id")
                    var email = jsonObject.getString("email")
                    val employee: JSONObject = jsonObject.getJSONObject("employee")
                    var firstname_uz_latin = employee.getString("firstname_uz_latin")
                    var lastname_uz_latin = employee.getString("lastname_uz_latin")
                    var middlename_uz_latin = employee.getString("middlename_uz_latin")
                    var born_date = employee.getString("born_date")
                    var tabel = employee.getString("tabel")
                    var employee_staff = employee.getString("employee_staff")
                    var JsonObject_employee_staff =
                        JSONObject(employee_staff.replace("[", "").replace("]", ""))
                    var JSONObject_staff: JSONObject =
                        JsonObject_employee_staff.getJSONObject("staff")
                    var JSONObject_position: JSONObject = JSONObject_staff.getJSONObject("position")
                    var name_uz_latin = JSONObject_position.getString("name_uz_latin")


                    sharedPreference =
                        getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

                    var editor = sharedPreference?.edit()

                    editor?.clear()
                    editor?.putString("token_type", token_type)
                    editor?.putString("access_token", access_token)
                    editor?.putString("username", username)
                    editor?.putString("password", etPasswordCon?.text.toString())
                    editor?.putLong("l", 100L)
                    editor?.putString("email", email)

                    editor?.putString("firstname_uz_latin", firstname_uz_latin)
                    editor?.putString("lastname_uz_latin", lastname_uz_latin)
                    editor?.putString("middlename_uz_latin", middlename_uz_latin)
                    editor?.putString("born_date", born_date)
                    editor?.putString("tabel", tabel)
                    editor?.putString("name_uz_latin", name_uz_latin)
                    editor?.putString("employee_id", employee_id)
                    editor?.putString("file_id", doc_file_id)
                    editor?.apply()

                    fun Activity.hideKeyboard(view: View) {
                        hideKeyboard(currentFocus ?: View(this))
                    }

                        val intent = Intent(this, Dashboard::class.java)
                        startActivity(intent)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                buttonLogin.isEnabled=true
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                params["Authorization"] = "Bearer $access_token"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
        requestQueue.addRequestFinishedListener<Any> { requestQueue.cache.clear() }
    }
}