package id.maskology.data.remote.api

import id.maskology.data.model.Category
import id.maskology.data.model.Product
import id.maskology.data.model.ProductByStore
import id.maskology.data.model.Store
import id.maskology.data.remote.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("categories")
    suspend fun getAllCategory(
        @Query("page") page: Int,
        @Query("limit") size: Int
    ) : CategoryResponse

    @GET("categories")
    suspend fun getAllCategoryProduct(
        @Query("page") page: Int,
        @Query("limit") size: Int
    ) : CategoryProductResponse

    @GET("stores")
    suspend fun getAllStore(
        @Query("page") page: Int,
        @Query("limit") size: Int
    ) : StoreResponse

    @GET("products")
    suspend fun getAllProduct(
        @Query("page") page: Int,
        @Query("limit") size: Int
    ) : ProductResponse

    @GET("products")
    suspend fun getAllProductByCategory(
        @Query("page") page: Int,
        @Query("limit") size: Int,
        @Query("category") category: String,
    ) : ProductResponse

    @GET("stores/{id}/product")
    suspend fun getAllProductByStore(
        @Path("id") id: String
    ) : List<ProductByStore>

    @GET("categories/{id}")
    suspend fun getCategory(
        @Path("id") id: String
    ) : Category

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ) : Product

    @GET("stores/{id}")
    suspend fun getStore(
        @Path("id") id: String
    ) : Store

    @Multipart
    @POST("predict")
    fun predict(
        @Part image: MultipartBody.Part
    ) : Call<PredictResponse>
}