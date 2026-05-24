import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}