package id.maskology.ui.detailEcommerce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.maskology.data.Repository
import id.maskology.data.Result
import id.maskology.data.model.Category
import id.maskology.data.model.Product
import id.maskology.data.model.ProductByStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailEcommerceViewModel(private val repository: Repository) : ViewModel() {
    private val _listProduct = MutableLiveData<PagingData<Product>>()
    val listProduct: LiveData<PagingData<Product>> = _listProduct

    private val _listProductByStore = MutableLiveData<Result<List<ProductByStore>>>()
    val listProductByStore: LiveData<Result<List<ProductByStore>>> = _listProductByStore

    init {
        getListProduct()
    }

    private fun getListProduct() = viewModelScope.launch {
        repository.getAllProduct().cachedIn(viewModelScope).collect { values ->
            _listProduct.value = values
        }
    }

    fun getListProductByStore(storeId: String) = viewModelScope.launch {
        repository.getProductByStore(storeId).collect { values ->
            _listProductByStore.value = values
        }
    }

}