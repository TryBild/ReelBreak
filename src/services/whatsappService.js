const axios = require('axios');

const sendMessage = async (phone, message) => {
  try {
    await axios.post(
      `https://graph.facebook.com/v18.0/${process.env.WHATSAPP_PHONE_ID}/messages`,
      {
        messaging_product: 'whatsapp',
        to: phone,
        type: 'text',
        text: { body: message }
      },
      {
        headers: {
          Authorization: `Bearer ${process.env.WHATSAPP_TOKEN}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return true;
  } catch (error) {
    console.error('WhatsApp error:', error.message);
    return false;
  }
};

// Daily digest — sent at 8AM
const sendDailyDigest = async (phone, yesterdayCount, todayGoal) => {
  const message = `🧠 *ReelBreak Daily Report*\n\nKal aapne *${yesterdayCount} reels* dekhe.\nAaj ka goal: *${todayGoal} reels*\n\nAaj kam karo. Apna time bachao! 💪\n\n_ReelBreak — Break the Reel. Reclaim Your Mind._`;
  return sendMessage(phone, message);
};

// 80% or blocked alert
const sendLimitAlert = async (phone, currentCount, limit, type) => {
  let message;

  if (type === '80%') {
    message = `⚠️ *ReelBreak Alert*\n\nAapne aaj *${currentCount}/${limit} reels* dekh liye!\nLimit ke 80% pe pahunch gaye.\n\nPhone rakh do ab! 🙏`;
  } else {
    message = `🚫 *ReelBreak — Limit Hit!*\n\nAapne aaj ka limit (*${limit} reels*) complete kar liya!\nApp ab block ho jayega.\n\nKal fresh start! 💪`;
  }

  return sendMessage(phone, message);
};

// Weekly report — sent on Sunday
const sendWeeklyReport = async (phone, weekData) => {
  const total = weekData.reduce((sum, day) => sum + day.totalCount, 0);
  const avg = Math.round(total / 7);
  const best = Math.min(...weekData.map(d => d.totalCount));

  const message = `📊 *ReelBreak Weekly Report*\n\nIs hafte:\n• Kul reels: *${total}*\n• Daily average: *${avg}*\n• Best day: *${best} reels*\n\nAgla hafte aur better karo! 🎯\n\n_ReelBreak — Break the Reel. Reclaim Your Mind._`;
  return sendMessage(phone, message);
};

module.exports = { sendDailyDigest, sendLimitAlert, sendWeeklyReport };
