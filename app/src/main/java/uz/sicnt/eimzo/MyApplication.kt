package uz.sicnt.eimzo

import android.app.Application
import android.content.Context
import uz.sicnt.horcrux.Horcrux


class MyApplication : Application() {
    companion object {
        lateinit var instance: Application
        lateinit var context: Context
        lateinit var horcrux: Horcrux
    }

    private val apiKey = "79DC56F42765A0017C31309DB9600EA924684ED023A8079460454768331626AB94CFFF8FC2D4007976D4A6C56F11D56DFA962276DC54AE8C0F39E8A8EBDFA10B"

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        horcrux =
            Horcrux(
                context,
                apiKey
            )
    }
}