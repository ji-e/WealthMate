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

    init() {
        MainViewControllerKt.debugBuild()
        KoinInitializerKt.doInitKoin()
        FirebaseApp.configure()

        // 조용한 로그인(Silent Sign-In) 프로바이더 등록
        setupSilentSignInProvider()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    if !GIDSignIn.sharedInstance.handle(url) {
                    }
                }
        }
    }

    private func setupSilentSignInProvider() {
        // ComposeApp 프레임워크 내에 shared 모듈이 export 되어 있으므로 바로 접근 가능합니다.
        IosSilentSignInProvider.shared.setProvider { onComplete in
            GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
                if let error = error {
                    print("iOS Silent Sign-In error: \(error.localizedDescription)")
                    onComplete(nil)
                } else if let user = user {
                    let auth = GoogleAuthEntity(
                        accessToken: user.accessToken.tokenString,
                        expiresIn: 3600,
                        refreshToken: user.refreshToken.tokenString
                    )
                    onComplete(auth)
                } else {
                    onComplete(nil)
                }
            }
        }
    }
}
