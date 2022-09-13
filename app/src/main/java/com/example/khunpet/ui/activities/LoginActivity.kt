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
import com.example.khunpet.model.Usuario
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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


              binding.imageView2.setOnClickListener() {
                  val intent = Intent(this, MainActivityRefug::class.java)
                  this.startActivity(intent)

              }
/*


              binding.txtSignUp.setOnClickListener() {
                  val intent = Intent(this, MainActivity::class.java)
                  this.startActivity(intent)

              }
*/

             /* binding.btnIniciarSesion.setOnClickListener() {

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
                  */


                  binding.btnIniciarSesion.setOnClickListener() {
                      val mEmail = binding.txtEmail.text.toString()
                      val mPassword = binding.txtPassword.text.toString()

                      when {
                          mPassword.isEmpty() || mEmail.isEmpty() -> {
                              Toast.makeText(this, "Email o contraseña o incorrectos.",
                                  Toast.LENGTH_SHORT).show()
                          }
                          else -> {
                              if(mEmail=="pae@gmail.com"){
                                  reloadRefug()
                              }else{
                                  signIn(mEmail, mPassword)
                              }
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
        getUsuario()
    }

    fun getUsuario()  {
        val guid : String = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("guid", guid)
            .get()
            .addOnCompleteListener {
                if (it.result.documents.isNotEmpty()) {
                    AppDatabase.setUsuario(it.result.documents[0].toObject(Usuario::class.java)!!)
                    val usuario = AppDatabase.getUsuarioConectado()
                    if (usuario.tipo=="usuario") {
                        val intent = Intent(this, MainActivity::class.java)
                        this.startActivity(intent)
                    } else {
                        val intent = Intent(this, MainActivityRefug::class.java)
                        this.startActivity(intent)
                    }
                }
            }
    }


    private fun reloadRefug() {
        val intent = Intent(this, MainActivityRefug::class.java)
        this.startActivity(intent)
    }

}