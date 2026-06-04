const cron = require('node-cron');
const User = require('../models/User');
const ScrollLog = require('../models/ScrollLog');
const { sendDailyDigest, sendWeeklyReport } = require('./whatsappService');

// Daily digest — every day 8AM IST
const startDailyDigest = () => {
  cron.schedule('0 8 * * *', async () => {
    console.log('Running daily digest...');
    try {
      const yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);
      const yesterdayStr = yesterday.toISOString().split('T')[0];

      const users = await User.find({
        whatsappEnabled: true,
        plan: { $ne: 'free' }
      });

      for (const user of users) {
        const log = await ScrollLog.findOne({
          userId: user._id,
          date: yesterdayStr
        });

        const yesterdayCount = log?.totalCount || 0;
        await sendDailyDigest(user.whatsappPhone, yesterdayCount, user.dailyLimit);
      }

      console.log(`Digest sent to ${users.length} users`);
    } catch (error) {
      console.error('Digest error:', error.message);
    }
  }, { timezone: 'Asia/Kolkata' });
};

// Weekly report — every Sunday 9AM IST
const startWeeklyReport = () => {
  cron.schedule('0 9 * * 0', async () => {
    console.log('Running weekly report...');
    try {
      const days = [];
      for (let i = 6; i >= 0; i--) {
        const d = new Date();
        d.setDate(d.getDate() - i);
        days.push(d.toISOString().split('T')[0]);
      }

      const users = await User.find({
        whatsappEnabled: true,
        plan: { $ne: 'free' }
      });

      for (const user of users) {
        const logs = await ScrollLog.find({
          userId: user._id,
          date: { $in: days }
        });

        if (logs.length > 0) {
          await sendWeeklyReport(user.whatsappPhone, logs);
        }
      }

      console.log(`Weekly report sent to ${users.length} users`);
    } catch (error) {
      console.error('Weekly report error:', error.message);
    }
  }, { timezone: 'Asia/Kolkata' });
};

module.exports = { startDailyDigest, startWeeklyReport };
