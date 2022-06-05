package id.maskology.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.maskology.R
import id.maskology.databinding.ActivityMainBinding
import id.maskology.ui.camera.CameraActivity
import id.maskology.ui.detailEcommerce.DetailEcommerceActivity
import id.maskology.ui.detailProduct.DetailProductActivity
import id.maskology.ui.main.fragment.EducationFragment
import id.maskology.ui.main.fragment.FavoriteFragment
import id.maskology.ui.main.fragment.HomeFragment
import id.maskology.ui.main.fragment.ProfileFragment
import id.maskology.utils.NetworkCheck

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private val educationFragment = EducationFragment()
    private val profileFragment = ProfileFragment()
    private val favoriteFragment = FavoriteFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isConnect = NetworkCheck.connectionCheck(binding.root.context)

        setFragment(homeFragment)
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId){
                R.id.nav_home -> setFragment(homeFragment)
                R.id.nav_education -> setFragment(educationFragment)
                R.id.nav_favorite -> setFragment(favoriteFragment)
                R.id.nav_profile -> setFragment(profileFragment)
            }
            true
        }

        binding.fabCamera.setOnClickListener {
            if (isConnect) {
                toCameraActivity()
            } else {
                showAlertConnectionProblem()
            }
        }
    }



    private fun toCameraActivity() {
        startActivity(Intent(this@MainActivity, CameraActivity::class.java))
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }


    override fun onResume() {
        super.onResume()
        when (binding.bottomNavigation.selectedItemId){
            R.id.nav_home -> setFragment(homeFragment)
            R.id.nav_education -> setFragment(educationFragment)
            R.id.nav_favorite -> setFragment(favoriteFragment)
            R.id.nav_profile -> setFragment(profileFragment)
        }
    }

    private fun showAlertConnectionProblem(){
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(resources.getString(R.string.title_no_connection_alert))
            .setMessage(resources.getString(R.string.no_connection_alert))
            .setPositiveButton(resources.getString(R.string.title_btn_alert_neutral)){_,_ ->
                //Do nothing
            }
            .show()
    }


    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}