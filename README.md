# Тапсырмалар — Android қосымшасы

Практикалық жұмыс: **Android мобильді қосымшасын жобалау және әзірлеу**, тақырып №3 — *Күнделікті тапсырмалар тізімі*.

Kotlin · Jetpack Compose · Room · DataStore · Navigation Compose · MVVM

- 📄 Есеп: [ESEP.md](ESEP.md)
- 📱 Дайын APK: [release/Tapsyrmalar.apk](release/Tapsyrmalar.apk)
- 🧩 MIT App Inventor нұсқасы: [appinventor/Tapsyrmalar.aia](appinventor/Tapsyrmalar.aia)

## Мүмкіндіктері

- тапсырманы қосу, өзгерту, жою (растау диалогымен)
- орындалғанын белгілеу
- мерзімі, маңыздылығы, атауы немесе құрылған уақыты бойынша сұрыптау
- атауы мен сипаттамасы бойынша іздеу, күйі бойынша сүзгі
- статистика экраны
- бос және қате мәндерді тексеру, қазақ тіліндегі хабарламалар
- деректер Room-да, баптаулар DataStore-да сақталады

## Іске қосу

1. Android Studio-да жобаны ашыңыз (**File → Open** → осы қалта).
2. Gradle синхрондалуын күтіңіз (JDK 17).
3. Эмуляторды немесе телефонды таңдап, **Run ▶** басыңыз.

Командалық жолдан:

```bash
./gradlew testDebugUnitTest   # тесттер
./gradlew assembleDebug       # APK: app/build/outputs/apk/debug/app-debug.apk
```

## APK-ны телефонға орнату

`release/Tapsyrmalar.apk` файлын телефонға көшіріп, ашыңыз. Қажет болса, баптауларда «Белгісіз көздерден орнатуға» рұқсат беріңіз. Android 8.0 және одан жоғары нұсқалар қолдау табады.

GitHub Actions әр push кезінде тесттерді іске қосып, APK мен скриншоттарды жаңартады.
