package com.hongul.fliq.api

import com.hongul.fliq.BuildConfig
import com.hongul.fliq.api.card.CardRoutes
import com.hongul.fliq.api.user.UserRoutes
import retrofit2.converter.gson.GsonConverterFactory

sealed class Api {
    companion object {
        internal val retrofit by lazy {
            retrofit2.Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val card: CardRoutes by lazy { retrofit.create(CardRoutes::class.java) }
        val user: UserRoutes by lazy { retrofit.create(UserRoutes::class.java) }
    }
}

