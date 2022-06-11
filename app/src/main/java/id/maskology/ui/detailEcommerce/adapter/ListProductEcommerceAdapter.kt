package id.maskology.ui.detailEcommerce.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.maskology.R
import id.maskology.data.model.CategoryProduct
import id.maskology.data.model.Product
import id.maskology.data.model.ProductByStore
import id.maskology.databinding.ItemLargeProductBinding
import id.maskology.databinding.ItemLargeProductEcommerceBinding
import id.maskology.databinding.ItemSmallProductBinding
import id.maskology.ui.detailProduct.DetailProductActivity
import id.maskology.ui.main.adapter.ListCategoryProductAdapter
import id.maskology.utils.CurrencyFormatter

class ListProductEcommerceAdapter(private val listStoreProduct: List<ProductByStore>)  :
    ListAdapter<ProductByStore, ListProductEcommerceAdapter.ListViewHolder>(DIFF_CALLBACK)
{
    override fun onBindViewHolder(holder: ListProductEcommerceAdapter.ListViewHolder, position: Int) {
        holder.bind(listStoreProduct[position])
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListProductEcommerceAdapter.ListViewHolder {
        val binding = ItemLargeProductEcommerceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    inner class ListViewHolder(
        private val binding: ItemLargeProductEcommerceBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(productByStore : ProductByStore){
            binding.apply {
                Glide.with(itemView)
                    .load(productByStore.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_baseline_image)
                    .error(R.drawable.ic_baseline_broken_image)
                    .into(imgProduct)
                tvProductName.text = productByStore.name
                tvProductPrice.text = CurrencyFormatter.rupiahFormatter(Integer.valueOf(productByStore.price))
                tvStock.text = productByStore.stock.toString()

                root.setOnClickListener {
                    val intent = Intent(itemView.context, DetailProductActivity::class.java)
                    val product = Product(
                        productByStore.id,
                        productByStore.storeId,
                        productByStore.categoryId,
                        productByStore.name,
                        productByStore.price,
                        productByStore.stock,
                        productByStore.desc,
                        productByStore.imageUrl,
                        "",
                        productByStore.createdAt,
                        productByStore.updatedAt)
                    intent.putExtra("product", product)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imgProduct, "img_product")
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun getItemCount(): Int = listStoreProduct.size

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ProductByStore> =
            object : DiffUtil.ItemCallback<ProductByStore>() {
                override fun areItemsTheSame(oldItem: ProductByStore, newItem: ProductByStore): Boolean {
                    return oldItem == newItem
                }
                override fun areContentsTheSame(oldItem: ProductByStore, newItem: ProductByStore): Boolean {
                    return oldItem.id == newItem.id
                }

            }
    }

}