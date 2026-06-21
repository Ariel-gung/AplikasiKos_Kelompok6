package com.example.aplikasikos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

/**
 * MainActivity yang menghubungkan desain layout XML dengan logika PBO Kotlin.
 * Mengontrol alur Login, validasi email, Dashboard, serta simulasi pemesanan kamar.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tampilkan layar login saat aplikasi pertama kali dibuka
        showLoginScreen()
    }

    /**
     * Memuat layout login XML dan menangani aksi klik login beserta validasi PBO.
     */
    private fun showLoginScreen() {
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener {
            val emailInput = etEmail.text.toString().trim()
            val passwordInput = etPassword.text.toString().trim()

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                // Memanfaatkan Class User dari DataModels.kt (Penerapan PBO Enkapsulasi)
                // Jika format email salah, constructor User akan menolak (throw IllegalArgumentException)
                val user = User(
                    userId = "USR-04231012",
                    nama = "Ariel Athaillah",
                    email = emailInput,
                    role = "Pencari"
                )
                
                user.login()
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                
                // Buka dashboard utama jika login sukses
                showDashboardScreen(user)
            } catch (e: IllegalArgumentException) {
                // Validasi gagal (misal email tidak mengandung @)
                Toast.makeText(this, "Error Validasi: ${e.message}", Toast.LENGTH_LONG).show()
                etEmail.error = e.message
            }
        }
    }

    /**
     * Memuat layout beranda XML dan menampilkan data kamar secara dinamis.
     */
    private fun showDashboardScreen(user: User) {
        setContentView(R.layout.activity_main)

        val tvUserName = findViewById<TextView>(R.id.tv_user_name)
        val tvRoleBadge = findViewById<TextView>(R.id.tv_role_badge)
        
        // Tampilkan profil user hasil login
        tvUserName.text = user.nama
        tvRoleBadge.text = "Role: ${user.role} Kos"

        // Instansiasi objek Kamar berdasarkan Class Kamar (PBO)
        val kamar1 = Kamar("KMR-102", "102", "Deluxe", 1500000, tersedia = true)
        val kamar2 = Kamar("KMR-105", "105", "Standar", 850000, tersedia = true)

        val btnPesan1 = findViewById<Button>(R.id.btn_pesan_kamar_1)
        val btnPesan2 = findViewById<Button>(R.id.btn_pesan_kamar_2)

        btnPesan1.setOnClickListener {
            pesanKamar(user, kamar1)
        }

        btnPesan2.setOnClickListener {
            pesanKamar(user, kamar2)
        }
    }

    /**
     * Menjalankan simulasi transaksi pemesanan menggunakan Class Pemesanan (PBO).
     */
    private fun pesanKamar(user: User, kamar: Kamar) {
        if (!kamar.cekKetersediaan()) {
            Toast.makeText(this, "Kamar ${kamar.nomorKamar} sudah habis terpesan!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Instansiasi Pemesanan dengan durasi 1 bulan dan harga total otomatis (PBO)
            val pemesanan = Pemesanan(
                pemesananId = "PMN-${System.currentTimeMillis().toString().takeLast(5)}",
                userId = user.userId,
                kamarId = kamar.kamarId,
                tanggalMulai = "21-06-2026",
                durasiBulan = 1,
                totalHarga = kamar.harga
            )

            // Ubah status ketersediaan kamar
            kamar.updateStatusKetersediaan(false)
            
            // Proses transaksi alur OOP (PENDING -> APPROVED -> PAID)
            pemesanan.setujuiPemesanan()
            pemesanan.bayarPemesanan()

            val infoText = "Pemesanan Kamar ${kamar.nomorKamar} Berhasil!\nStatus Pembayaran: ${pemesanan.status}\nTotal Harga: Rp ${pemesanan.totalHarga}"
            Toast.makeText(this, infoText, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Pemesanan Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
