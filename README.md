# HTML Foto Lezer Android 0.2

Deze app leest HTML-bestanden uit een door de gebruiker gekozen Android-map, inclusief losse foto's, CSS en JavaScript waarnaar het HTML-bestand relatief verwijst.

## Bouwen zonder Android Studio

1. Maak op GitHub een nieuw repository, bijvoorbeeld `html-fotolezer`.
2. Upload **de inhoud van deze map** naar de `main` branch (dus `app/`, `build.gradle`, `settings.gradle`, `.github/`, enz.).
3. Ga naar **Actions**. De workflow **Bouw APK** start bij een push automatisch, of kies **Run workflow**.
4. Wacht tot de workflow klaar is.
5. Open de geslaagde workflow en download het artifact **HTMLFotoLezer-debug**.
6. Pak de download uit. Daarin staat `app-debug.apk`.
7. Zet de APK op de Android-telefoon en installeer hem. Android kan eerst toestemming vragen om een app buiten de Play Store te installeren.
8. Open HTML Foto Lezer → **Kies map** → selecteer de map waarin de HTML en de foto's staan.
9. Kies de HTML-pagina.

De foto's blijven losse JPG-bestanden; ze worden niet in het HTML-bestand opgenomen.
