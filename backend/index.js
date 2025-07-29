const express = require("express");
const cors = require("cors");

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

let items = [];
let currentId = 1;

app.post("/login", (req, res) => {
  const { username, password } = req.body;
  if (username === "test" && password === "test123") {
    res.status(200).json({ message: "Login successful" });
  } else {
    res.status(401).json({ message: "Invalid credentials" });
  }
});

app.get("/items", (req, res) => {
  res.json(items);
});

app.post("/items", (req, res) => {
  const { name } = req.body;
  if (!name) {
    return res.status(400).json({ message: "Name is required" });
  }
  const newItem = { id: currentId++, name };
  items.push(newItem);
  res.status(201).json(newItem);
});

app.put("/items/:id", (req, res) => {
  const { id } = req.params;
  const { name } = req.body;
  const item = items.find((item) => item.id == id);
  if (item) {
    item.name = name;
    res.json(item);
  } else {
    res.status(404).json({ message: "Item not found" });
  }
});

app.delete("/items/:id", (req, res) => {
  const { id } = req.params;
  const initialLength = items.length;
  items = items.filter((item) => item.id != id);
  if (items.length === initialLength) {
    res.status(404).json({ message: "Item not found" });
  } else {
    res.status(204).send();
  }
});

// Health check endpoint
app.get("/health", (req, res) => {
  res.status(200).json({ status: "OK", timestamp: new Date().toISOString() });
});

// Only start server if not being required as module
if (require.main === module) {
  app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
}

module.exports = app;