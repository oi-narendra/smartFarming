package com.pranav.smartfarming.ui.login

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pranav.smartfarming.databinding.ActivityLoginBinding
import com.pranav.smartfarming.databinding.ProgressDialogBinding
import com.pranav.smartfarming.utils.errorToast
import com.pranav.smartfarming.utils.isValidEmail
import com.pranav.smartfarming.utils.successToast
import timber.log.Timber


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var mAuth: FirebaseAuth? = null

    private var email: String = ""
    private var password: String = ""

    lateinit var progressDialog: Dialog
    lateinit var progressDialogBinding: ProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        setupProgressDialog()

        binding.loginButton.setOnClickListener {

            progressDialog.show()

            if (validateLoginDetails()) {

                mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Timber.d("signInWithEmail:success")
                            successToast("Success")
                            progressDialog.dismiss()

                        } else {
                            errorToast("${task.exception?.message}")
                            Timber.d("Login failed")
                            progressDialog.dismiss()
                        }
                    }
            }

        }
    }

    private fun setupProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        progressDialogBinding =
            ProgressDialogBinding.inflate(layoutInflater, binding.root, false)

        progressDialog.setContentView(progressDialogBinding.root)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun validateLoginDetails(): Boolean {
        email = binding.loginEmail.text.toString().trim()
        password = binding.loginPassword.text.toString().trim()
        return when {
            email.isEmpty() || !email.isValidEmail() -> {
                binding.loginEmail.error = "Invalid Email"
                false
            }
            password.isEmpty() || password.length < 5 -> {
                binding.loginPassword.error = "Invalid Password"
                false
            }
            else -> true
        }
    }


}


