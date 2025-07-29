import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

// Mock fetch globally
global.fetch = jest.fn();

describe('App Component', () => {
  beforeEach(() => {
    fetch.mockClear();
  });

  test('renders login form initially', () => {
    render(<App />);
    expect(screen.getByPlaceholderText('Username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
    expect(screen.getByText('Login')).toBeInTheDocument();
  });

  test('handles successful login', async () => {
    fetch
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ message: 'Login successful' })
      })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => []
      });

    render(<App />);
    
    fireEvent.change(screen.getByPlaceholderText('Username'), {
      target: { value: 'test' }
    });
    fireEvent.change(screen.getByPlaceholderText('Password'), {
      target: { value: 'test123' }
    });
    
    fireEvent.click(screen.getByText('Login'));
    
    await waitFor(() => {
      expect(screen.getByText('Todo List')).toBeInTheDocument();
    });
  });

  test('handles failed login', async () => {
    // Mock alert
    global.alert = jest.fn();
    
    fetch.mockResolvedValueOnce({
      ok: false,
      status: 401
    });

    render(<App />);
    
    fireEvent.change(screen.getByPlaceholderText('Username'), {
      target: { value: 'wrong' }
    });
    fireEvent.change(screen.getByPlaceholderText('Password'), {
      target: { value: 'wrong' }
    });
    
    fireEvent.click(screen.getByText('Login'));
    
    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith('Login failed');
    });
  });
});