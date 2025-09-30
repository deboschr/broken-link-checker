package com.unpar.brokenlinkchecker;

/**
 * Status proses crawling untuk ditampilkan di UI.
 */
public enum CrawlStatus {
    IDLE("Belum mulai"),
    RUNNING("Sedang berjalan…"),
    STOPPED("Dihentikan"),
    COMPLETED("Selesai");

    private final String text;

    CrawlStatus(String text) {
        this.text = text;
    }

    /** Teks ramah pengguna untuk ditampilkan di Label progress. */
    public String getText() {
        return text;
    }
}
