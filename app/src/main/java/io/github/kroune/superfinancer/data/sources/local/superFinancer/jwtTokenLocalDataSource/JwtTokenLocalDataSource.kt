package io.github.kroune.superfinancer.data.sources.local.superFinancer.jwtTokenLocalDataSource

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class JwtTokenLocalDataSource : JwtTokenLocalDataSourceI, KoinComponent {
    private val jwtTokenKey = "jwtToken"
    private val sharedPreferences by inject<SharedPreferences>()
    private val _jwtTokenState = MutableStateFlow(loadJwtToken())
    override val jwtTokenState: StateFlow<String?>
        get() = _jwtTokenState

    override suspend fun updateJwtToken(token: String) {
        _jwtTokenState.emit(token)
        sharedPreferences.edit(commit = true) {
            putString(jwtTokenKey, token)
        }
    }

    override suspend fun removeJwtToken() {
        _jwtTokenState.emit(null)
        sharedPreferences.edit(commit = true) {
            remove(jwtTokenKey)
        }
    }

    private fun loadJwtToken(): String? {
        return sharedPreferences.getString(jwtTokenKey, null)
    }

    override fun getJwtToken(): String? {
        return _jwtTokenState.value
    }
}
