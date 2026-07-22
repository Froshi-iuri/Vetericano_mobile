package com.example.authenticationfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.authenticationfirebase.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseDatabase.getInstance().reference //constante que conecta la aplicación con la base de datos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        binding.btnLogin.setOnClickListener {
            intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun registrarUsuario() {

        val nombre = binding.etNombre.text.toString().trim()
        val cedula = binding.etCedula.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validar campos vacíos
        if (nombre.isEmpty() || cedula.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if(password.length < 6){
            Toast.makeText(this, "La contraseña debe tener más de 6 carácteres", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuarios(
            nombre = nombre,
            cedula = cedula,
            correo = correo,
            password = password
        )
        //esto de abajo valida si la cedula existe.
        //entramos a
        database.child("Usuarios").child(cedula).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // La cédula ya está registrada en la base de datos
                Toast.makeText(this, "Esta cédula ya se encuentra registrada", Toast.LENGTH_SHORT).show()
            } else {
                // La cédula es nueva, procedemos a guardar el usuario
                database.child("Usuarios").child(cedula).setValue(usuario)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
            }
        }
            .addOnFailureListener { excepcion ->
                Toast.makeText(this, "Error de conexión con la base de datos: ${excepcion.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun limpiarCampos(){
        binding.etNombre.setText("")
        binding.etCedula.setText("")
        binding.etCorreo.setText("")
        binding.etPassword.setText("")
    }
}