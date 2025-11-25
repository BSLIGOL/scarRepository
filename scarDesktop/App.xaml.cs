using Microsoft.UI.Xaml;

namespace scarDesktop
{
    public partial class App : Application
    {
        public App()
        {
            this.InitializeComponent();
            this.UnhandledException += App_UnhandledException;
        }

        private void App_UnhandledException(object sender, Microsoft.UI.Xaml.UnhandledExceptionEventArgs e)
        {
            // 예외 발생 시 메시지 출력 (디버깅용)
            System.Diagnostics.Debug.WriteLine($"Unhandled Exception: {e.Message}");
            e.Handled = true; // 앱 종료 방지
        }

        protected override void OnLaunched(Microsoft.UI.Xaml.LaunchActivatedEventArgs args)
        {
            m_window = new MainWindow();
            m_window.Activate();
        }

        private Window m_window;
    }
}
