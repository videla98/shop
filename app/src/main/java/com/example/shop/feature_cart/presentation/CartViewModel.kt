package com.example.shop.feature_cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shop.feature_cart.domain.use_case.CartUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCases: CartUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> get() = _state

    private var getCartItemsJob: Job? = null

    init {
        viewModelScope.launch {
            getCartItems()
        }
    }

    private suspend fun getCartItems() {
        getCartItemsJob?.cancel()
        getCartItemsJob = cartUseCases.getCartItems().onEach { cartItems ->
            _state.update {
                it.copy(cartItems = cartItems)
            }
        }.launchIn(viewModelScope)
    }

    suspend fun onEvent(event: CartEvent) {
        when(event) {
            is CartEvent.AddToCart -> {
                cartUseCases.addToCart(event.productId)
            }
            is CartEvent.RemoveFromCart -> {
                cartUseCases.removeFromCart(event.productId)
            }
            is CartEvent.DeleteFromCart -> {
                cartUseCases.deleteFromCart(event.productId)
            }
            is CartEvent.IsUserLoggedIn -> {
                _state.update {
                    it.copy(userIsLoggedIn = cartUseCases.isUserLoggedIn())
                }
            }
            is CartEvent.TransferCart -> {
                cartUseCases.transferCart()
            }
        }
    }
}