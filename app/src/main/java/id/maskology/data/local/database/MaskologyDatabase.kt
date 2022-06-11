package id.maskology.data.local.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.maskology.data.model.*

@Database(
    entities = [
        Category::class,
        CategoryProduct::class,
        Store::class,
        Product::class,
        StoreRemoteKeys::class,
        ProductRemoteKeys::class,
        CategoryRemoteKeys::class,
        ProductByStore::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MaskologyDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun categoryProductDao(): CategoryProductDao
    abstract fun storeDao(): StoreDao
    abstract fun productDao(): ProductDao
    abstract fun productRemoteKeysDao(): ProductRemoteKeysDao
    abstract fun storeRemoteKeysDao(): StoreRemoteKeysDao
    abstract fun categoryRemoteKeysDao(): CategoryRemoteKeysDao
    abstract fun productByStoreDao(): ProductByStoreDao

    companion object {
        @Volatile
        private var INSTANCE: MaskologyDatabase? = null

        @JvmStatic
        fun getInstance(application: Application): MaskologyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    application,
                    MaskologyDatabase::class.java, "maskologyDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}