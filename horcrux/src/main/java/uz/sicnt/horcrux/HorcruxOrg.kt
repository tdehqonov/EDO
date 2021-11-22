package uz.sicnt.horcrux

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import uz.sicnt.horcrux.Constants.*
import java.util.regex.Pattern

class HorcruxOrg(private val context: Context, private val apiKey: String) : Activity(),
    DialogInterface.OnClickListener {

    val tag: String = "HORCRUX"

    private lateinit var pkcs7: String
    public var serialNumber: CharSequence? = null
    public var signature: ByteArray? = null
    private var subject: CharSequence? = null

    private var regex = ",?(\\\\s)*([A-Za-z]+|[0-9\\\\.]+)=([^=,\\\\]*),?(\\\\s)*"
    private var p: Pattern = Pattern.compile(regex)

    /**
     * @param context      Current activity
     * @param massage      String value to generate PKCS7
     * @param serialNumber App serial number (Optional)
     */
    fun createPKCS7(
        context: Activity,
        massage: String,
        serialNumber: String?
    ) {
        val message = massage.toByteArray()
        val intent = Intent()
        intent.setClassName(
            E_IMZO_APP,
            E_IMZO_ACTIVITY
        )
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_SERIAL_NUMBER, serialNumber)
        intent.putExtra(EXTRA_PARAM_MESSAGE, message)
        context.startActivityForResult(intent, CREATE_PKCS7)
    }

    /**
     * @param context Current activity
     * @param massage String value to generate PKCS7
     */
    @JvmName("createPKCS71")
    fun createPKCS7(context: Activity, massage: String, serialNumber:String) {
        //val serialNumber = ""
        val message = massage.toByteArray()
        val intent = Intent()
        intent.setClassName(
            E_IMZO_APP,
            E_IMZO_ACTIVITY
        )
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_SERIAL_NUMBER, serialNumber)
        intent.putExtra(EXTRA_PARAM_MESSAGE, message)
        context.startActivityForResult(intent, CREATE_PKCS7)

    }

    /**
     * @param context Current activity
     */
    fun appendPkcs7(context: Activity) {

        val pkcs7: ByteArray = Base64.decode(getPKCS(), Base64.NO_WRAP)
        val intent = Intent()
        intent.setClassName(
            E_IMZO_APP,
            E_IMZO_ACTIVITY
        )
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_SERIAL_NUMBER, getSerialNumber())
        intent.putExtra(EXTRA_PARAM_APPEND_PKCS7, pkcs7)
        context.startActivityForResult(intent, APPEND_CODE)
    }

    /**
     * @param context Current activity
     * @param pkcs7String String value to generate PKCS7
     */
    fun appendPkcs7(context: Activity, pkcs7String: String) {
        val pkcs7: ByteArray = Base64.decode(pkcs7String, Base64.NO_WRAP)
        val intent = Intent()
        intent.setClassName(
            E_IMZO_APP,
            E_IMZO_ACTIVITY
        )
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_SERIAL_NUMBER, getSerialNumber())
        intent.putExtra(EXTRA_PARAM_APPEND_PKCS7, pkcs7)
        context.startActivityForResult(intent, APPEND_CODE)
    }

    /**
     * @param context Current activity
     * @param pkcs7String String value to generate PKCS7
     * @param serialNumber String value of serialNumber
     */
    fun appendPkcs7(context: Activity, pkcs7String: String, serialNumber: String) {
        val pkcs7: ByteArray = Base64.decode(pkcs7String, Base64.NO_WRAP)
        val intent = Intent()
        intent.setClassName(
            E_IMZO_APP,
            E_IMZO_ACTIVITY
        )
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_SERIAL_NUMBER, serialNumber)
        intent.putExtra(EXTRA_PARAM_APPEND_PKCS7, pkcs7)
        context.startActivityForResult(intent, APPEND_CODE)
    }

    /**
     * @param context Current activity
     * @param timeStamp String value timestamp
     */
    fun attachPkcs7(
        context: Activity,
        timeStamp: String
    ) {
        val pkcs7: ByteArray = Base64.decode(getPKCS(), Base64.NO_WRAP)
        val tst: ByteArray = Base64.decode(timeStamp, Base64.NO_WRAP)
        val intent = Intent()
        intent.setClassName(E_IMZO_APP, E_IMZO_ACTIVITY)
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_ATTACH_SERIAL_NUMBER, getSerialNumber())
        intent.putExtra(EXTRA_PARAM_ATTACH_PKCS7, pkcs7)
        intent.putExtra(EXTRA_PARAM_ATTACH_TST, tst)
        context.startActivityForResult(intent, ATTACH_CODE)
    }

    /**
     * @param context Current activity
     * @param pkcs7String String value PKSC7
     * @param timeStamp String value timestamp
     */
    fun attachPkcs7(
        context: Activity,
        pkcs7String: String,
        timeStamp: String
    ) {
        val pkcs7: ByteArray = Base64.decode(pkcs7String, Base64.NO_WRAP)
        val tst: ByteArray = Base64.decode(timeStamp, Base64.NO_WRAP)
        val intent = Intent()
        intent.setClassName(E_IMZO_APP, E_IMZO_ACTIVITY)
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_ATTACH_SERIAL_NUMBER, getSerialNumber())
        intent.putExtra(EXTRA_PARAM_ATTACH_PKCS7, pkcs7)
        intent.putExtra(EXTRA_PARAM_ATTACH_TST, tst)
        context.startActivityForResult(intent, ATTACH_CODE)
    }

    /**
     * @param context Current activity
     * @param pkcs7String String value PKSC7
     * @param serialNumber String value serial numbe
     * @param timeStamp String value timestamp
     */
    fun attachPkcs7(
        context: Activity,
        pkcs7String: String,
        serialNumber: String,
        timeStamp: String
    ) {
        val pkcs7: ByteArray = Base64.decode(pkcs7String, Base64.NO_WRAP)
        val tst: ByteArray = Base64.decode(timeStamp, Base64.NO_WRAP)
        val intent = Intent()
        intent.setClassName(E_IMZO_APP, E_IMZO_ACTIVITY)
        intent.putExtra(EXTRA_PARAM_API_KEY, apiKey)
        intent.putExtra(EXTRA_PARAM_ATTACH_SERIAL_NUMBER, serialNumber)
        intent.putExtra(EXTRA_PARAM_ATTACH_PKCS7, pkcs7)
        intent.putExtra(EXTRA_PARAM_ATTACH_TST, tst)
        context.startActivityForResult(intent, ATTACH_CODE)
    }

    /**
     * Parse PFX file
     */
    fun parsePFX(data: Intent?) {
        pkcs7 = Base64.encodeToString(
            data!!.getByteArrayExtra(EXTRA_RESULT_PKCS7),
            Base64.NO_WRAP
        )
        serialNumber =
            data.getCharSequenceExtra(EXTRA_RESULT_SERIAL_NUMBER)
        signature =
            data.getByteArrayExtra(EXTRA_RESULT_SIGNATURE)
        subject =
            data.getCharSequenceExtra(EXTRA_RESULT_SUBJECT_NAME)

//        var EXTRA_PARAM_ATTACH_TST1:String =data.getCharSequenceExtra(EXTRA_PARAM_ATTACH_TST).toString()
 //       var EXTRA_PARAM_ATTACH_PKCS71:String =data.getCharSequenceExtra(EXTRA_PARAM_ATTACH_PKCS7).toString()
//        var EXTRA_PARAM_ATTACH_SERIAL_NUMBER1:String =data.getCharSequenceExtra(EXTRA_PARAM_ATTACH_SERIAL_NUMBER).toString()
//        var EXTRA_PARAM_APPEND_PKCS71:String =data.getCharSequenceExtra(EXTRA_PARAM_APPEND_PKCS7).toString()
//        var EXTRA_PARAM_MESSAGE1:String =data.getCharSequenceExtra(EXTRA_PARAM_MESSAGE).toString()
//        var EXTRA_PARAM_API_KEY1:String=data.getCharSequenceExtra(EXTRA_PARAM_API_KEY).toString()
//        var EXTRA_RESULT_PKCS71:String =data.getCharSequenceExtra(EXTRA_RESULT_PKCS7)!!.toString()
//        var RESULT_DOC_HASH1:String =data.getCharSequenceExtra(RESULT_DOC_HASH).toString()


 //       println(EXTRA_PARAM_ATTACH_TST1+EXTRA_PARAM_ATTACH_SERIAL_NUMBER1+EXTRA_PARAM_APPEND_PKCS71+EXTRA_PARAM_MESSAGE1+EXTRA_PARAM_API_KEY1+EXTRA_RESULT_PKCS71+RESULT_DOC_HASH1)


//        println("serialNumber "+serialNumber.toString())
//        println("signature "+signature.toString())
//        println("subject "+subject.toString())
//        println("pkcs7 "+pkcs7.toString())

        println("data:"+data.toString())

        writeString(EXTRA_RESULT_PKCS7, pkcs7)
        writeString(EXTRA_RESULT_SERIAL_NUMBER, serialNumber as String)
        writeString(EXTRA_RESULT_SUBJECT_NAME, subject as String)
    }

    /**
     * @return Check if user is Legal
     */
    fun isLegal(): Boolean {
        val subject = readString(EXTRA_RESULT_SUBJECT_NAME)
        return subject.contains(YUR_TIN)
    }

    /**
     * @return Check if user is Individual
     */
    fun isIndividual(): Boolean {
        val subject = readString(EXTRA_RESULT_SUBJECT_NAME)
        return !subject.contains(YUR_TIN) && subject.contains(FIZ_TIN)
    }

    /**
     * @return User tin
     */
    fun getTin(): String {
        val subject = readString(EXTRA_RESULT_SUBJECT_NAME)
        val m = p.matcher(subject as CharSequence)
        while (m.find()) {
            if (m.group().contains(YUR_TIN)) {
                val yurTin = m.group().split("#").toTypedArray()
                return decodeHex(yurTin[1]).trim()
            } else if (m.group().contains(FIZ_TIN)) {
                val fizTin = m.group().split("=").toTypedArray()
                return fizTin[1].replace(",", "").trim()
            }
        }
        return ""
    }

    /**
     * @return PKCS7
     */
    fun getPKCS(): String {
        return readString(EXTRA_RESULT_PKCS7)
    }

    /**
     * @return Serial number
     */
    fun getSerialNumber(): String {
        return readString(EXTRA_RESULT_SERIAL_NUMBER)
    }

    /**
     * @return Subject name
     */
    fun getSubjectName(): String {
        return readString(EXTRA_RESULT_SUBJECT_NAME)
    }

    /**
     * @return Check if E-imzo app installed
     */
    fun isEImzoInstalled(): Boolean {
        val packageManager = context.packageManager
        var found = true
        try {
            packageManager.getPackageInfo(E_IMZO_APP, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            found = false
        }
        return found
    }

    /**
     * Show installation dialog
     */
    fun showInstallDialog(activity: Activity) {
        AlertDialog.Builder(activity, R.style.exitDialog)
            .setMessage(R.string.eimzo_not_install)
            .setCancelable(false)
            .setPositiveButton(R.string.yes, this)
            .setNegativeButton(R.string.no, this).create().show()
    }

    /**
     * @param hex String value
     * @return Decoded byte array
     */
    private fun decodeHex(hex: String): String {
        val sb = StringBuilder()
        val hexData = hex.toCharArray()
        var count = 0
        while (count < hexData.size - 1) {
            val firstDigit = Character.digit(hexData[count], 16)
            val lastDigit = Character.digit(hexData[count + 1], 16)
            val decimal = firstDigit * 16 + lastDigit
            sb.append(decimal.toChar())
            count += 2
        }
        return sb.toString().trim { it <= ' ' }
    }

    /**
     * Save temp data   FIXME
     */
    private fun writeString(key: String, property: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, property)
        editor.apply()
    }

    /**
     * Read temp data   FIXME
     */
    private fun readString(key: String?): String {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, "")!!
    }

    /**
     * @param bytes Bytes to
     * @return String
     */
    fun toHexString(bytes: ByteArray?): String {
        val hexString = java.lang.StringBuilder()
        if (bytes != null) {
            for (aByte in bytes) {
                val hex = Integer.toHexString(0xFF and aByte.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            return hexString.toString()
        }
        return ""
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$E_IMZO_APP")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$E_IMZO_APP")
                    )
                )
            }
            DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
        }
    }
}