package id.maskology.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.maskology.R
import id.maskology.databinding.ActivitySplashscreenBinding
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.main.MainActivity
import id.maskology.ui.onboarding.OnBoardingActivity
import id.maskology.ui.onboarding.viewmodel.OnBoardingViewModel
import id.maskology.ui.splashscreen.viewmodel.SplashscreenViewModel
import id.maskology.utils.NetworkCheck
import kotlin.system.exitProcess

class SplashscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding
    private val delay = 3000L
    private lateinit var splashscreenViewModel: SplashscreenViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        auth = Firebase.auth
        checkInternet()
    }

    private fun checkInternet() {
        val isConnect = NetworkCheck.connectionCheck(binding.root.context)
        if (!isConnect) {
            Toast.makeText(this@SplashscreenActivity, resources.getString(R.string.text_toast_connection_alert), Toast.LENGTH_LONG).show()
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this@SplashscreenActivity.application)
        splashscreenViewModel = ViewModelProvider(this@SplashscreenActivity, factory)[SplashscreenViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        playAnimation()
        splashDelay()
    }

    private fun playAnimation() {
        val animateLogo = ObjectAnimator.ofFloat(binding.imgLogo, View.ALPHA, 1f).setDuration(1000)
        val animateTextOwner = ObjectAnimator.ofFloat(binding.tvOwner, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playSequentially(animateLogo, animateTextOwner)
            start()
        }
    }

    private fun splashDelay() {
        Handler(Looper.getMainLooper()).postDelayed({toNextActivity()}, delay)
    }

    private fun toNextActivity() {
        splashscreenViewModel.getEmailPreferences().observe(this@SplashscreenActivity){currentUserEmail ->
            if (auth.currentUser != null || currentUserEmail !=""){
                startActivity(Intent(this@SplashscreenActivity, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashscreenActivity, OnBoardingActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        exitProcess(0)
    }
}