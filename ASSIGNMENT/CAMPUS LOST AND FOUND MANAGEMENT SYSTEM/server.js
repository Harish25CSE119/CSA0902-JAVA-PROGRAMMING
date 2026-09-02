const express = require('express');
const cors = require('cors');
const mysql = require('mysql2/promise');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Database Config
const dbConfig = {
    host: 'localhost',
    port: 3306,
    user: 'root',
    password: 'admin',
    database: 'campus_lost_found'
};

let dbPool = null;
let useFallbackStore = false;

// In-Memory Fallback Data Store (if MySQL service is offline)
let mockItems = [
    {
        item_id: 1,
        item_name: 'Blue Leather Wallet',
        description: 'Contains student ID card and driver license',
        category: 'Personal Effects',
        status: 'Lost',
        location: 'Main Library 2nd Floor',
        date_reported: '2026-08-28',
        reported_by: 'Alex Johnson',
        contact: '555-0192'
    },
    {
        item_id: 2,
        item_name: 'Apple AirPods Pro',
        description: 'White wireless earbuds in a silicone case with sticker',
        category: 'Electronics',
        status: 'Found',
        location: 'Student Activity Center',
        date_reported: '2026-08-29',
        reported_by: 'Sarah Smith',
        contact: '555-0143'
    },
    {
        item_id: 3,
        item_name: 'Calculus Textbook 10th Ed',
        description: 'Hardcover math book with yellow highlighter marks',
        category: 'Books & Stationery',
        status: 'Lost',
        location: 'Science Building Room 304',
        date_reported: '2026-08-30',
        reported_by: 'David Miller',
        contact: '555-0188'
    },
    {
        item_id: 4,
        item_name: 'Stainless Water Bottle',
        description: 'Hydroflask 32oz dark blue with stickers',
        category: 'Personal Effects',
        status: 'Returned',
        location: 'Cafeteria Table 12',
        date_reported: '2026-08-25',
        reported_by: 'Emily Davis',
        contact: '555-0176'
    },
    {
        item_id: 5,
        item_name: 'HP Laptop Charger',
        description: '65W USB-C power adapter found near outlet',
        category: 'Electronics',
        status: 'Found',
        location: 'Engineering Computer Lab B',
        date_reported: '2026-08-31',
        reported_by: 'Professor James',
        contact: '555-0112'
    }
];

let nextMockId = 6;

async function initDB() {
    try {
        dbPool = mysql.createPool(dbConfig);
        const connection = await dbPool.getConnection();
        console.log('Successfully connected to MySQL database: campus_lost_found');
        connection.release();
    } catch (err) {
        console.warn('Notice: MySQL server is not reachable. Using interactive in-memory fallback store.');
        useFallbackStore = true;
    }
}

// REST API Endpoints

// 1. Get Dashboard Statistics
app.get('/api/stats', async (req, res) => {
    try {
        if (!useFallbackStore && dbPool) {
            const [rows] = await dbPool.query('SELECT status, COUNT(*) as count FROM items GROUP BY status');
            let total = 0, lost = 0, found = 0, returned = 0;
            rows.forEach(r => {
                const cnt = Number(r.count);
                total += cnt;
                if (r.status === 'Lost') lost = cnt;
                else if (r.status === 'Found') found = cnt;
                else if (r.status === 'Returned') returned = cnt;
            });
            return res.json({ total, lost, found, returned, mode: 'MySQL' });
        } else {
            let total = mockItems.length;
            let lost = mockItems.filter(i => i.status === 'Lost').length;
            let found = mockItems.filter(i => i.status === 'Found').length;
            let returned = mockItems.filter(i => i.status === 'Returned').length;
            return res.json({ total, lost, found, returned, mode: 'InMemory' });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 2. Get All / Search Items
app.get('/api/items', async (req, res) => {
    const { keyword, category, status, location } = req.query;

    try {
        if (!useFallbackStore && dbPool) {
            let sql = 'SELECT * FROM items WHERE 1=1';
            const params = [];

            if (keyword) {
                sql += ' AND (item_name LIKE ? OR description LIKE ?)';
                params.push(`%${keyword}%`, `%${keyword}%`);
            }
            if (category && category !== 'All Categories') {
                sql += ' AND category = ?';
                params.push(category);
            }
            if (status && status !== 'All Statuses') {
                sql += ' AND status = ?';
                params.push(status);
            }
            if (location) {
                sql += ' AND location LIKE ?';
                params.push(`%${location}%`);
            }

            sql += ' ORDER BY item_id DESC';
            const [rows] = await dbPool.query(sql, params);
            return res.json(rows);
        } else {
            let results = [...mockItems];

            if (keyword) {
                const kw = keyword.toLowerCase();
                results = results.filter(i => 
                    i.item_name.toLowerCase().includes(kw) || 
                    i.description.toLowerCase().includes(kw)
                );
            }
            if (category && category !== 'All Categories') {
                results = results.filter(i => i.category === category);
            }
            if (status && status !== 'All Statuses') {
                results = results.filter(i => i.status === status);
            }
            if (location) {
                const loc = location.toLowerCase();
                results = results.filter(i => i.location.toLowerCase().includes(loc));
            }

            results.sort((a, b) => b.item_id - a.item_id);
            return res.json(results);
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 3. Get Single Item
app.get('/api/items/:id', async (req, res) => {
    const id = parseInt(req.params.id);
    try {
        if (!useFallbackStore && dbPool) {
            const [rows] = await dbPool.query('SELECT * FROM items WHERE item_id = ?', [id]);
            if (rows.length === 0) return res.status(404).json({ error: 'Item not found' });
            return res.json(rows[0]);
        } else {
            const item = mockItems.find(i => i.item_id === id);
            if (!item) return res.status(404).json({ error: 'Item not found' });
            return res.json(item);
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 4. Create New Item (Lost or Found)
app.post('/api/items', async (req, res) => {
    const { item_name, description, category, status, location, date_reported, reported_by, contact } = req.body;

    if (!item_name || !description || !category || !status || !location || !date_reported || !reported_by || !contact) {
        return res.status(400).json({ error: 'All fields are required' });
    }

    try {
        if (!useFallbackStore && dbPool) {
            const sql = `INSERT INTO items (item_name, description, category, status, location, date_reported, reported_by, contact)
                         VALUES (?, ?, ?, ?, ?, ?, ?, ?)`;
            const [result] = await dbPool.query(sql, [item_name, description, category, status, location, date_reported, reported_by, contact]);
            return res.status(201).json({ message: 'Item reported successfully', item_id: result.insertId });
        } else {
            const newItem = {
                item_id: nextMockId++,
                item_name,
                description,
                category,
                status,
                location,
                date_reported,
                reported_by,
                contact
            };
            mockItems.push(newItem);
            return res.status(201).json({ message: 'Item reported successfully', item_id: newItem.item_id });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 5. Update Item
app.put('/api/items/:id', async (req, res) => {
    const id = parseInt(req.params.id);
    const { item_name, description, category, status, location, date_reported, reported_by, contact } = req.body;

    try {
        if (!useFallbackStore && dbPool) {
            const sql = `UPDATE items SET item_name=?, description=?, category=?, status=?, location=?, date_reported=?, reported_by=?, contact=?
                         WHERE item_id=?`;
            const [result] = await dbPool.query(sql, [item_name, description, category, status, location, date_reported, reported_by, contact, id]);
            if (result.affectedRows === 0) return res.status(404).json({ error: 'Item not found' });
            return res.json({ message: 'Item updated successfully' });
        } else {
            const index = mockItems.findIndex(i => i.item_id === id);
            if (index === -1) return res.status(404).json({ error: 'Item not found' });
            mockItems[index] = { item_id: id, item_name, description, category, status, location, date_reported, reported_by, contact };
            return res.json({ message: 'Item updated successfully' });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 6. Mark Item as Returned
app.put('/api/items/:id/return', async (req, res) => {
    const id = parseInt(req.params.id);

    try {
        if (!useFallbackStore && dbPool) {
            const [result] = await dbPool.query("UPDATE items SET status = 'Returned' WHERE item_id = ?", [id]);
            if (result.affectedRows === 0) return res.status(404).json({ error: 'Item not found' });
            return res.json({ message: 'Item marked as Returned' });
        } else {
            const item = mockItems.find(i => i.item_id === id);
            if (!item) return res.status(404).json({ error: 'Item not found' });
            item.status = 'Returned';
            return res.json({ message: 'Item marked as Returned' });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 7. Delete Item
app.delete('/api/items/:id', async (req, res) => {
    const id = parseInt(req.params.id);

    try {
        if (!useFallbackStore && dbPool) {
            const [result] = await dbPool.query('DELETE FROM items WHERE item_id = ?', [id]);
            if (result.affectedRows === 0) return res.status(404).json({ error: 'Item not found' });
            return res.json({ message: 'Item deleted successfully' });
        } else {
            const index = mockItems.findIndex(i => i.item_id === id);
            if (index === -1) return res.status(404).json({ error: 'Item not found' });
            mockItems.splice(index, 1);
            return res.json({ message: 'Item deleted successfully' });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

initDB().then(() => {
    app.listen(PORT, () => {
        console.log(`============================================================`);
        console.log(` Campus Lost & Found Management System Web Application`);
        console.log(` Running on Localhost: http://localhost:${PORT}`);
        console.log(`============================================================`);
    });
});
