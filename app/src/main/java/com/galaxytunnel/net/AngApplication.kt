package com.galaxytunnel.net

import android.content.Context
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.WorkManager
import com.tencent.mmkv.MMKV
import com.galaxytunnel.net.AppConfig.ANG_PACKAGE
import com.galaxytunnel.net.handler.SettingsManager
import com.galaxytunnel.net.util.MmkvManager

class AngApplication : MultiDexApplication() {
    companion object {
        lateinit var application: AngApplication
    }

    /**
     * Attaches the base context to the application.
     * @param base The base context.
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    private val workManagerConfiguration: Configuration = Configuration.Builder()
        .setDefaultProcessName("${ANG_PACKAGE}:bg")
        .build()

    /**
     * Initializes the application.
     */
    override fun onCreate() {
        super.onCreate()

        MMKV.initialize(this)

        // Add default servers on first launch
        addDefaultServers()

        // Initialize WorkManager with the custom configuration
        WorkManager.initialize(this, workManagerConfiguration)

        // Ensure critical preference defaults are present in MMKV early
        SettingsManager.initApp(this)
        SettingsManager.setNightMode()

        es.dmoral.toasty.Toasty.Config.getInstance()
            .setGravity(android.view.Gravity.BOTTOM, 0, 300)
            .apply()
    }

    private fun addDefaultServers() {
        val mmkv = MMKV.defaultMMKV()
        val isFirstRun = mmkv.decodeBool("is_first_run_for_servers_v2", true)

        if (isFirstRun) {
            val serverUris = listOf(
                "vless://19658493-1494-4766-994d-eb2801088064@galaxy.keyjansama.workers.dev:443?path=ed%3D%2F9000&security=tls&encryption=none&host=galaxy.keyjansama.workers.dev&fp=chrome&type=ws&sni=galaxy.keyjansama.workers.dev#Galaxy-Tunnelr%F0%9F%9A%80",
                "vless://7777489c-9d5f-407d-81e9-3467cff92134@galaxy-5.gaxlayplanet.workers.dev:443?path=ed%3D%2F2680&security=tls&alpn=h3%2Ch2%2Chttp%2F1.1&encryption=none&host=galaxy-5.gaxlayplanet.workers.dev&fp=random&type=ws&sni=galaxy-5.gaxlayplanet.workers.dev#Galaxy-Tunnel",
                "vless://8221a740-8218-4775-ab45-0bab948285ec@sub.galaxytunnel2026.workers.dev:443?security=tls&encryption=none&host=sub.galaxytunnel2026.workers.dev&type=ws&sni=sub.galaxytunnel2026.workers.dev#Galaxy-Tunnel",
                "vless://26fe5cdd-e772-4238-8adc-9bf53d4781fa@coca.nobless.workers.dev:443?path=%2F&security=tls&encryption=none&host=coca.nobless.workers.dev&type=ws&sni=coca.nobless.workers.dev#Galaxy-Tunnel",
                "trojan://5a733fcb-f724-45d5-9f6f-9cd96d812409@clone.yatokami.workers.dev:443?path=%2F&security=tls&alpn=h3%2Ch2%2Chttp%2F1.1&host=clone.yatokami.workers.dev&fp=chrome&type=ws&sni=clone.yatokami.workers.dev#clone",
                "trojan://18616960-5953-490c-a717-5462c9c63517@galaxy-2.pages.dev:443?path=%2F&security=tls&host=galaxy-2.pages.dev&fp=random&type=ws&sni=galaxy-2.pages.dev#Galaxy-Tunnel%E2%9A%A1",
                "trojan://3413d540-942c-4763-ad39-3854a3621a2e@galaxy-3.z-empire.workers.dev:443?path=%2F&security=tls&alpn=h3&host=galaxy-3.z-empire.workers.dev&type=ws&sni=galaxy-3.z-empire.workers.dev#Galaxy-Tunnel%2FTrojan%F0%9F%86%99"
            )
            val servers = serverUris.joinToString("\n")
            MmkvManager.importBatchConfig(servers)
            mmkv.encode("is_first_run_for_servers_v2", false)
        }
    }
}
