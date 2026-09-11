package nl.reisblik.htmlfotolezer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int PICK_FOLDER = 42;
    private Uri treeUri;
    private WebView webView;
    private LinearLayout list;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        Button choose = new Button(this);
        choose.setText("Kies map met HTML + foto's");
        root.addView(choose);

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));

        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setVisibility(View.GONE);
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);

        choose.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                       Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                       Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            startActivityForResult(i, PICK_FOLDER);
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FOLDER && resultCode == RESULT_OK && data != null) {
            treeUri = data.getData();
            try {
                getContentResolver().takePersistableUriPermission(
                    treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {}
            showHtmlFiles();
        }
    }

    private void showHtmlFiles() {
        list.removeAllViews();
        list.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        Uri children = DocumentsContract.buildChildDocumentsUriUsingTree(
            treeUri, DocumentsContract.getTreeDocumentId(treeUri));

        try {
            android.database.Cursor c = getContentResolver().query(
                children,
                new String[] {
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_MIME_TYPE
                },
                null, null, null);

            if (c != null) {
                while (c.moveToNext()) {
                    String id = c.getString(0);
                    String name = c.getString(1);
                    String mime = c.getString(2);

                    if (name.toLowerCase().endsWith(".html") ||
                        name.toLowerCase().endsWith(".htm") ||
                        "text/html".equals(mime)) {
                        Button b = new Button(this);
                        b.setText(name);
                        b.setOnClickListener(v -> openHtml(id));
                        list.addView(b);
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Map kon niet worden gelezen.", Toast.LENGTH_LONG).show();
        }

        if (list.getChildCount() == 0) {
            TextView t = new TextView(this);
            t.setText("Geen HTML-bestanden gevonden in deze map.");
            t.setPadding(24, 24, 24, 24);
            list.addView(t);
        }
    }

    private void openHtml(String documentId) {
        try {
            Uri fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId);
            InputStream in = getContentResolver().openInputStream(fileUri);
            String html = new String(readAll(in), StandardCharsets.UTF_8);
            in.close();

            list.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadDataWithBaseURL(
                "content://html-fotolezer/",
                html,
                "text/html",
                "UTF-8",
                null
            );
        } catch (Exception e) {
            Toast.makeText(this, "HTML-bestand kon niet worden geopend.", Toast.LENGTH_LONG).show();
        }
    }

    private byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
        return out.toByteArray();
    }
}
