package com.example.core.network

import android.content.Context
import com.example.core.api.endpoint.ApiEndpoints
import com.example.core.security.SecurityEngine

/**
 * NetworkModule
 * مكان تجميع وربط جميع مكونات Network Engine معاً.
 * في المستقبل، يُستبدل بـ Dagger/Hilt Module.
 */
object NetworkModule {

    // Base URL لخادم Kurotek TypeScript/Node.js (server/src/) — ليس Go، انظر ADR-004 المُحدَّث
    const val BASE_URL = "https://api.kurotek.app/"

    fun provideNetworkConfig(enableLogging: Boolean = false) = NetworkConfig(
        baseUrl = BASE_URL,
        connectTimeoutSecs = 30L,
        readTimeoutSecs = 30L,
        writeTimeoutSecs = 30L,
        enableLogging = enableLogging
    )

    fun provideLogger(): Logger = LogcatLogger()
    fun provideMetrics(): MetricsHook = NoOpMetricsHook()

    fun provideNetworkMonitor(context: Context): NetworkMonitor =
        NetworkMonitorImpl(context)

    fun providePendingQueue(context: Context): PendingRequestQueue =
        PendingRequestQueueImpl(context)

    fun provideNetworkEngine(
        context: Context,
        tokenProvider: TokenProvider,
        enableLogging: Boolean = false
    ): NetworkEngineImpl {
        val config = provideNetworkConfig(enableLogging)
        val monitor = provideNetworkMonitor(context)
        monitor.startMonitoring()
        return NetworkEngineImpl(
            config = config,
            tokenProvider = tokenProvider,
            networkMonitor = monitor,
            pendingQueue = providePendingQueue(context),
            logger = provideLogger(),
            metricsHook = provideMetrics(),
            securityEngine = SecurityEngine
        )
    }

    fun provideApiEndpoints(engine: NetworkEngineImpl): ApiEndpoints =
        engine.retrofit.create(ApiEndpoints::class.java)
}
