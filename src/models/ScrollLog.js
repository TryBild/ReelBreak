const mongoose = require('mongoose');

const scrollLogSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  date: { type: String, required: true },
  counts: {
    instagram: { type: Number, default: 0 },
    youtube: { type: Number, default: 0 },
    facebook: { type: Number, default: 0 },
    snapchat: { type: Number, default: 0 }
  },
  totalCount: { type: Number, default: 0 },
  limitHit: { type: Boolean, default: false },
  limitHitAt: { type: Date }
}, { timestamps: true });

// Compound index — one log per user per day
scrollLogSchema.index({ userId: 1, date: 1 }, { unique: true });

module.exports = mongoose.model('ScrollLog', scrollLogSchema);