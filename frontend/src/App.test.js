import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

// Mock fetch globally
global.fetch = jest.fn();

describe('App Component', () => {
  beforeEach(() => {
    fetch.mockClear();
    // Reset any global mocks
    jest.clearAllMocks();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('renders login form initially', async () => {
    await act(async () => {
      render(<App />);
    });
    
    // Check for form elements
    expect(screen.getByPlaceholderText('Username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
    
    // Use more specific selector for the button
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    
    // Check for heading
    expect(screen.getByRole('heading', { name: /login/i })).toBeInTheDocument();
  });

  test('handles successful login', async () => {
    // Mock successful login
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ message: 'Login successful' })
    });
    
    // Mock items fetch (called after successful login)
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => []
    });

    await act(async () => {
      render(<App />);
    });
    
    // Fill in the form
    const usernameInput = screen.getByPlaceholderText('Username');
    const passwordInput = screen.getByPlaceholderText('Password');
    const loginButton = screen.getByRole('button', { name: /login/i });
    
    await act(async () => {
      fireEvent.change(usernameInput, {
        target: { value: 'test' }
      });
      fireEvent.change(passwordInput, {
        target: { value: 'test123' }
      });
    });
    
    await act(async () => {
      fireEvent.click(loginButton);
    });
    
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

    await act(async () => {
      render(<App />);
    });
    
    // Fill in the form with wrong credentials
    const usernameInput = screen.getByPlaceholderText('Username');
    const passwordInput = screen.getByPlaceholderText('Password');
    const loginButton = screen.getByRole('button', { name: /login/i });
    
    await act(async () => {
      fireEvent.change(usernameInput, {
        target: { value: 'wrong' }
      });
      fireEvent.change(passwordInput, {
        target: { value: 'wrong' }
      });
    });
    
    await act(async () => {
      fireEvent.click(loginButton);
    });
    
    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith('Login failed');
    });
    
    // Verify we're still on login page
    expect(screen.getByPlaceholderText('Username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
  });

  test('displays todo list interface after login', async () => {
    // Mock successful login
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ message: 'Login successful' })
    });
    
    // Mock items fetch with actual items
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => [
        { id: 1, name: 'Test Todo Item' }
      ]
    });

    await act(async () => {
      render(<App />);
    });
    
    // Login first
    await act(async () => {
      fireEvent.change(screen.getByPlaceholderText('Username'), {
        target: { value: 'test' }
      });
      fireEvent.change(screen.getByPlaceholderText('Password'), {
        target: { value: 'test123' }
      });
    });
    
    await act(async () => {
      fireEvent.click(screen.getByRole('button', { name: /login/i }));
    });
    
    // Wait for todo interface to appear
    await waitFor(() => {
      expect(screen.getByText('Todo List')).toBeInTheDocument();
    });
    
    // Check for todo interface elements
    expect(screen.getByPlaceholderText('New item')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /add/i })).toBeInTheDocument();
    
    // Wait for the todo item to appear
    await waitFor(() => {
      expect(screen.getByText('Test Todo Item')).toBeInTheDocument();
    });
  });

  test('handles network error during login', async () => {
    // Mock alert
    global.alert = jest.fn();
    
    // Mock fetch to throw network error
    fetch.mockRejectedValueOnce(new Error('Network error'));

    await act(async () => {
      render(<App />);
    });
    
    await act(async () => {
      fireEvent.change(screen.getByPlaceholderText('Username'), {
        target: { value: 'test' }
      });
      fireEvent.change(screen.getByPlaceholderText('Password'), {
        target: { value: 'test123' }
      });
    });
    
    await act(async () => {
      fireEvent.click(screen.getByRole('button', { name: /login/i }));
    });
    
    // Should handle the error gracefully
    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith('Login failed');
    });
  });

  test('can add new todo item', async () => {
    // Mock successful login
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ message: 'Login successful' })
    });
    
    // Mock initial empty items fetch
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => []
    });

    await act(async () => {
      render(<App />);
    });
    
    // Login first
    await act(async () => {
      fireEvent.change(screen.getByPlaceholderText('Username'), {
        target: { value: 'test' }
      });
      fireEvent.change(screen.getByPlaceholderText('Password'), {
        target: { value: 'test123' }
      });
      fireEvent.click(screen.getByRole('button', { name: /login/i }));
    });
    
    await waitFor(() => {
      expect(screen.getByText('Todo List')).toBeInTheDocument();
    });
    
    // Mock add item request
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ id: 1, name: 'New Test Item' })
    });
    
    // Mock fetch items after adding
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => [
        { id: 1, name: 'New Test Item' }
      ]
    });
    
    // Add new item
    const newItemInput = screen.getByPlaceholderText('New item');
    const addButton = screen.getByRole('button', { name: /add/i });
    
    await act(async () => {
      fireEvent.change(newItemInput, {
        target: { value: 'New Test Item' }
      });
      fireEvent.click(addButton);
    });
    
    // Wait for item to appear
    await waitFor(() => {
      expect(screen.getByText('New Test Item')).toBeInTheDocument();
    });
  });
});