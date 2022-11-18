package com.example.shop.ui.main

import androidx.lifecycle.ViewModel
import com.example.shop.data.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ShopRepository
): ViewModel() {
    suspend fun logOut() {
        repository.logOut()
    }
}