package com.example.khunpet.ui.activities


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.khunpet.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

          override fun onCreate(savedInstanceState: Bundle?) {

              auth = Firebase.auth

              super.onCreate(savedInstanceState)
              binding = ActivityLoginBinding.inflate(layoutInflater)
              setContentView(binding.root)

              binding.txtSignUp.setOnClickListener(){
                  var intent = Intent(this, RegistroActivity::class.java)
                  startActivity(intent)
              }

              binding.btnIniciarSesion.setOnClickListener() {

                  val mEmail = binding.txtEmail.text.toString()
                  val mPassword = binding.txtPassword.text.toString()

                  when {
                      mPassword.isEmpty() || mEmail.isEmpty() -> {
                          Toast.makeText(this, "Email o contraseña o incorrectos.",
                              Toast.LENGTH_SHORT).show()
                      }
                      else -> {
                          signIn(mEmail, mPassword)
                      }
                  }
              }

              binding.loginPrincipal.setOnClickListener() {
                  hideSoftKeyboard(binding.root)
              }

          }

          fun hideSoftKeyboard(view: View) {
              val imm =
                  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
              imm.hideSoftInputFromWindow(view.windowToken, 0)
          }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithEmail:success")
                    reload()
                } else {
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Email o contraseña o incorrectos.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun reload() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

}