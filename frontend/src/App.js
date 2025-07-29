import React, { useState, useEffect } from "react";
import "./App.css";

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [items, setItems] = useState([]);
  const [newItem, setNewItem] = useState("");

  const login = async () => {
    try {
      const res = await fetch("http://localhost:3001/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });
      if (res.ok) {
        setLoggedIn(true);
      } else {
        alert("Login failed");
      }
    } catch (error) {
      console.error("Login error:", error);
      alert("Login failed");
    }
  };

  const fetchItems = async () => {
    try {
      const res = await fetch("http://localhost:3001/items");
      if (res.ok) {
        const data = await res.json();
        setItems(data);
      }
    } catch (error) {
      console.error("Failed to fetch items:", error);
    }
  };

  const addItem = async () => {
    if (!newItem.trim()) return;
    
    try {
      const res = await fetch("http://localhost:3001/items", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: newItem })
      });
      if (res.ok) {
        setNewItem("");
        await fetchItems(); // Refresh the list
      }
    } catch (error) {
      console.error("Failed to add item:", error);
    }
  };

  const editItem = async (id, name) => {
    const newName = prompt("Edit item:", name);
    if (newName && newName.trim()) {
      try {
        const res = await fetch(`http://localhost:3001/items/${id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ name: newName })
        });
        if (res.ok) {
          await fetchItems(); // Refresh the list
        }
      } catch (error) {
        console.error("Failed to edit item:", error);
      }
    }
  };

  const deleteItem = async (id) => {
    try {
      const res = await fetch(`http://localhost:3001/items/${id}`, { 
        method: "DELETE" 
      });
      if (res.ok) {
        await fetchItems(); // Refresh the list
      }
    } catch (error) {
      console.error("Failed to delete item:", error);
    }
  };

  // Handle Enter key press for login
  const handleLoginKeyPress = (e) => {
    if (e.key === 'Enter') {
      login();
    }
  };

  // Handle Enter key press for adding items
  const handleAddItemKeyPress = (e) => {
    if (e.key === 'Enter') {
      addItem();
    }
  };

  useEffect(() => {
    if (loggedIn) {
      fetchItems();
    }
  }, [loggedIn]);

  return (
    <div className="App">
      {!loggedIn ? (
        <div>
          <h2>Login</h2>
          <input 
            placeholder="Username" 
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            onKeyPress={handleLoginKeyPress}
          />
          <input 
            placeholder="Password" 
            type="password" 
            value={password}
            onChange={(e) => setPassword(e.target.value)} 
            onKeyPress={handleLoginKeyPress}
          />
          <button onClick={login}>Login</button>
        </div>
      ) : (
        <div>
          <h2>Todo List</h2>
          <input 
            value={newItem} 
            onChange={(e) => setNewItem(e.target.value)} 
            placeholder="New item"
            onKeyPress={handleAddItemKeyPress}
          />
          <button onClick={addItem}>Add</button>
          <ul>
            {items.map((item) => (
              <li key={item.id}>
                {item.name}
                <button onClick={() => editItem(item.id, item.name)}>Edit</button>
                <button onClick={() => deleteItem(item.id)}>Delete</button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default App;