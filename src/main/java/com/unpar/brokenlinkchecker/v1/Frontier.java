package com.unpar.brokenlinkchecker.v1;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Frontier adalah struktur data buat nyimpan daftar URL
 * yang bakal dicrawl berikutnya.
 *
 * Implementasi sekarang pakai queue (FIFO).
 * Jadi link yang masuk duluan akan diproses duluan.
 */
public class Frontier {

    // Queue FIFO, implementasi ArrayDeque
    private final Queue<String> urls = new ArrayDeque<>();

    /**
     * Tambahin URL baru ke frontier.
     * URL akan ditaruh di belakang antrian (offer).
     */
    public void enqueue(String url) {
        this.urls.offer(url);
    }

    /**
     * Ambil URL berikutnya dari frontier.
     * - Kalau queue kosong maka return null.
     * - Kalau ada maka ambil elemen pertama (poll).
     *   Artinya, URL paling awal masuk akan diproses duluan.
     */
    public String dequeue() {
        return this.urls.poll(); // FIFO
    }

    /**
     * Cek apakah frontier kosong.
     * Return true kalau nggak ada URL lagi yang bisa dicrawl.
     */
    public boolean isEmpty() {
        return urls.isEmpty();
    }
}
