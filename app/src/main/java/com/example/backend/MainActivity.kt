package com.example.backend

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend.databinding.ActivityLoginBinding
import com.example.backend.databinding.ActivityRegisterBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var registerBinding: ActivityRegisterBinding
    private var currentStatusTextView: TextView? = null
    
    private val handler = Handler(Looper.getMainLooper())
    private val connectionCheckRunnable = object : Runnable {
        override fun run() {
            currentStatusTextView?.let { checkServerConnection(it) }
            handler.postDelayed(this, 5000) // Revisa cada 5 segundos
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLogin()
        handler.post(connectionCheckRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(connectionCheckRunnable)
    }

    private fun checkServerConnection(statusTextView: TextView) {
        RetrofitClient.getClient().create(ApiService::class.java)
            .checkApi().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        statusTextView.text = "Conexión con el Servidor Establecida"
                        statusTextView.setTextColor(Color.parseColor("#4CAF50"))
                    } else {
                        statusTextView.text = "Error de respuesta del servidor"
                        statusTextView.setTextColor(Color.RED)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    statusTextView.text = "Sin conexión con el servidor"
                    statusTextView.setTextColor(Color.RED)
                }
            })
    }

    private fun showLogin() {
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        currentStatusTextView = loginBinding.tvConnectionStatus
        
        // Verificación inmediata al cambiar de pantalla
        currentStatusTextView?.let { checkServerConnection(it) }

        loginBinding.btnLogin.setOnClickListener {
            val username = loginBinding.etUsername.text.toString()
            val password = loginBinding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por Favor Llena Todos los Campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(username, password)
            RetrofitClient.getClient().create(ApiService::class.java)
                .login(user).enqueue(object : Callback<ResponseMessage> {
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        val msg = if (response.isSuccessful) {
                            response.body()?.message ?: "Éxito"
                        } else {
                            "Usuaio y/o Contraseña Incorrectos. Intentalo de Nuevo."
                        }
                        loginBinding.tvStatus.text = msg
                    }
                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        loginBinding.tvStatus.text = "Problemas de Conexión al Servidor.\nError: ${t.message}"
                    }
                })
        }

        loginBinding.tvGoToRegister.setOnClickListener {
            showRegister()
        }
    }

    private fun showRegister() {
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        currentStatusTextView = registerBinding.tvConnectionStatus

        // Verificación inmediata al cambiar de pantalla
        currentStatusTextView?.let { checkServerConnection(it) }

        registerBinding.btnRegister.setOnClickListener {
            val username = registerBinding.etUsername.text.toString()
            val password = registerBinding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por Favor Llena Todos los Campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(username, password)
            RetrofitClient.getClient().create(ApiService::class.java)
                .register(user).enqueue(object : Callback<ResponseMessage> {
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        val msg = if (response.isSuccessful) {
                            response.body()?.message ?: "Registrado"
                        } else {
                            "Registro Fallido."
                        }
                        registerBinding.tvStatus.text = msg
                    }
                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        registerBinding.tvStatus.text = "Problemas de Conexión al Servidor.\nError: ${t.message}"
                    }
                })
        }

        registerBinding.tvGoToLogin.setOnClickListener {
            showLogin()
        }
    }
}
