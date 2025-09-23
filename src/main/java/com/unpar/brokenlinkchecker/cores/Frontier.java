package com.unpar.brokenlinkchecker.cores;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Frontier adalah struktur data buat nyimpen daftar URL
 * yang bakal dicrawl berikutnya.
 *
 * Implementasi sekarang pakai queue (FIFO).
 * Jadi link yang masuk duluan akan diproses duluan.
 * Ini artinya pola crawling masih BFS (Breadth-First).
 */
public class Frontier {

    // Deque dipakai sebagai queue sederhana untuk simpan URL
    private final Deque<String> urls = new ArrayDeque<>();


    /**
     * Tambahin URL baru ke frontier.
     * URL akan ditaruh di belakang antrian (addLast).
     */
    public void add(String url) {
        this.urls.addLast(url);
    }

    /**
     * Ambil URL berikutnya dari frontier.
     * - Kalau queue kosong maka return null.
     * - Kalau ada maka ambil elemen pertama (pollFirst).
     *   Artinya, URL paling awal masuk akan diproses duluan.
     */
    public String next() {
        if (urls.isEmpty()) return null;
        return this.urls.pollFirst(); // FIFO
    }

    /**
     * Cek apakah frontier kosong.
     * Return true kalau nggak ada URL lagi yang bisa dicrawl.
     */
    public boolean isEmpty() {
        return urls.isEmpty();
    }
}
