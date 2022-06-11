package id.maskology.ui.main.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import id.maskology.R
import id.maskology.data.Repository
import id.maskology.data.Result
import id.maskology.databinding.FragmentHomeBinding
import id.maskology.data.model.Banner
import id.maskology.data.model.CategoryProduct
import id.maskology.ui.LoadingStateAdapter
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.main.adapter.*
import id.maskology.ui.main.viewmodel.HomeViewModel
import id.maskology.utils.NetworkCheck

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bannerSlideAdapter: BannerSlideAdapter
    private lateinit var sliderHandler: Handler
    private lateinit var sliderRun: Runnable
    private lateinit var reSliderRun: Runnable
    private lateinit var homeViewModel: HomeViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setViewModel()
        setSliderBanner()
        setListNewProducts()
        getDataListCategory()
        setListProduct()
        setNoConnectionToast()
    }

    private fun setNoConnectionToast() {
        val isConnect = NetworkCheck.connectionCheck(binding.root.context)
        if (!isConnect) {
            Toast.makeText(requireContext(), resources.getString(R.string.text_no_connection_load_data),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListProduct() {
        val listAdapter = ListProductAdapter()
        binding.rvProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
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

        homeViewModel.listProduct.observe(viewLifecycleOwner){ listProduct->
            listAdapter.submitData(lifecycle, listProduct)
        }
    }

    private fun getDataListCategory() {
        homeViewModel.listCategoryProduct.observe(viewLifecycleOwner){ result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        //do nothing
                    }
                    is Result.Success -> {
                        val defaultListCategoryProduct = listOf(
                            CategoryProduct("all", resources.getString(R.string.all_category))
                        )
                        val listCategoryProduct = defaultListCategoryProduct + result.data
                        setListCategoryProduct(listCategoryProduct)
                    }
                    is Result.Error -> {
                        //do nothing
                    }
                }
            }
        }
    }

    private fun setListCategoryProduct(listCategoryProduct: List<CategoryProduct>) {
        val listAdapter = ListCategoryProductAdapter(listCategoryProduct)

        binding.collapsedbar.rvCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = listAdapter
        }
    }

    private fun setListNewProducts() {
        val listAdapter = ListNewProductAdapter()
        binding.collapsedbar.headerHome.rvNewProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = listAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listAdapter.retry()
                }
            )
        }

        binding.collapsedbar.headerHome.rvNewProduct.adapter = listAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listAdapter.retry()
            }
        )

        homeViewModel.listProduct.observe(viewLifecycleOwner){ listNewProduct ->
            listAdapter.submitData(lifecycle, listNewProduct)
        }

    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity().application)
        homeViewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
    }

    private fun setSliderBanner() {
        val listBanner = listOf(
            Banner(R.drawable.dummy_1),
            Banner(R.drawable.dummy_2),
            Banner(R.drawable.dummy_3)
        )

        binding.collapsedbar.headerHome.vpBannerSlider.apply {
            bannerSlideAdapter = BannerSlideAdapter(listBanner)
            sliderHandler = Handler()
            sliderRun = Runnable {
                currentItem += 1
            }
            reSliderRun = Runnable {
                currentItem = 0
            }
            adapter = bannerSlideAdapter
            setIndicatorBanner()
            setActiveBannerIndicator(0)
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setActiveBannerIndicator(position)
                    sliderHandler.removeCallbacks(sliderRun)
                    sliderHandler.postDelayed(sliderRun, 5000)
                    if (position == listBanner.size - 1){
                        sliderHandler.postDelayed(reSliderRun, 5000)
                    }
                }
            })
        }

    }

    private fun setIndicatorBanner() {
        val indicator = arrayOfNulls<ImageView>(bannerSlideAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicator.indices){
            indicator[i] = ImageView(requireContext())
            indicator[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.collapsedbar.headerHome.indicatorSlideLayout.addView(indicator[i])
        }
    }

    private fun setActiveBannerIndicator(index: Int) {
        val childCount = binding.collapsedbar.headerHome.indicatorSlideLayout.childCount
        for (i in 0 until childCount) {
            val imageView = binding.collapsedbar.headerHome.indicatorSlideLayout[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_indicator_inactive
                    )
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRun)
        sliderHandler.removeCallbacks(reSliderRun)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRun, 3000)
    }

}