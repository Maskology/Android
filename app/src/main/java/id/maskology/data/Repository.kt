package id.maskology.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import id.maskology.data.local.database.MaskologyDatabase
import id.maskology.data.local.datastore.AuthPreferences
import id.maskology.data.model.*
import id.maskology.data.remote.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class Repository(
    private val apiService: ApiService,
    private val maskologyDatabase: MaskologyDatabase,
    private val authPreferences: AuthPreferences

) {
    fun getAllProduct(): Flow<PagingData<Product>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = ProductRemoteMediator(maskologyDatabase, apiService),
            pagingSourceFactory = {
                maskologyDatabase.productDao().getAllProduct()
            }
        ).flow
    }

    fun getAllProductByCategory(category: String): Flow<PagingData<Product>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                ProductByCategoryPagingSource(apiService, category)
            }
        ).flow
    }

    fun getAllCategory(): Flow<PagingData<Category>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = CategoryRemoteMediator(maskologyDatabase, apiService),
            pagingSourceFactory = {
                maskologyDatabase.categoryDao().getAllCategory()
            }
        ).flow
    }

    fun getAllCategoryProduct(): Flow<Result<List<CategoryProduct>>>{
        return flow {
            emit(Result.Loading)
            try {
                val page = 1
                val size = 5
                val response = apiService.getAllCategoryProduct(page, size)
                val totalPage = response.meta.totalPage
                maskologyDatabase.categoryProductDao().deleteAllCategoryProduct()

                for (i in page..totalPage){
                    val responseApi = apiService.getAllCategoryProduct(i, size)
                    val listCategoryProduct = responseApi.listCategoryProduct
                    maskologyDatabase.categoryProductDao().insertCategoryProduct(listCategoryProduct)
                }
            }catch (e: Exception) {
                emit(Result.Error("Category Product Not Found"))
                Log.e(TAG, "Get Store: ${e.message.toString()}")
            }

            val resultList = maskologyDatabase.categoryProductDao().getAllCategoryProduct()
            emit(Result.Success(resultList))
        }.flowOn(Dispatchers.IO)
    }


    fun getStore(id: String): Flow<Result<Store>>{
        return flow {
            emit(Result.Loading)
            try {
                val response = apiService.getStore(id)
                maskologyDatabase.storeDao().deleteStore(id)
                maskologyDatabase.storeDao().insertOneStore(response)
                Log.d(TAG, "Get Store: ${response.id}")
            }catch (e: Exception) {
                emit(Result.Error("Store Not Found"))
                Log.e(TAG, "Get Store: ${e.message.toString()}")
            }
            val store = maskologyDatabase.storeDao().getStore(id)
            emit(Result.Success(store))
        }.flowOn(Dispatchers.IO)
    }

    fun getProductByStore(storeId: String): Flow<Result<List<ProductByStore>>>{
        return flow {
            emit(Result.Loading)
            try {
                val response = apiService.getAllProductByStore(storeId)
                maskologyDatabase.productByStoreDao().deleteAllProduct(storeId)
                maskologyDatabase.productByStoreDao().insertProduct(response)
                Log.d(TAG, "Get Store: $response")
            }catch (e: Exception) {
                emit(Result.Error("Store Product Not Found"))
                Log.e(TAG, "Get Store Product: ${e.message.toString()}")
            }
            val listProductByStore = maskologyDatabase.productByStoreDao().getAllProductByStore(storeId)
            emit(Result.Success(listProductByStore))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveAuthPreferences(email: String, name: String, imageUrl: String) {
        authPreferences.saveAuthPreferences(email, name, imageUrl)
    }

    fun getNamePreferences(): LiveData<String> {
        return authPreferences.getNamePreferences().asLiveData()
    }

    fun getEmailPreferences(): LiveData<String> {
        return authPreferences.getEmailPreferences().asLiveData()
    }

    fun getImageUrlPreferences(): LiveData<String> {
        return authPreferences.getImageUrlPreferences().asLiveData()
    }

    suspend fun deleteAuthPreferences() {
        authPreferences.deleteAuthPreferences()
    }


    companion object {
        private val TAG = Repository::class.java.simpleName

        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance(apiService: ApiService, maskologyDatabase: MaskologyDatabase, authPreferences: AuthPreferences): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(apiService, maskologyDatabase, authPreferences)
            }.also { INSTANCE = it }
    }
}