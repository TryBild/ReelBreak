require('dotenv').config();
const app = require('./app');
const connectDB = require('./config/db');
const { startDailyDigest, startWeeklyReport } = require('./services/cronService');

const PORT = process.env.PORT || 3000;

const start = async () => {
  await connectDB();
  // Start cron jobs
  startDailyDigest();
  startWeeklyReport();
  app.listen(PORT, () => {
    console.log(`🧠 ReelBreak API running on port ${PORT}`);
  });
};

start();