package id.maskology.ui.maskStory.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.maskology.R
import id.maskology.data.model.Product
import id.maskology.databinding.ItemLargeProductBinding
import id.maskology.databinding.ItemSmallProductBinding
import id.maskology.ui.detailProduct.DetailProductActivity
import id.maskology.utils.CurrencyFormatter

class ListRelatedProductAdapter :
    PagingDataAdapter<Product, ListRelatedProductAdapter.ListViewHolder>(DIFF_CALLBACK)
{
    override fun onBindViewHolder(holder: ListRelatedProductAdapter.ListViewHolder, position: Int) {
        val product = getItem(position)
        if (product  != null){
            holder.bind(product)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListRelatedProductAdapter.ListViewHolder {
        val binding = ItemSmallProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    inner class ListViewHolder(
        private val binding: ItemSmallProductBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(product : Product){
            binding.apply {
                Glide.with(itemView)
                    .load(product.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_baseline_image)
                    .error(R.drawable.ic_baseline_broken_image)
                    .into(imgProduct)
                tvProductName.text = product.name
                tvProductPrice.text = CurrencyFormatter.rupiahFormatter(Integer.valueOf(product.price))
                tvStoreName.text = product.storeName

                root.setOnClickListener {
                    val intent = Intent(itemView.context, DetailProductActivity::class.java)
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

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Product> =
            object : DiffUtil.ItemCallback<Product>() {
                override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem == newItem
                }
                override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem.id == newItem.id
                }

            }
    }

}