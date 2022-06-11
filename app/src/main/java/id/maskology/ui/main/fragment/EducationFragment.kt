package id.maskology.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.maskology.R
import id.maskology.data.model.FunFact
import id.maskology.databinding.FragmentEducationBinding
import id.maskology.ui.LoadingStateAdapter
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.main.adapter.ListCategoryEducationAdapter
import id.maskology.ui.main.adapter.ListFunFactAdapter
import id.maskology.ui.main.viewmodel.EducationViewModel
import id.maskology.utils.NetworkCheck

class EducationFragment : Fragment(R.layout.fragment_education) {

    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!
    private lateinit var educationViewModel: EducationViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEducationBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        setListCategory()
        setListFunFact()
        setNoConnectionToast()
    }

    private fun setNoConnectionToast() {
        val isConnect = NetworkCheck.connectionCheck(binding.root.context)
        if (!isConnect) {
            Toast.makeText(requireContext(), resources.getString(R.string.text_no_connection_load_data),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListFunFact() {
        val listFunFact = listOf(
            FunFact(R.drawable.dummy_1, "Tarian Topeng Keras", "Tari Topeng Keras adalah satu tarian putra (tunggal) memakai topeng, dengan perbendaharaan gerak yang sederhana tetapi membutuhkan kemampuan penari untuk menyesuaikan gerak dengan ekspresi topeng. Tarian ini biasanya ditampilkan sebagai pembuka (penglembar) dari pertunjukan drama tari topeng, dilakukan dengan penekanan pada penguasaan terhadap jalinan wiraga dan wirama yang didukung kesadasan dan kepahaman akan wirasa"),
            FunFact(R.drawable.dummy_2, "Topeng Tua Refleksi Manusia di Usia Tua", "Tari Topeng Tua merupakan bagian drama tari tradisional Bali. Selain dipentaskan sebagai pertunjukan hiburan, ada pula jenis tari topeng yang menjadi pelengkap dari upacara keagamaan. Salah satu tari topeng yang memiliki fungsi dalam kedua hal tersebut adalah tari topeng tua, yang disebut juga tari werda lumaku. Tari topeng tua menampilkan seorang penari dengan busana yang megah dan mengenakan topeng kayu dari kayu ylang-ylang. Dari raut wajahnya, terlihat tokoh yang diperankan adalah pria berusia senja."),
            FunFact(R.drawable.dummy_3, "Tari Barong representasi Dharma dan Adharma", "Tari Barong merupakan peninggalan kebudayaan pra-Hindu yang melambangkan pertempuran antara kebaikan (dharma) dan keburukan (adharma). Menurut keyakinan masyarakat Bali, khususnya yang beragama Hindu, kebaikan dan keburukan selalu berdampingan atau disebut juga sebagai Rwa Bhineda. Kata Barong berasal dari kata bahruang yang berarti beruang.")
        )

        val listAdapter = ListFunFactAdapter(listFunFact)
        binding.collapsedbar.headerEducation.rvFunFact.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = listAdapter
        }


    }

    private fun setListCategory() {
        val listAdapter = ListCategoryEducationAdapter()
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = listAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listAdapter.retry()
                }
            )
        }

        binding.rvCategory.adapter = listAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listAdapter.retry()
            }
        )

        educationViewModel.listCategory.observe(viewLifecycleOwner){ listCategory ->
            listAdapter.submitData(lifecycle, listCategory)
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity().application)
        educationViewModel = ViewModelProvider(requireActivity(), factory)[EducationViewModel::class.java]
    }
}