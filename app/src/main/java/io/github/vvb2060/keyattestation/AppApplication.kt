package io.github.vvb2060.keyattestation

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import org.bouncycastle.jce.provider.BouncyCastleProvider
import rikka.html.text.HtmlCompat
import rikka.material.app.DayNightDelegate
import java.security.Security
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AppApplication : Application() {
    companion object {
        const val TAG = "KeyAttestation"
        lateinit var app: AppApplication
        val mainHandler: Handler = Handler(Looper.getMainLooper())
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
    }

    init {
        app = this
        DayNightDelegate.setApplicationContext(this)
        DayNightDelegate.setDefaultNightMode(DayNightDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        HtmlCompat.setContext(this)
        installProvider(this)
    }

    private fun installProvider(context: Context) {
        if (BuildConfig.DEBUG) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
            Security.insertProviderAt(BouncyCastleProvider(), 1)
        } else runCatching {
            context.packageManager.getApplicationInfo("com.google.android.gms",
                    PackageManager.MATCH_SYSTEM_ONLY)
            val gms = context.createPackageContext("com.google.android.gms",
                    Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            gms.classLoader
                    .loadClass("com.google.android.gms.common.security.ProviderInstallerImpl")
                    .getMethod("insertProvider", Context::class.java)
                    .invoke(null, gms)
        }
    }
}
