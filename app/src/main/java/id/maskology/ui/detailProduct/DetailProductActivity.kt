package id.maskology.ui.detailProduct

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.maskology.R
import id.maskology.data.Result
import id.maskology.data.model.Product
import id.maskology.data.model.Store
import id.maskology.databinding.ActivityDetailProductBinding
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.detailEcommerce.DetailEcommerceActivity
import id.maskology.ui.detailProduct.viewmodel.DetailProductViewModel
import id.maskology.utils.CurrencyFormatter


class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var product: Product
    private lateinit var detailProductViewModel: DetailProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        setEmptyStoreProfile()
        getDataProduct()
        setView()
        setToolbar()
        getDataStore()
        setProfileStore()
    }

    private fun setEmptyStoreProfile() {
        binding.descriptionProductLayout.miniProfileLayout.apply {
            imgStore.visibility = View.INVISIBLE
            tvStoreName.visibility = View.INVISIBLE
            tvPhoneNumber.visibility = View.INVISIBLE
            btnContact.visibility = View.INVISIBLE
            btnCopy.visibility = View.INVISIBLE
            tvErrorMessage.visibility = View.INVISIBLE
        }
    }

    private fun setProfileStore() {
        detailProductViewModel.store.observe(this@DetailProductActivity){result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                        showErrorMessage(false, "")
                        setEmptyStoreProfile()
                    }
                    is Result.Success -> {
                        showLoading(false)
                        showErrorMessage(false, "")
                        binding.descriptionProductLayout.miniProfileLayout.apply {
                            imgStore.visibility = View.VISIBLE
                            tvStoreName.visibility = View.VISIBLE
                            tvPhoneNumber.visibility = View.VISIBLE
                            btnContact.visibility = View.VISIBLE
                            btnCopy.visibility = View.VISIBLE
                        }
                        val store = result.data
                        setMiniProfileStore(store)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showErrorMessage(true, result.error)
                        setEmptyStoreProfile()
                    }
                }
            }
        }
    }

    private fun setMiniProfileStore(store: Store) {
        binding.descriptionProductLayout.miniProfileLayout.apply {
            Glide.with(applicationContext)
                .load(store.profilePictureUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_image)
                .error(R.drawable.ic_baseline_broken_image)
                .into(imgStore)
            tvStoreName.text = store.name
            tvPhoneNumber.text = store.contact
            btnContact.setOnClickListener { contactStore(store.contact) }
            btnCopy.setOnClickListener { copyContact(store.contact) }
            imgStore.setOnClickListener { toProfileStore(store) }
            tvStoreName.setOnClickListener { toProfileStore(store)  }
        }
    }

    private fun copyContact(contact: String) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("contact", contact)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            this@DetailProductActivity,
            resources.getString(R.string.text_copy_contact),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun toProfileStore(store: Store) {
        val intent = Intent(this@DetailProductActivity, DetailEcommerceActivity::class.java)
        intent.putExtra("store", store)
        startActivity(intent)
    }

    private fun contactStore(contact: String) {
        val url = "https://api.whatsapp.com/send?phone=$contact"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun getDataStore() {
        detailProductViewModel.getStore(product.storeId)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this@DetailProductActivity.application)
        detailProductViewModel = ViewModelProvider(this@DetailProductActivity, factory)[DetailProductViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_favorite -> addToFavorite()
            R.id.menu_share -> shareProduct()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareProduct() {
        Toast.makeText(this@DetailProductActivity, "This feature is still under development", Toast.LENGTH_SHORT).show()
    }

    private fun addToFavorite() {
        Toast.makeText(this@DetailProductActivity, "This feature is still under development", Toast.LENGTH_SHORT).show()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.collapsebar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.collapsebar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setView() {
        Glide.with(applicationContext)
            .load(product.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.ic_baseline_image)
            .error(R.drawable.ic_baseline_broken_image)
            .into(binding.collapsebar.headerDetailProduct.imgProduct)
        binding.collapsebar.collapsebar.title = product.name
        binding.descriptionProductLayout.tvPrice.text = CurrencyFormatter.rupiahFormatter(Integer.valueOf(product.price))
        binding.descriptionProductLayout.tvDescription.text = product.desc
        binding.descriptionProductLayout.tvStock.text = product.stock.toString()
    }

    private fun getDataProduct() {
        product = intent.getParcelableExtra<Product>("product") as Product
    }

    private fun showLoading(isLoading: Boolean){
        binding.descriptionProductLayout.miniProfileLayout.progresbar.apply {
            visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun showErrorMessage(isError: Boolean, message: String){
        binding.descriptionProductLayout.miniProfileLayout.tvErrorMessage.apply {
            if (isError) {
                visibility = View.VISIBLE
                text = message
            } else {
                visibility = View.GONE
            }
        }
    }
}