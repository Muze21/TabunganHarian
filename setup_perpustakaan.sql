-- =============================================
-- SQL SETUP: Sistem Perpustakaan
-- Jalankan di phpMyAdmin atau MySQL terminal
-- =============================================

-- 1. Buat database
CREATE DATABASE IF NOT EXISTS db_perpustakaan;

-- 2. Gunakan database
c

-- 3. Buat tabel buku
CREATE TABLE IF NOT EXISTS buku (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    judul        VARCHAR(255) NOT NULL,
    penulis      VARCHAR(150) NOT NULL,
    tahun_terbit YEAR        NOT NULL
);

-- 4. (OPSIONAL) Isi data contoh
INSERT INTO buku (judul, penulis, tahun_terbit) VALUES
('Laskar Pelangi',          'Andrea Hirata',      2005),
('Bumi Manusia',            'Pramoedya Ananta Toer', 1980),
('Negeri 5 Menara',         'Ahmad Fuadi',         2009),
('Harry Potter',            'J.K. Rowling',        1997),
('The Alchemist',           'Paulo Coelho',        1988);
