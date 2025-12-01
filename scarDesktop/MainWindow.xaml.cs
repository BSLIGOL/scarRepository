using Microsoft.UI.Xaml;
using scarDesktop.Views;

namespace scarDesktop
{
    public sealed partial class MainWindow : Window
    {
        public MainWindow()
        {
            this.InitializeComponent();
            ContentFrame.Navigate(typeof(LoginPage));
        }
    }
}
