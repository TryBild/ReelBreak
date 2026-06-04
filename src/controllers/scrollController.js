const ScrollLog = require('../models/ScrollLog');
const User = require('../models/User');
const { sendLimitAlert } = require('../services/whatsappService');

// Sync scroll count from Android app
const syncCount = async (req, res) => {
  try {
    const { date, counts } = req.body;
    const userId = req.user._id;

    const totalCount = (counts.instagram || 0) + (counts.youtube || 0) +
      (counts.facebook || 0) + (counts.snapchat || 0);

    let log = await ScrollLog.findOneAndUpdate(
      { userId, date },
      { counts, totalCount },
      { upsert: true, new: true }
    );

    const user = req.user;

    // 80% limit alert
    if (totalCount >= user.dailyLimit * 0.8 && totalCount < user.dailyLimit) {
      if (user.whatsappEnabled) {
        await sendLimitAlert(user.whatsappPhone, totalCount, user.dailyLimit, '80%');
      }
    }

    // Hard limit hit
    if (totalCount >= user.dailyLimit && !log.limitHit) {
      log.limitHit = true;
      log.limitHitAt = new Date();
      await log.save();

      if (user.whatsappEnabled) {
        await sendLimitAlert(user.whatsappPhone, totalCount, user.dailyLimit, 'blocked');
      }
    }

    res.status(200).json({
      success: true,
      totalCount,
      limitHit: log.limitHit,
      dailyLimit: user.dailyLimit
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// Get today's count
const getToday = async (req, res) => {
  try {
    const today = new Date().toISOString().split('T')[0];
    const log = await ScrollLog.findOne({ userId: req.user._id, date: today });

    res.status(200).json({
      success: true,
      data: log || { counts: { instagram: 0, youtube: 0, facebook: 0, snapchat: 0 }, totalCount: 0 }
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// Get weekly report
const getWeekly = async (req, res) => {
  try {
    const days = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      days.push(d.toISOString().split('T')[0]);
    }

    const logs = await ScrollLog.find({
      userId: req.user._id,
      date: { $in: days }
    });

    res.status(200).json({ success: true, data: logs, days });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// Update daily limit
const updateLimit = async (req, res) => {
  try {
    const { dailyLimit } = req.body;

    if (!dailyLimit || dailyLimit < 1) {
      return res.status(400).json({ success: false, message: 'Invalid limit' });
    }

    await User.findByIdAndUpdate(req.user._id, { dailyLimit });

    res.status(200).json({ success: true, message: 'Limit updated', dailyLimit });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

module.exports = { syncCount, getToday, getWeekly, updateLimit };
