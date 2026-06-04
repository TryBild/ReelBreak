const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/auth');
const User = require('../models/User');

// Get profile
router.get('/profile', protect, async (req, res) => {
  res.status(200).json({ success: true, data: req.user });
});

// Update WhatsApp settings
router.put('/whatsapp', protect, async (req, res) => {
  try {
    const { whatsappEnabled, whatsappPhone } = req.body;
    await User.findByIdAndUpdate(req.user._id, { whatsappEnabled, whatsappPhone });
    res.status(200).json({ success: true, message: 'WhatsApp settings updated' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// Update name
router.put('/name', protect, async (req, res) => {
  try {
    const { name } = req.body;
    await User.findByIdAndUpdate(req.user._id, { name });
    res.status(200).json({ success: true, message: 'Name updated' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

module.exports = router;