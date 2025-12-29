🎬 MovieApp - Modern Android Movie Collection App
MovieApp; kullanıcıların popüler filmleri keşfedebileceği, detaylarını inceleyebileceği, kendi favori listelerini oluşturup yönetebileceği ve arama yapabileceği modern bir Android uygulamasıdır.

Uygulama TMDB (The Movie Database) API'sini kullanır ve Firebase Auth ile kullanıcı yönetimi sağlar.

🏗 Mimari ve Teknolojiler
Bu proje, Google'ın önerdiği Modern Android Development (MAD) prensiplerine sadık kalınarak geliştirilmiştir.

Mimari Yaklaşım: Clean Architecture & MVVM
Proje, sorumlulukların ayrılması (Separation of Concerns) ilkesine dayanarak 3 ana katmana(data, domain, ui) ayrılmıştır:

Data Layer: API (Retrofit), Veritabanı (Room) ve DataStore işlemlerini yönetir. Repository Pattern uygulanmıştır.
Domain Layer: Uygulamanın iş mantığını (Business Logic) içerir. Tamamen saf Kotlin kodudur ve Android framework'ünden bağımsızdır (Use Cases).
UI Layer (Presentation): Jetpack Compose kullanılarak geliştirilmiştir. MVVM (Model-View-ViewModel) deseni, State Holder (Tek Durum) yapısıyla birlikte kullanılarak UI tutarlılığı sağlanmıştır.

Kullanılan Kütüphaneler
UI: Jetpack Compose, Material3
Dependency Injection: Hilt
Network: Retrofit, OkHttp, Moshi
Database: Room (Offline Caching & Local Storage)
Async: Coroutines, Flow
Auth: Firebase Authentication
Image Loading: Coil
Navigation: Navigation Compose
Pagination: Paging3
Testing: JUnit4, MockK, Turbine, Coroutines Test



🛠 Kurulum
Projeyi yerel ortamınızda çalıştırmak için aşağıdaki 3 adımı uygulamanız yeterlidir:

1. Projeyi İndirin Repo'yu klonlayın ve Android Studio'da açın. Gradle senkronizasyonunun bitmesini bekleyin.

2. TMDB API Anahtarını Ekleyin Proje kök dizinindeki local.properties dosyasını açın (yoksa oluşturun) ve kendi TMDB API anahtarınızı şu formatta ekleyin: tmdb_api_key="BURAYA_API_KEY_GELECEK"

3. Firebase Bağlantısını Yapın Firebase konsolundan google-services.json dosyanızı indirin ve projenin app/ klasörünün içine yapıştırın. (Uygulama paket adı: com.example.movieapp)

4. Çalıştırın Artık projeyi emülatörde veya fiziksel cihazda çalıştırabilirsiniz.
