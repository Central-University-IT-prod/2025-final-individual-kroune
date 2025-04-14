package io.github.kroune.superfinancer

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.arkivanov.decompose.retainedComponent
import io.github.kroune.superfinancer.data.database.ApplicationDatabase
import io.github.kroune.superfinancer.data.json.DefaultJsonProvider
import io.github.kroune.superfinancer.data.json.DefaultJsonProviderI
import io.github.kroune.superfinancer.di.koinModule
import io.github.kroune.superfinancer.navigation.RootComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

private val executeOnce = lazy {
    startKoin {
        modules(koinModule)
    }
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        "securedPreferences",
        masterKeyAlias,
        applicationContextValue,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    with(GlobalContext.get()) {
        declare<DefaultJsonProviderI>(DefaultJsonProvider(applicationContextValue))
        declare(
            Room.databaseBuilder(
                applicationContextValue,
                ApplicationDatabase::class.java, "database-name"
            ).build()
        )
        declare(sharedPreferences)
    }
}

private lateinit var applicationContextValue: Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationContextValue = applicationContext
        executeOnce.value
        val component = retainedComponent {
            RootComponent(it)
        }
        setContent {
            App(component)
        }
    }
}
