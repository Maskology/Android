package id.maskology.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.maskology.data.model.Product
import id.maskology.data.model.ProductByStore

@Dao
interface ProductByStoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: List<ProductByStore>)

    @Query("SELECT * FROM productByStore WHERE storeId=:storeId")
    fun getAllProductByStore(storeId: String): List<ProductByStore>

    @Query("DELETE FROM productByStore WHERE storeId=:storeId")
    suspend fun deleteAllProduct(storeId: String)
}