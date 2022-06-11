package id.maskology.ui.maskStory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.maskology.data.Repository
import id.maskology.data.model.Category
import id.maskology.data.model.Product
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MaskStoryViewModel(private val repository: Repository) : ViewModel() {
    private val _listProduct = MutableLiveData<PagingData<Product>>()
    val listProduct: LiveData<PagingData<Product>> = _listProduct

    fun getListProductByCategory(category: String)  = viewModelScope.launch {
        repository.getAllProductByCategory(category).cachedIn(viewModelScope).collect { values ->
            _listProduct.value = values
        }
    }

}