import SwiftUI
import ComposeApp
import GoogleSignIn
import Firebase

// 1. 반드시 클래스 선언이 struct iOSApp 밖에 있어야 합니다.
class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ app: UIApplication,
        open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        // 구글 로그인 인증 결과 핸들링
        return GIDSignIn.sharedInstance.handle(url)
    }
}
@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    // ✅ Koin 초기화 코드를 여기에 추가!
    init() {
        // 1단계에서 만든 KoinInitializerKt 파일을 통해 initKoin() 함수를 호출합니다.
        // Kotlin의 top-level 함수는 {파일이름}Kt 클래스의 static 메소드로 변환됩니다.
        KoinInitializerKt.doInitKoin()
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // 구글 로그인이 처리하지 못한 경우에만 다른 처리를 하도록 구성
                    if !GIDSignIn.sharedInstance.handle(url) {
                        // 다른 커스텀 URL 스킴 처리 로직이 있다면 여기에 추가
                    }
                }
        }
    }
}
