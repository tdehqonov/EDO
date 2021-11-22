package uz.sicnt.eimzo.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.anychart.data.Set
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import kotlinx.android.synthetic.main.activity_dashboard_new.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import uz.sicnt.eimzo.ConnectURL
import uz.sicnt.eimzo.Dashboard
import uz.sicnt.eimzo.R
import java.io.IOException


var tv_count_inbox_int = 2
var tv_count_outbox_int = 1
var tv_count_draft_int = 2
var tv_count_cancel_int = 1

lateinit var anychart: AnyChartView
lateinit var pie: Pie

private var set: Set? = null

class HomeFragment : Fragment(), View.OnClickListener {

    var tv_count_inbox: TextView? = null
    var tv_new_count_inbox: TextView? = null
    var tv_count_outbox: TextView? = null
    var tv_new_count_outbox: TextView? = null
    var tv_count_draft: TextView? = null
    var tv_new_count_draft: TextView? = null
    var tv_count_cancel: TextView? = null
    var tv_new_count_cancel: TextView? = null
    var swipe_refresh_layout: SwipeRefreshLayout? = null

    var cvKiruvchiHujjatlar: CardView? = null
    var cvChiquvchiHujjatlar: CardView? = null
    var cvQoralamaHujjatlar: CardView? = null
    var cvBekorQilinganHujjatlar: CardView? = null
    var linearpie: LinearLayout? = null

    var token_type: String? = null
    var access_token: String? = null

    var tabel: String? = null
    var jsonObjectResponse: JSONObject? = null

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RequestWithListHttps()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_dashboard_new, container, false)
        return view
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        anychart= view.any_chart_view
        swipe_refresh_layout = view.swipe_refresh

        var sharedPreference: SharedPreferences =
            activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!

        token_type = sharedPreference.getString("token_type", "")
        access_token = sharedPreference.getString("access_token", "")
        tabel = sharedPreference.getString("tabel", "")

        tv_count_inbox = view.tv_count_inbox
        tv_new_count_inbox = view.tv_new_count_inbox
        tv_count_outbox = view.tv_count_outbox
        tv_new_count_outbox = view.tv_new_count_outbox
        tv_count_draft = view.tv_count_draft
        tv_new_count_draft = view.tv_new_count_draft
        tv_count_cancel = view.tv_count_cancel
        tv_new_count_cancel = view.tv_new_count_cancel
        linearpie = view.linearpie
        cvKiruvchiHujjatlar = view.cvKiruvchiHujjatlar
        cvChiquvchiHujjatlar = view.cvChiquvchiHujjatlar
        cvQoralamaHujjatlar = view.cvQoralamaHujjatlar
        cvBekorQilinganHujjatlar = view.cvBekorQilinganHujjatlar

        cvKiruvchiHujjatlar?.setOnClickListener { ToDocFragment("Kiruvchi hujjatlar", "inbox") }
        cvChiquvchiHujjatlar?.setOnClickListener { ToDocFragment("CHiquvchi hujjatlar", "outbox") }
        cvQoralamaHujjatlar?.setOnClickListener { ToDocFragment("Qoralama hujjatlar", "draft") }
        cvBekorQilinganHujjatlar?.setOnClickListener { ToDocFragment("Bekor qilingan hujjatlar", "cancel") }

        RequestWithListHttps()

        APIlib.getInstance().setActiveAnyChartView(anychart)
        var pie = AnyChart.pie()

        pie.legend()
            .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
            .align(Align.CENTER)
        var istablet: Boolean? = null

        istablet = isTablet(activity as Context)

//       if (istablet == true){
            linearpie?.layoutParams?.height = 1000
            linearpie?.layoutParams?.width = 1200
            linearpie?.requestLayout()
//        }

        anychart?.setChart(pie)
        var data_entry = ArrayList<DataEntry>()
//
        data_entry?.add(ValueDataEntry("Kiruvchi hujjatlar", tv_count_inbox_int))
        data_entry?.add(ValueDataEntry("Chiquvchi hujjatlar", tv_count_outbox_int))
        data_entry?.add(ValueDataEntry("Qoralamalar", tv_count_draft_int))
        data_entry?.add(ValueDataEntry("Bekor qilingan hujjatlar", tv_count_cancel_int))
        pie.data(data_entry)

        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                data_entry.clear()
                data_entry?.add(ValueDataEntry("Kiruvchi hujjatlar", tv_count_inbox_int))
                data_entry?.add(ValueDataEntry("Chiquvchi hujjatlar", tv_count_outbox_int))
                data_entry?.add(ValueDataEntry("Qoralamalar", tv_count_draft_int))
                data_entry?.add(ValueDataEntry("Bekor qilingan hujjatlar", tv_count_cancel_int))
                pie.data(data_entry)
            }
        }

        handler.postDelayed(runnable, 2000)
        swipe_refresh_layout?.setOnRefreshListener{

            RequestWithListHttps()
            swipe_refresh_layout?.isRefreshing = false
            if (checkConnectivity(requireContext()))
            { Toast.makeText(context, "Yangilandi", Toast.LENGTH_SHORT).show()
            }
            else
            { Toast.makeText(context, "Internet ulanishni tekshiring!", Toast.LENGTH_SHORT).show() }

            pie.data(data_entry)
            handler.postDelayed(runnable, 1000)
            anychart?.refreshDrawableState()
            (activity as Dashboard).RequsetWithNotification(token_type, access_token)
        }
    }

    fun isTablet(ctx: Context): Boolean {
        return ctx.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun checkConnectivity(context: Context): Boolean
    {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        if(activeNetwork?.isConnected!=null){
            return activeNetwork.isConnected
        }
        else
        {
            return false
        }
    }

    fun ToDocFragment(title: String, document_type: String)
    {
        val notificationFragment = DocFragment()
        val bundle = Bundle()
        bundle.putString("token_type", token_type)
        bundle.putString("access_token", access_token)
        bundle.putString("title", title)
        bundle.putString("document_type", document_type)
        notificationFragment.arguments = bundle
        val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
        transaction?.addToBackStack(null)?.replace(R.id.fragment, notificationFragment)?.commit()
    }

    private fun RequestWithListHttps()
    {
        val URL = ConnectURL().DASHBOARD_MOBILE
        var okHttpClient: OkHttpClient = OkHttpClient()
        val request: okhttp3.Request = okhttp3.Request
            .Builder()
            .url(URL)
            .method("GET", null)
            .addHeader(
                "Authorization",
                "Bearer $access_token"
            )
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback { override fun onFailure(call: Call?, e: IOException?) { }
            override fun onResponse(call: Call?, response: okhttp3.Response?) {

                try {
                    val jsonObject = JSONObject(response?.body()?.string())
                    val jsonArray = jsonObject.optJSONArray("boxes")

                    var jsonObject0 = jsonArray.getJSONObject(0)
                    var jsonObject1 = jsonArray.getJSONObject(1)
                    var jsonObject2 = jsonArray.getJSONObject(2)
                    var jsonObject3 = jsonArray.getJSONObject(3)

                    activity?.runOnUiThread {

                        tv_count_inbox?.text = jsonObject0.optString("count")
                        tv_new_count_inbox?.text = jsonObject0.optString("new_count") + "/"
                        tv_count_inbox_int = jsonObject0.optString("count").toString().toInt()

                        tv_count_outbox?.text = jsonObject1.optString("count")
                        tv_new_count_outbox?.text = jsonObject1.optString("new_count") + "/"
                        tv_count_outbox_int = jsonObject1.optString("count").toString().toInt()

                        tv_count_draft?.text = jsonObject2.optString("count")
                        tv_new_count_draft?.text = jsonObject2.optString("new_count") + "/"
                        tv_count_draft_int = jsonObject2.optString("count").toString().toInt()

                        tv_count_cancel?.text = jsonObject3.optString("count")
                        tv_new_count_cancel?.text = jsonObject3.optString("new_count") + "/"
                        tv_count_cancel_int = jsonObject3.optString("count").toString().toInt()

                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }
        })
    }

override fun onClick(v: View?) {

    }
}