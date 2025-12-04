using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Data;
using System;

namespace scarDesktop.Converters
{
    public class IntToVisibilityConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, string language)
        {
            if (value is int intValue)
            {
                
                bool isInverse = parameter?.ToString() == "Inverse";

                if (isInverse)
                {
                    return intValue == 0 ? Visibility.Visible : Visibility.Collapsed;
                }
                else
                {
                    return intValue > 0 ? Visibility.Visible : Visibility.Collapsed;
                }
            }
            return Visibility.Collapsed;
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            throw new NotImplementedException();
        }
    }
}
