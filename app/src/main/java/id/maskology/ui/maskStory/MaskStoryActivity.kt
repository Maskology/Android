package id.maskology.ui.maskStory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.maskology.R
import id.maskology.data.model.Category
import id.maskology.data.model.Product
import id.maskology.databinding.ActivityDetailProductBinding
import id.maskology.databinding.ActivityMaskStoryBinding
import id.maskology.ui.LoadingStateAdapter
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.main.adapter.ListNewProductAdapter
import id.maskology.ui.maskStory.adapter.ListRelatedProductAdapter
import id.maskology.ui.maskStory.viewmodel.MaskStoryViewModel
import id.maskology.utils.LocalDateConverter
import id.maskology.utils.NetworkCheck

class MaskStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaskStoryBinding
    private lateinit var category: Category
    private lateinit var maskStoryViewModel: MaskStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaskStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        getDataCategory()
        getDataProduct()
        setView()
        setToolbar()
        setListRelatedProduct()
        setNoConnectionToast()
    }

    private fun setNoConnectionToast() {
        val isConnect = NetworkCheck.connectionCheck(binding.root.context)
        if (!isConnect) {
            Toast.makeText(this@MaskStoryActivity, resources.getString(R.string.text_no_connection_load_data),Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDataProduct() {
        when (category.name){
            "Topeng Barong" -> {maskStoryViewModel.getListProductByCategory("barong")}
            "Topeng Bujuh" -> {maskStoryViewModel.getListProductByCategory("bujuh")}
            "Topeng Dalem" -> {maskStoryViewModel.getListProductByCategory("dalem")}
            "Topeng Keras" -> {maskStoryViewModel.getListProductByCategory("keras")}
            "Topeng Sidakarya" -> {maskStoryViewModel.getListProductByCategory("sidakarya")}
            "Topeng Tua" -> {maskStoryViewModel.getListProductByCategory("tua")}
        }
    }

    private fun setListRelatedProduct() {
        val listAdapter = ListRelatedProductAdapter()
        binding.descriptionStoryMaskLayout.rvRelatedProduct.apply {
            layoutManager = LinearLayoutManager(this@MaskStoryActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = listAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listAdapter.retry()
                }
            )
        }

        maskStoryViewModel.listProduct.observe(this@MaskStoryActivity){ listRelatedProduct ->
            listAdapter.submitData(lifecycle, listRelatedProduct)
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this@MaskStoryActivity.application)
        maskStoryViewModel = ViewModelProvider(this@MaskStoryActivity, factory)[MaskStoryViewModel::class.java]
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
            .load(category.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.ic_baseline_image)
            .error(R.drawable.ic_baseline_broken_image)
            .into(binding.collapsebar.headerStoryMask.imgMask)
        binding.collapsebar.collapsebar.title = category.name
        binding.descriptionStoryMaskLayout.tvWriterName.text = category.author
        binding.descriptionStoryMaskLayout.tvDescription.text = category.detail
        binding.descriptionStoryMaskLayout.tvYear.text = LocalDateConverter.convertLocalDate(category.updatedAt)
    }

    private fun getDataCategory() {
        category = intent.getParcelableExtra<Category>("category") as Category
    }

}