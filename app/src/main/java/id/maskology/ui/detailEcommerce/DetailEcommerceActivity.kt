package id.maskology.ui.detailEcommerce

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.maskology.R
import id.maskology.data.model.Product
import id.maskology.data.model.Store
import id.maskology.databinding.ActivityDetailEcommerceBinding
import id.maskology.ui.LoadingStateAdapter
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.detailEcommerce.adapter.ListProductEcommerceAdapter
import id.maskology.ui.detailEcommerce.viewmodel.DetailEcommerceViewModel
import id.maskology.ui.detailProduct.viewmodel.DetailProductViewModel
import id.maskology.ui.main.adapter.ListProductAdapter

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
        setView()
        setListProductStore()
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
        val listAdapter = ListProductEcommerceAdapter()
        binding.rvProduct.apply {
            layoutManager = GridLayoutManager(this@DetailEcommerceActivity, 2)
            setHasFixedSize(true)
            adapter = listAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listAdapter.retry()
                }
            )
        }

        binding.rvProduct.adapter = listAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listAdapter.retry()
            }
        )

        detailEcommerceViewModel.listProduct.observe(this@DetailEcommerceActivity){ listProduct->
            listAdapter.submitData(lifecycle, listProduct)
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
}