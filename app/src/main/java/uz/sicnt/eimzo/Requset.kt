package uz.sicnt.eimzo

import okhttp3.*
import java.io.IOException

class Requset {

    public fun RequsetWithJarayonda(document_id: String?,access_token:String?) {

        val URL = ConnectURL().PROCESSINGEURL
        var okHttpClient: OkHttpClient = OkHttpClient()

        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("document_id", document_id)
            .addFormDataPart(      "comment",
                "processing"
            )
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
                //val json = response?.body()?.string()
            }
        })
    }

    fun RequsetWithIzoh(document_id: String?, access_token: String?, GLOBALCOMMENT: String) {

        val URL = ConnectURL().COMMENTURL
        var okHttpClient: OkHttpClient = OkHttpClient()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("document_id", document_id)
            .addFormDataPart(      "comment",
                "$GLOBALCOMMENT")
            .addFormDataPart("substantiate","false")
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
                //val json = response?.body()?.string()
            }
        })

    }

    fun RequsetWithAsoslabBering(document_id: String?, access_token: String?,signer_id:String?) {

        val URL = ConnectURL().COMMENTURL
        var okHttpClient: OkHttpClient = OkHttpClient()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("document_id", document_id)
            .addFormDataPart(      "comment",
                "Ушбу ҳужжат асослаб берилсин! Прошу обосновать документ!")
            .addFormDataPart(      "signer_id",
                "$signer_id")
            .addFormDataPart("substantiate","true")
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
                //val json = response?.body()?.string()
            }
        })

    }




}