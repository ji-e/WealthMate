import SwiftUI
import ComposeApp
import GoogleSignIn
import Firebase
import FirebaseMessaging
import UserNotifications

// 1. 반드시 클래스 선언이 struct iOSApp 밖에 있어야 합니다.
class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()


        let clientID = Bundle.main.infoDictionary?["GIDClientID"] as? String ?? ""
        let serverClientID = Bundle.main.infoDictionary?["GIDServerClientID"] as? String ?? ""

        if !clientID.isEmpty {
            GIDSignIn.sharedInstance.configuration = GIDConfiguration(
                clientID: clientID,
                serverClientID: serverClientID.isEmpty ? nil : serverClientID
            )
        } else {
            print("ERROR: GIDClientID is empty. Check your Info.plist.")
        }


        // Messaging delegate 설정
        Messaging.messaging().delegate = self
        
        // 알림 센터 delegate 설정
        UNUserNotificationCenter.current().delegate = self
        
        // 원격 알림 등록
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { _, _ in }
        
        application.registerForRemoteNotifications()
        
        return true
    }

    func application(
        _ app: UIApplication,
        open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        // 구글 로그인 인증 결과 핸들링
        return GIDSignIn.sharedInstance.handle(url)
    }
    
    // APNS 토큰을 Firebase에 전달
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
    
    // FCM 등록 토큰 수신
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("Firebase registration token: \(String(describing: fcmToken))")
        // 필요한 경우 여기서 서버로 토큰을 전송하거나 앱 내에서 활용할 수 있습니다.
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        MainViewControllerKt.debugBuild()
        KoinInitializerKt.doInitKoin()
        // FirebaseApp.configure() // AppDelegate의 didFinishLaunchingWithOptions에서 호출하도록 변경함

        // 조용한 로그인(Silent Sign-In) 프로바이더 등록
        setupSilentSignInProvider()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    print("✅ onOpenURL called: \(url)") // 이게 찍히는지 확인
                    if !GIDSignIn.sharedInstance.handle(url) {
                        print("❌ GIDSignIn did not handle this URL")
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
