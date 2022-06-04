package id.maskology.ui.onboarding

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.maskology.R
import id.maskology.databinding.ActivityOnBoardingBinding
import id.maskology.ui.ViewModelFactory
import id.maskology.ui.main.MainActivity
import id.maskology.ui.onboarding.viewmodel.OnBoardingViewModel
import id.maskology.utils.NetworkCheck

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var onBoardingViewModel: OnBoardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        val isConnect = NetworkCheck.connectionCheck(binding.root.context)

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // Initialize Firebase Auth
        auth = Firebase.auth
        binding.btnLogin.setOnClickListener {
            if (isConnect) {
                signIn()
            } else {
                showAlertConnectionFirebase()
            }
        }
        binding.btnLoginGuest.setOnClickListener {
            if (isConnect) {
                signInAsGuest()
            } else {
                showAlertConnectionGuest()
            }
        }
    }

    private fun autoSignInAsGuest() {
        onBoardingViewModel.getEmailPreferences().observe(this@OnBoardingActivity){result ->
            if (result != "") {
                startActivity(Intent(this@OnBoardingActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this@OnBoardingActivity.application)
        onBoardingViewModel = ViewModelProvider(this@OnBoardingActivity, factory)[OnBoardingViewModel::class.java]
    }

    private fun signInAsGuest() {
        val email = "guest@gmail.com"
        val name = "Guest User"
        val imageUrl = "guest"
        onBoardingViewModel.saveAuthPreferences(email, name, imageUrl)
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@OnBoardingActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
        autoSignInAsGuest()
    }

    private fun showAlertConnectionFirebase() {
        MaterialAlertDialogBuilder(this@OnBoardingActivity)
            .setTitle(resources.getString(R.string.title_no_connection_alert))
            .setMessage(resources.getString(R.string.no_connection_message))
            .setPositiveButton(resources.getString(R.string.title_btn_alert_neutral)){_,_ ->
                //Do nothing
            }
            .show()
    }

    private fun showAlertConnectionGuest() {
        MaterialAlertDialogBuilder(this@OnBoardingActivity)
            .setTitle(resources.getString(R.string.title_no_connection_alert))
            .setMessage(resources.getString(R.string.no_connection_alert))
            .setPositiveButton(resources.getString(R.string.text_keep_login)){_,_ ->
                signInAsGuest()
            }
            .show()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}