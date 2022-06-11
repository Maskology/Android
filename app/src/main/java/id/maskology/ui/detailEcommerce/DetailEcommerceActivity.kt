package id.maskology.ui.detailEcommerce

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.maskology.R
import id.maskology.data.Result
import id.maskology.data.model.CategoryProduct
import id.maskology.data.model.Product
import id.maskology.data.model.Store
import id.maskology.databinding.ActivityDetailEcommerceBinding
import id.maskology.ui.LoadingStateAdapter
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.detailEcommerce.adapter.ListProductEcommerceAdapter
import id.maskology.ui.detailEcommerce.viewmodel.DetailEcommerceViewModel
import id.maskology.ui.detailProduct.viewmodel.DetailProductViewModel
import id.maskology.ui.main.adapter.ListProductAdapter
import id.maskology.utils.NetworkCheck

class DetailEcommerceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEcommerceBinding
    private lateinit var store: Store
    private lateinit var detailEcommerceViewModel: DetailEcommerceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEcommerceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        setToolbar()
        getDataStore()
        getDataStoreProduct()
        setView()
        setListProductStore()
        setNoConnectionToast()
    }

    private fun setNoConnectionToast() {
        val isConnect = NetworkCheck.connectionCheck(binding.root.context)
        if (!isConnect) {
            Toast.makeText(this@DetailEcommerceActivity, resources.getString(R.string.text_no_connection_load_data),Toast.LENGTH_SHORT).show()
        }
    }


    private fun getDataStoreProduct() {
        detailEcommerceViewModel.getListProductByStore(store.id)
    }

    private fun setToolbar() {
        setSupportActionBar(binding.collapsebar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.collapsebar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setListProductStore() {
        detailEcommerceViewModel.listProductByStore.observe(this@DetailEcommerceActivity){ result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                        showErrorMessage(false, "")
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val listProductStore = result.data
                        if (listProductStore.size < 0){
                            val isConnect = NetworkCheck.connectionCheck(binding.root.context)
                            if (isConnect) {
                                showErrorMessage(true, resources.getString(R.string.text_no_data_store_product))
                            } else {
                                showErrorMessage(true, resources.getString(R.string.text_server_error_to_load_data))
                            }
                        } else {
                            showErrorMessage(false, "")
                            val listAdapter = ListProductEcommerceAdapter(listProductStore)

                            binding.listStoreProductLayout.rvStoreProduct.apply {
                                layoutManager = GridLayoutManager(this@DetailEcommerceActivity, 2)
                                setHasFixedSize(true)
                                adapter = listAdapter
                            }
                        }

                    }
                    is Result.Error -> {
                        showLoading(false)
                        showErrorMessage(true, resources.getString(R.string.text_server_error_to_load_data))
                    }
                }
            }

        }
    }

    private fun setView() {
        binding.collapsebar.collapsebar.title = store.name
        binding.collapsebar.headerDetailEcommerce.apply {
            Glide.with(applicationContext)
                .load(store.backgroundPictureUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_baseline_image)
                .error(R.drawable.ic_baseline_broken_image)
                .into(imgBackground)
            Glide.with(applicationContext)
                .load(store.profilePictureUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_image)
                .error(R.drawable.ic_baseline_broken_image)
                .into(imgProfile)
            tvEcommerceName.text = store.name
            tvPhoneNumber.text = store.contact
            tvDescription.text = store.desc
            btnContact.setOnClickListener { contactStore(store.contact) }
            btnCopy.setOnClickListener { copyContact(store.contact) }
        }
    }

    private fun contactStore(contact: String) {
        val url = "https://api.whatsapp.com/send?phone=$contact"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun copyContact(contact: String) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("contact", contact)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            this@DetailEcommerceActivity,
            resources.getString(R.string.text_copy_contact),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getDataStore() {
        store = intent.getParcelableExtra<Store>("store") as Store
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this@DetailEcommerceActivity.application)
        detailEcommerceViewModel = ViewModelProvider(this@DetailEcommerceActivity, factory)[DetailEcommerceViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean){
        binding.listStoreProductLayout.progresbar.apply {
            visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun showErrorMessage(isError: Boolean, message: String){
        binding.listStoreProductLayout.tvErrorMessage.apply {
            if (isError) {
                visibility = View.VISIBLE
                text = message
            } else {
                visibility = View.GONE
            }
        }
    }
}