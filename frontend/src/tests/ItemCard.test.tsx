import React from 'react';
import { render } from '@testing-library/react-native';
import ItemCard from '../components/ItemCard';

// Mock the react-native-gesture-handler dependency
jest.mock('react-native-gesture-handler', () => {
  const View = require('react-native/Libraries/Components/View/View');
  return {
    RectButton: View,
  };
});

// Mock the navigation hook if needed, though not strictly necessary for this component
// jest.mock('@react-navigation/native', () => ({
//   useNavigation: () => ({ navigate: jest.fn() }),
// }));

describe('ItemCard', () => {
  const mockItem = {
    id: '1',
    name: 'Milk',
    quantity: 1,
    expirationDate: '2025-12-31',
    predictedExpirationDate: '2025-12-28',
    storageLocation: 'Fridge',
  };

  it('renders item name and quantity correctly', () => {
    const { getByText } = render(<ItemCard item={mockItem} onPress={jest.fn()} />);
    expect(getByText('Milk')).toBeTruthy();
    expect(getByText('Qty: 1')).toBeTruthy();
  });

  it('renders expiration date and location correctly', () => {
    const { getByText } = render(<ItemCard item={mockItem} onPress={jest.fn()} />);
    expect(getByText('Expires: 2025-12-31')).toBeTruthy();
    expect(getByText('Location: Fridge')).toBeTruthy();
  });

  it('renders predicted expiration date when available', () => {
    const { getByText } = render(<ItemCard item={mockItem} onPress={jest.fn()} />);
    expect(getByText('AI Predicts: 2025-12-28')).toBeTruthy();
  });

  it('does not render predicted expiration date when null', () => {
    const itemWithoutPrediction = { ...mockItem, predictedExpirationDate: null };
    const { queryByText } = render(<ItemCard item={itemWithoutPrediction} onPress={jest.fn()} />);
    expect(queryByText('AI Predicts:')).toBeNull();
  });
});
