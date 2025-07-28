
# Test Strategy Document

## What is Being Tested
- A React frontend that interacts with a Node.js API backend to manage login and items.

## Test Coverage
- UI Automation: Login, Create, Edit, Delete, and Assert items
- API Automation: POST /login, GET/POST/PUT/DELETE /items

## Tools Used
- Selenium (Java) for UI testing
- Rest-assured (Java) for API testing

## How to Run
- Ensure backend and frontend are running (see README)
- Use Maven to run API/UI tests

## Assumptions
- Only one user (test/test123)
- No real authentication mechanism
- Stateless backend in-memory storage
