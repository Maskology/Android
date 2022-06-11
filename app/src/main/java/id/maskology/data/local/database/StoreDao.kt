package id.maskology.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.maskology.data.model.Store

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: List<Store>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOneStore(store: Store)

    @Query("SELECT * FROM store")
    fun getAllStore(): PagingSource<Int, Store>

    @Query("SELECT * FROM store WHERE id=:id")
    fun getStore(id: String): Store

    @Query("DELETE FROM store WHERE id=:id")
    suspend fun deleteStore(id: String)
}