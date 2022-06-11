package id.maskology.ui.main.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.maskology.R
import id.maskology.databinding.FragmentProfileBinding
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.main.viewmodel.ProfileViewModel
import id.maskology.ui.onboarding.OnBoardingActivity
import id.maskology.utils.NetworkCheck


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var profileViewModel: ProfileViewModel
    private var firebaseUser: FirebaseUser? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        setViewModel()
        setNoConnectionToast()
        auth = Firebase.auth

        firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            setGuestView()
        } else {
            setFirebaseUserView()
        }
    }

    private fun setNoConnectionToast() {
        val isConnect = NetworkCheck.connectionCheck(binding.root.context)
        if (!isConnect) {
            Toast.makeText(requireContext(), resources.getString(R.string.text_no_connection_load_data),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFirebaseUserView() {
        var name = firebaseUser?.displayName
        var photo = firebaseUser?.photoUrl
        binding.nameUser.text = name
        Glide.with(this@ProfileFragment)
            .load(photo)
            .into(binding.profileUser)
        binding.btnLogout.setOnClickListener{
            showPopupFirebaseUser()
        }

    }

    private fun setGuestView() {
        profileViewModel.getNamePreferences().observe(viewLifecycleOwner){name ->
            binding.nameUser.text = name
        }
        Glide.with(this@ProfileFragment)
            .load(R.drawable.ic_baseline_person)
            .into(binding.profileUser)
        binding.btnLogout.setOnClickListener{
            showPopupGuest()
        }
    }

    private fun showPopupGuest() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(context)
        alert.setMessage("Are you sure to logout Guest?")
            .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, which ->
                signOutGuest() // Last step. Logout function
            }).setNegativeButton("Cancel", null)
        val alert1: AlertDialog = alert.create()
        alert1.show()
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity().application)
        profileViewModel = ViewModelProvider(requireActivity(), factory)[ProfileViewModel::class.java]
    }

    private fun showPopupFirebaseUser() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(context)
        alert.setMessage("Are you sure to logout User?")
            .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, which ->
                signOutFirebaseUser() // Last step. Logout function
            }).setNegativeButton("Cancel", null)
        val alert1: AlertDialog = alert.create()
        alert1.show()
    }

    private fun signOutFirebaseUser() {
        auth.signOut()
        startActivity(Intent(activity, OnBoardingActivity::class.java))
        activity?.finish()
    }

    private fun signOutGuest() {
        profileViewModel.deleteAuthPreferences()
        startActivity(Intent(activity, OnBoardingActivity::class.java))
        activity?.finish()
    }
}