import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    // ✅ Koin 초기화 코드를 여기에 추가!
    init() {
        // 1단계에서 만든 KoinInitializerKt 파일을 통해 initKoin() 함수를 호출합니다.
        // Kotlin의 top-level 함수는 {파일이름}Kt 클래스의 static 메소드로 변환됩니다.
        KoinInitializerKt.doInitKoin()
    }


    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
