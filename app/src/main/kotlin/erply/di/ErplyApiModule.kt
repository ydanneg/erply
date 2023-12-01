package erply.di

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ErplyApiModule {

    @Provides
    @Singleton
    fun providesErplyApi(): ErplyApi = ErplyApi(onLog = {
        Log.d("HTTP", it)
    })
}
