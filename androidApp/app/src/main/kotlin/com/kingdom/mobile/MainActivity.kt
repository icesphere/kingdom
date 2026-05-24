package com.kingdom.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kingdom.mobile.design.KingdomTheme
import com.kingdom.mobile.network.KingdomApiClient
import com.kingdom.mobile.repository.KingdomRepository
import com.kingdom.mobile.state.GameLayoutPreferenceStore
import com.kingdom.mobile.state.KingdomViewModel
import com.kingdom.mobile.state.SharedPreferencesGameLayoutPreferenceStore
import com.kingdom.mobile.ui.KingdomApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = KingdomRepository(KingdomApiClient(DEFAULT_BASE_URL))
        val gameLayoutPreferenceStore = SharedPreferencesGameLayoutPreferenceStore(
            getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        )
        val factory = KingdomViewModelFactory(repository, gameLayoutPreferenceStore)
        setContent {
            KingdomTheme {
                val viewModel: KingdomViewModel = viewModel(factory = factory)
                KingdomApp(viewModel)
            }
        }
    }

    private companion object {
        const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"
        const val PREFERENCES_NAME = "kingdom_preferences"
    }
}

class KingdomViewModelFactory(
    private val repository: KingdomRepository,
    private val gameLayoutPreferenceStore: GameLayoutPreferenceStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KingdomViewModel::class.java)) {
            return KingdomViewModel(repository, gameLayoutPreferenceStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
