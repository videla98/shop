package com.example.shop.ui.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shop.data.ShopRepository
import com.example.shop.data.database.entities.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: ShopRepository,
) : ViewModel() {
    private val sortBy = MutableLiveData<String>("Price")
    private val _sortOrder = MutableLiveData<Boolean>(false)
    val sortOrder get() = _sortOrder
    private val _productList = MutableLiveData<List<Product>>()
    val products get() = _productList

    init {
        viewModelScope.launch {
            repository.setCart()
        }
        getProductsSorted()
    }

    fun addToCart(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToCart(id)
        }
    }

    fun changeSortOrder() {
        _sortOrder.value = !_sortOrder.value!!
        getProductsSorted()
    }

    fun sortBy(selection: String) {
        sortBy.value = selection
        getProductsSorted()
    }

    private fun getProductsSorted() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProductsSortedBy(sortBy.value ?: "Price", sortOrder.value ?: false)
                .collect {
                    _productList.postValue(it)
                }
        }
    }
}