# seguro
> Secure persistence using AES encryption on Android with no dependencies. 

```kotlin
 val seguro = Seguro.Builder()
    .enableEncryption(encryptKey = true, encryptValue = true)
    .setPassword("Password@123")
    .setFolderName(".${BuildConfig.APPLICATION_ID}")
    .setPersistentType(Seguro.PersistenceType.SharedPreferences(applicationContext))
    .build()
```


Made with ♥ by cesar ferreira

