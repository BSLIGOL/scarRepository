using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using scarDesktop.ViewModels;
using System;

namespace scarDesktop.Views
{
    public sealed partial class CreateStudyPage : Page
    {
        public CreateStudyViewModel ViewModel => (CreateStudyViewModel)DataContext;

        public CreateStudyPage()
        {
            this.InitializeComponent();
            ViewModel.StudyCreated += ViewModel_StudyCreated;
        }

        private void ViewModel_StudyCreated(object sender, EventArgs e)
        {
            
            if (Frame.CanGoBack)
            {
                Frame.GoBack();
            }
            else
            {
                Frame.Navigate(typeof(StudyListPage));
            }
        }

        private void CancelButton_Click(object sender, RoutedEventArgs e)
        {
            if (Frame.CanGoBack)
            {
                Frame.GoBack();
            }
            else
            {
                Frame.Navigate(typeof(StudyListPage));
            }
        }
    }
}
