package id.maskology.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "productByStore")
@Parcelize
data class ProductByStore (
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("storeId")
    val storeId: String,

    @field:SerializedName("categoryId")
    val categoryId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("price")
    val price: String,

    @field:SerializedName("stock")
    val stock: Int,

    @field:SerializedName("desc")
    val desc: String,

    @field:SerializedName("imageUrl")
    val imageUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String,
) : Parcelable