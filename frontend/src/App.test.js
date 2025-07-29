import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

// Mock fetch globally
global.fetch = jest.fn();

describe('App Component', () => {
  beforeEach(() => {
    fetch.mockClear();
  });

  afterEach(() => {
    // Clean up any mocks
    jest.clearAllMocks();
  });

  test('renders login form initially', () => {
    render(<App />);
    
    // Check for form elements
    expect(screen.getByPlaceholderText('Username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
    
    // Use more specific selector for the button
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    
    // Check for heading
    expect(screen.getByRole('heading', { name: /login/i })).toBeInTheDocument();
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
    
    // Fill in the form
    const usernameInput = screen.getByPlaceholderText('Username');
    const passwordInput = screen.getByPlaceholderText('Password');
    const loginButton = screen.getByRole('button', { name: /login/i });
    
    fireEvent.change(usernameInput, {
      target: { value: 'test' }
    });
    fireEvent.change(passwordInput, {
      target: { value: 'test123' }
    });
    
    fireEvent.click(loginButton);
    
    await waitFor(() => {
      expect(screen.getByText('Todo List')).toBeInTheDocument();
    });
    
    // Verify login form is no longer visible
    expect(screen.queryByPlaceholderText('Username')).not.toBeInTheDocument();
    expect(screen.queryByPlaceholderText('Password')).not.toBeInTheDocument();
  });

  test('handles failed login', async () => {
    // Mock alert
    global.alert = jest.fn();
    
    fetch.mockResolvedValueOnce({
      ok: false,
      status: 401
    });

    render(<App />);
    
    // Fill in the form with wrong credentials
    const usernameInput = screen.getByPlaceholderText('Username');
    const passwordInput = screen.getByPlaceholderText('Password');
    const loginButton = screen.getByRole('button', { name: /login/i });
    
    fireEvent.change(usernameInput, {
      target: { value: 'wrong' }
    });
    fireEvent.change(passwordInput, {
      target: { value: 'wrong' }
    });
    
    fireEvent.click(loginButton);
    
    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith('Login failed');
    });
    
    // Verify we're still on login page
    expect(screen.getByPlaceholderText('Username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
  });

  test('displays todo list interface after login', async () => {
    fetch
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ message: 'Login successful' })
      })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => [
          { id: 1, name: 'Test Todo Item' }
        ]
      });

    render(<App />);
    
    // Login first
    fireEvent.change(screen.getByPlaceholderText('Username'), {
      target: { value: 'test' }
    });
    fireEvent.change(screen.getByPlaceholderText('Password'), {
      target: { value: 'test123' }
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));
    
    // Wait for todo interface to appear
    await waitFor(() => {
      expect(screen.getByText('Todo List')).toBeInTheDocument();
    });
    
    // Check for todo interface elements
    expect(screen.getByPlaceholderText('New item')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /add/i })).toBeInTheDocument();
    expect(screen.getByText('Test Todo Item')).toBeInTheDocument();
  });

  test('handles network error during login', async () => {
    // Mock alert
    global.alert = jest.fn();
    
    // Mock fetch to throw network error
    fetch.mockRejectedValueOnce(new Error('Network error'));

    render(<App />);
    
    fireEvent.change(screen.getByPlaceholderText('Username'), {
      target: { value: 'test' }
    });
    fireEvent.change(screen.getByPlaceholderText('Password'), {
      target: { value: 'test123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: /login/i }));
    
    // Should handle the error gracefully
    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith('Login failed');
    });
  });
});