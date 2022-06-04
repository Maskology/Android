package id.maskology.ui.main.adapter

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
import id.maskology.databinding.ItemSmallProductBinding
import id.maskology.ui.detailProduct.DetailProductActivity
import id.maskology.utils.CurrencyFormatter

class ListNewProductAdapter :
    PagingDataAdapter<Product, ListNewProductAdapter.ListViewHolder>(DIFF_CALLBACK)
{
    override fun onBindViewHolder(holder: ListNewProductAdapter.ListViewHolder, position: Int) {
        val newProduct = getItem(position)
        if (newProduct != null){
            holder.bind(newProduct)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListNewProductAdapter.ListViewHolder {
        val binding = ItemSmallProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    inner class ListViewHolder(
        private val binding: ItemSmallProductBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(newProduct: Product){
            binding.apply {
                Glide.with(itemView)
                    .load(newProduct.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_baseline_image)
                    .error(R.drawable.ic_baseline_broken_image)
                    .into(imgProduct)
                tvProductName.text = newProduct.name
                tvProductPrice.text = CurrencyFormatter.rupiahFormatter(Integer.valueOf(newProduct.price))
                tvStoreName.text = newProduct.storeName

                root.setOnClickListener {
                    val intent = Intent(itemView.context, DetailProductActivity::class.java)
                    intent.putExtra("product", newProduct)

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