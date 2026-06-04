const express = require('express');
const router = express.Router();
const { syncCount, getToday, getWeekly, updateLimit } = require('../controllers/scrollController');
const { protect, isPro } = require('../middleware/auth');

router.post('/sync', protect, syncCount);
router.get('/today', protect, getToday);
router.get('/weekly', protect, isPro, getWeekly);
router.put('/limit', protect, updateLimit);

module.exports = router;
