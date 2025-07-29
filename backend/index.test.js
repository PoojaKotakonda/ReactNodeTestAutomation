const request = require('supertest');
const app = require('./index');

describe('Backend API Tests', () => {
  beforeEach(() => {
    // Reset items array before each test
    require('./index');
  });

  describe('POST /login', () => {
    test('should return 200 for valid credentials', async () => {
      const response = await request(app)
        .post('/login')
        .send({ username: 'test', password: 'test123' });
      
      expect(response.status).toBe(200);
      expect(response.body.message).toBe('Login successful');
    });

    test('should return 401 for invalid credentials', async () => {
      const response = await request(app)
        .post('/login')
        .send({ username: 'wrong', password: 'wrong' });
      
      expect(response.status).toBe(401);
      expect(response.body.message).toBe('Invalid credentials');
    });

    test('should handle missing credentials', async () => {
      const response = await request(app)
        .post('/login')
        .send({});
      
      expect(response.status).toBe(401);
    });
  });

  describe('Items API', () => {
    test('should get empty items array initially', async () => {
      const response = await request(app).get('/items');
      expect(response.status).toBe(200);
      expect(response.body).toEqual([]);
    });

    test('should create and retrieve items', async () => {
      // Create item
      const createResponse = await request(app)
        .post('/items')
        .send({ name: 'Test Item' });
      
      expect(createResponse.status).toBe(201);
      expect(createResponse.body.name).toBe('Test Item');
      expect(createResponse.body.id).toBe(1);
      
      // Get items
      const getResponse = await request(app).get('/items');
      expect(getResponse.status).toBe(200);
      expect(getResponse.body).toHaveLength(1);
      expect(getResponse.body[0].name).toBe('Test Item');
    });

    test('should reject item creation without name', async () => {
      const response = await request(app)
        .post('/items')
        .send({});
      
      expect(response.status).toBe(400);
      expect(response.body.message).toBe('Name is required');
    });

    test('should update existing item', async () => {
      // Create item first
      const createResponse = await request(app)
        .post('/items')
        .send({ name: 'Original Item' });
      
      const itemId = createResponse.body.id;
      
      // Update item
      const updateResponse = await request(app)
        .put(`/items/${itemId}`)
        .send({ name: 'Updated Item' });
      
      expect(updateResponse.status).toBe(200);
      expect(updateResponse.body.name).toBe('Updated Item');
      expect(updateResponse.body.id).toBe(itemId);
    });

    test('should return 404 for updating non-existent item', async () => {
      const response = await request(app)
        .put('/items/999')
        .send({ name: 'Updated Item' });
      
      expect(response.status).toBe(404);
      expect(response.body.message).toBe('Item not found');
    });

    test('should delete existing item', async () => {
      // Create item first
      const createResponse = await request(app)
        .post('/items')
        .send({ name: 'Item to Delete' });
      
      const itemId = createResponse.body.id;
      
      // Delete item
      const deleteResponse = await request(app)
        .delete(`/items/${itemId}`);
      
      expect(deleteResponse.status).toBe(204);
      
      // Verify item is deleted
      const getResponse = await request(app).get('/items');
      expect(getResponse.body).toHaveLength(0);
    });

    test('should return 404 for deleting non-existent item', async () => {
      const response = await request(app)
        .delete('/items/999');
      
      expect(response.status).toBe(404);
      expect(response.body.message).toBe('Item not found');
    });
  });

  describe('Health Check', () => {
    test('should return health status', async () => {
      const response = await request(app).get('/health');
      expect(response.status).toBe(200);
      expect(response.body.status).toBe('OK');
      expect(response.body.timestamp).toBeDefined();
    });
  });
});