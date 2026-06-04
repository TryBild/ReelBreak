const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  phone: { type: String, required: true, unique: true, trim: true },
  name: { type: String, trim: true },
  plan: { type: String, enum: ['free', 'pro_monthly', 'pro_yearly'], default: 'free' },
  planExpiresAt: { type: Date },
  dailyLimit: { type: Number, default: 100 },
  whatsappEnabled: { type: Boolean, default: false },
  whatsappPhone: { type: String },
  timezone: { type: String, default: 'Asia/Kolkata' },
  isActive: { type: Boolean, default: true }
}, { timestamps: true });

module.exports = mongoose.model('User', userSchema);
