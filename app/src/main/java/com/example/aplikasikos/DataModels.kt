package com.example.aplikasikos

import java.util.regex.Pattern

/**
 * Representasi data User (Pencari Kos atau Pemilik Kos).
 * Menerapkan enkapsulasi untuk memvalidasi format email.
 */
class User(
    val userId: String,
    var nama: String,
    email: String,
    val role: String // "Pencari" atau "Pemilik"
) {
    // Properti email dengan custom setter untuk validasi format email
    var email: String = email
        set(value) {
            require(isValidEmail(value)) { "Format email tidak valid: $value" }
            field = value
        }

    init {
        // Validasi awal saat objek pertama kali dibuat
        require(nama.isNotBlank()) { "Nama tidak boleh kosong" }
        require(role == "Pencari" || role == "Pemilik") { "Role harus 'Pencari' atau 'Pemilik'" }
        require(isValidEmail(email)) { "Format email awal tidak valid: $email" }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        val pattern = Pattern.compile(emailRegex)
        return pattern.matcher(email).matches()
    }

    fun login(): Boolean {
        println("User $nama ($role) berhasil login.")
        return true
    }

    fun updateProfile(namaBaru: String, emailBaru: String) {
        this.nama = namaBaru
        this.email = emailBaru
        println("Profil user $userId berhasil diperbarui.")
    }
}

/**
 * Representasi data Kamar Kos.
 * Menerapkan enkapsulasi dengan memproteksi set harga dan status ketersediaan.
 */
class Kamar(
    val kamarId: String,
    val nomorKamar: String,
    val tipeKamar: String,
    harga: Int,
    tersedia: Boolean = true
) {
    // Harga kamar diproteksi dengan private set dan custom setter untuk validasi non-negatif
    var harga: Int = harga
        private set(value) {
            require(value >= 0) { "Harga kamar tidak boleh negatif" }
            field = value
        }

    // Status ketersediaan kamar diproteksi dengan private set
    var tersedia: Boolean = tersedia
        private set

    init {
        require(nomorKamar.isNotBlank()) { "Nomor kamar tidak boleh kosong" }
        require(harga >= 0) { "Harga awal kamar tidak boleh negatif" }
    }

    // Method publik untuk mengubah harga kamar dengan aman (ter-enkapsulasi)
    fun updateHarga(hargaBaru: Int) {
        this.harga = hargaBaru
        println("Harga kamar $nomorKamar berhasil diubah menjadi Rp $hargaBaru")
    }

    // Method publik untuk mengubah status ketersediaan kamar
    fun updateStatusKetersediaan(statusBaru: Boolean) {
        this.tersedia = statusBaru
        val statusText = if (statusBaru) "Tersedia" else "Sudah Terisi/Tidak Tersedia"
        println("Status ketersediaan kamar $nomorKamar diubah menjadi: $statusText")
    }

    fun cekKetersediaan(): Boolean {
        return tersedia
    }
}

/**
 * Representasi data Pemesanan Kos.
 * Menerapkan enkapsulasi penuh untuk melacak status pemesanan.
 */
class Pemesanan(
    val pemesananId: String,
    val userId: String,
    val kamarId: String,
    val tanggalMulai: String,
    durasiBulan: Int,
    totalHarga: Int
) {
    // Durasi pemesanan dilindungi oleh validasi (minimal 1 bulan)
    var durasiBulan: Int = durasiBulan
        private set(value) {
            require(value >= 1) { "Durasi pemesanan minimal 1 bulan" }
            field = value
        }

    // Total harga pemesanan dilindungi oleh validasi non-negatif
    var totalHarga: Int = totalHarga
        private set(value) {
            require(value >= 0) { "Total harga tidak boleh negatif" }
            field = value
        }

    // Status pemesanan diproteksi penuh (hanya bisa diubah melalui alur method spesifik)
    var status: String = "PENDING"
        private set

    init {
        require(durasiBulan >= 1) { "Durasi pemesanan awal minimal 1 bulan" }
        require(totalHarga >= 0) { "Total harga awal tidak boleh negatif" }
    }

    // Method untuk menyetujui pemesanan (mengubah status menjadi APPROVED)
    fun setujuiPemesanan() {
        if (this.status == "PENDING") {
            this.status = "APPROVED"
            println("Pemesanan $pemesananId disetujui.")
        } else {
            println("Pemesanan $pemesananId tidak dapat disetujui karena status saat ini: $status")
        }
    }

    // Method untuk membatalkan pemesanan (mengubah status menjadi CANCELLED)
    fun batalkanPemesanan() {
        if (this.status == "PENDING" || this.status == "APPROVED") {
            this.status = "CANCELLED"
            println("Pemesanan $pemesananId telah dibatalkan.")
        } else {
            println("Pemesanan $pemesananId tidak dapat dibatalkan karena sudah dalam status: $status")
        }
    }

    // Method untuk melakukan pembayaran pemesanan
    fun bayarPemesanan() {
        if (this.status == "APPROVED") {
            this.status = "PAID"
            println("Pemesanan $pemesananId berhasil dibayar.")
        } else {
            println("Pembayaran gagal. Pemesanan harus disetujui terlebih dahulu. Status saat ini: $status")
        }
    }
}
